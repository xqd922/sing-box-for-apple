param(
    [switch]$NoTests = $false,
    [switch]$Bundle = $true,
    [switch]$SkipRelease = $false
)

$ErrorActionPreference = "Stop"

function Get-JavaHome {
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        return $env:JAVA_HOME
    }

    $candidates = @(
        "$env:ProgramFiles\Eclipse Adoptium\jdk-17*",
        "$env:ProgramFiles\Microsoft\jdk-17*",
        "$env:ProgramFiles\Eclipse Adoptium\jdk-1*"
    )

    foreach ($pattern in $candidates) {
        $dirs = Get-ChildItem -Directory -Path (Split-Path $pattern) -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -like (Split-Path $pattern -Leaf) } |
            Sort-Object Name -Descending
        if ($dirs.Count -gt 0) {
            $candidate = $dirs[0].FullName
            if (Test-Path "$candidate\bin\java.exe") {
                return $candidate
            }
        }
    }

    throw "JAVA_HOME not found and Java 17 JDK is not auto-detected. Install Eclipse Temurin JDK 17 and retry."
}

function Assert-JavaVersion {
    param([string]$JavaHome)

    $env:JAVA_HOME = $JavaHome
    $env:Path = "$JavaHome\bin;$env:Path"

    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = "$JavaHome\bin\java.exe"
    $psi.Arguments = "-version"
    $psi.UseShellExecute = $false
    $psi.RedirectStandardError = $true
    $psi.RedirectStandardOutput = $true
    $psi.CreateNoWindow = $true

    $proc = [System.Diagnostics.Process]::Start($psi)
    $stderr = $proc.StandardError.ReadToEnd()
    $stdout = $proc.StandardOutput.ReadToEnd()
    $proc.WaitForExit()
    if ($proc.ExitCode -ne 0) {
        throw "Unable to execute java -version (exit code $($proc.ExitCode))"
    }
    $verOutput = "$stdout`n$stderr"

    if ($verOutput -notmatch '(\d+)(?:\.\d+)*') {
        throw "Unable to parse java version from: $verOutput"
    }
    $major = [int]$matches[1]
    if ($major -lt 17) {
        throw "Java 17+ is required, current major version is $major."
    }
}

function Get-AndroidSdk {
    $candidates = @(
        $env:ANDROID_HOME,
        $env:ANDROID_SDK_ROOT,
        "$env:LOCALAPPDATA\Android\Sdk",
        "$env:APPDATA\Local\Android\Sdk",
        "$env:USERPROFILE\AppData\Local\Android\Sdk",
        "$env:ProgramFiles\Android\Android Studio\Sdk",
        "${env:ProgramFiles(x86)}\Android\Android Studio\Sdk",
        "C:\Android\Sdk",
        "C:\Sdk"
    ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique

    foreach ($candidate in $candidates) {
        if (
            (Test-Path (Join-Path $candidate "platform-tools")) -or
            (Test-Path (Join-Path $candidate "cmdline-tools")) -or
            (Test-Path (Join-Path $candidate "build-tools"))
        ) {
            return $candidate
        }
    }

    throw "Android SDK not found. Set ANDROID_HOME (or ANDROID_SDK_ROOT) to a valid SDK path, then rerun.
Install: winget install --id Google.AndroidStudio -e"
}

function Ensure-LocalProperties {
    param([string]$ProjectDir, [string]$SdkPath)
    $propFile = Join-Path $ProjectDir "local.properties"
    "sdk.dir=$SdkPath" | Out-File -FilePath $propFile -Encoding UTF8 -NoNewline
}

function Ensure-DummyLibbox {
    param([string]$ProjectDir)
    $aarPath = Join-Path $ProjectDir "app\libs\libbox.aar"
    if (Test-Path $aarPath) {
        return
    }

    $tmp = Join-Path $ProjectDir "app\libs\.tmp_libbox_aar"
    New-Item -ItemType Directory -Path (Join-Path $tmp "META-INF") -Force | Out-Null
    "Manifest-Version: 1.0" | Set-Content -Encoding ASCII (Join-Path $tmp "META-INF\MANIFEST.MF")
    Compress-Archive -Path (Join-Path $tmp "META-INF") -DestinationPath "$aarPath.tmp" -Force
    Rename-Item "$aarPath.tmp" $aarPath -Force
    Remove-Item -Recurse -Force $tmp
}

function Invoke-Gradle {
    param([string[]]$Tasks, [string]$ProjectDir)
    $gradlew = Join-Path $ProjectDir "gradlew.bat"
    if (-not (Test-Path $gradlew)) {
        throw "gradlew.bat not found: $gradlew"
    }

    Push-Location $ProjectDir
    try {
        & $gradlew $Tasks --no-daemon
        if ($LASTEXITCODE -ne 0) {
            throw "Gradle failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

$projectDir = Split-Path -Parent (Resolve-Path $MyInvocation.MyCommand.Path)
$javaHome = Get-JavaHome
Assert-JavaVersion $javaHome

$sdkPath = Get-AndroidSdk
Ensure-LocalProperties $projectDir $sdkPath

Ensure-DummyLibbox $projectDir

$tasks = @("clean", "assembleDebug")
if (-not $SkipRelease) {
    $tasks += "assembleRelease"
    if ($Bundle) {
        $tasks += "bundleRelease"
    }
}
if (-not $NoTests) {
    $tasks = @("test") + $tasks
}

Invoke-Gradle $tasks $projectDir

Write-Host "Release artifacts:" -ForegroundColor Green
Get-ChildItem -Recurse -Path (Join-Path $projectDir "app\build\outputs") `
    -Include "*.apk", "*.aab" -ErrorAction SilentlyContinue |
    ForEach-Object { Write-Host " - $($_.FullName)" }
