# Libbox 构建指南

## 前置要求

### 1. 安装 Go
```bash
# macOS
brew install go

# Linux
sudo apt-get install golang-go

# Windows
# 下载并安装: https://golang.org/dl/
```

### 2. 安装 Android NDK
```bash
# 通过 Android Studio 安装
# SDK Manager -> SDK Tools -> NDK (Side by side)

# 或者命令行
sdkmanager "ndk;25.2.9519653"
```

### 3. 设置环境变量
```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
export ANDROID_HOME=$HOME/Library/Android/sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/25.2.9519653
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_NDK_HOME
```

## 构建步骤

### 方法一: 使用构建脚本
```bash
cd sing-box-android
./build_libbox.sh
```

### 方法二: 手动构建
```bash
# 1. 安装 gomobile
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest

# 2. 初始化 gomobile
gomobile init

# 3. 克隆 sing-box
git clone https://github.com/SagerNet/sing-box.git
cd sing-box

# 4. 切换到稳定版本
git checkout v1.8.0

# 5. 构建 Android 库
gomobile bind -v -androidapi 26 \
    -javapkg=io.nekohasekai.sagernet \
    -o ../app/libs/libbox.aar \
    ./experimental/libbox

cd ..
```

### 方法三: 使用预编译版本
如果不想自己编译，可以从以下地址下载预编译版本：

1. GitHub Releases: https://github.com/SagerNet/sing-box/releases
2. 或者使用其他项目提供的预编译版本

下载后将 `libbox.aar` 放到 `app/libs/` 目录。

## 验证构建

构建完成后，检查文件：
```bash
ls -lh app/libs/libbox.aar
# 应该显示类似: -rw-r--r--  1 user  staff   5.2M  date libbox.aar
```

## 集成到项目

构建脚本已经自动将 libbox.aar 放置到正确位置。现在需要更新 build.gradle.kts：

```kotlin
// app/build.gradle.kts
dependencies {
    // Libbox
    implementation(files("libs/libbox.aar"))
    
    // 其他依赖...
}
```

## 使用 Libbox API

### 初始化
```kotlin
import io.nekohasekai.sagernet.Libbox

// 在 Application 或 Service 中初始化
Libbox.boxSetup(
    basePath,      // 配置文件基础路径
    workingPath,   // 工作目录路径
    debugMode      // 是否为调试模式
)
```

### 启动 VPN
```kotlin
// 创建 sing-box 实例
val boxService = Libbox.newService(
    configContent,  // JSON 配置内容
    platformInterface // 平台接口实现
)

// 启动服务
boxService.start()

// 停止服务
boxService.stop()
```

### 平台接口
```kotlin
class AndroidPlatformInterface : LibboxInterface {
    override fun openTun(fd: Int, mtu: Int): Boolean {
        // 打开 TUN 设备
        return true
    }
    
    override fun writePacket(data: ByteArray): Boolean {
        // 写入数据包
        return true
    }
    
    override fun logMessage(message: String) {
        // 处理日志消息
    }
    
    override fun notifySpeedUpdate(upload: Long, download: Long) {
        // 更新速度统计
    }
}
```

## 常见问题

### 1. 编译失败
- 确保 Go 版本 >= 1.20
- 确保 Android NDK 已安装
- 确保环境变量设置正确

### 2. 运行时崩溃
- 检查 minSdkVersion >= 26
- 检查 ABI 兼容性 (arm64-v8a, armeabi-v7a, x86_64)

### 3. 性能问题
- 使用 Release 构建
- 启用 ProGuard/R8 优化

## 参考资源

- [sing-box 文档](https://sing-box.sagernet.org/)
- [gomobile 文档](https://pkg.go.dev/golang.org/x/mobile)
- [sing-box-for-android](https://github.com/SagerNet/sing-box-for-android)
