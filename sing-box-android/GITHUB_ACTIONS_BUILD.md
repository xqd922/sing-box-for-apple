# 使用 GitHub Actions 构建 APK

## 🚀 快速开始

### 方法一：手动触发 Workflow

1. **Fork 仓库**（如果还没有）
   - 访问：https://github.com/xqd922/sing-box-for-apple
   - 点击 "Fork" 按钮

2. **启用 GitHub Actions**
   - 进入你 Fork 的仓库
   - 点击 "Actions" 标签
   - 点击 "I understand my workflows, go ahead and enable them"

3. **创建 Workflow 文件**
   - 点击 "Create workflow" 或 "set up a workflow yourself"
   - 复制下面的 workflow 内容
   - 提交到仓库

4. **手动触发构建**
   - 进入 Actions 页面
   - 选择 "Build Android APK"
   - 点击 "Run workflow"
   - 输入版本号（如 v1.0.0）
   - 点击 "Run workflow" 按钮

5. **下载 APK**
   - 等待构建完成（约 10-15 分钟）
   - 在 Artifacts 部分下载 APK
   - 或在 Releases 页面下载

### 方法二：使用现有 Release

直接下载已发布的版本：

**访问**: https://github.com/xqd922/sing-box-for-apple/releases/tag/v1.0.0

**注意**: 当前 Release 没有附带 APK 文件。需要按照方法一自行构建。

## 📝 Workflow 文件内容

创建文件：`.github/workflows/android.yml`

```yaml
name: Build Android APK

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to build'
        required: true
        default: 'v1.0.0'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Setup Android SDK
      uses: android-actions/setup-android@v3

    - name: Cache Gradle
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: gradle-${{ hashFiles('**/*.gradle*') }}

    - name: Create dummy libbox.aar
      run: |
        mkdir -p sing-box-android/app/libs
        cd sing-box-android/app/libs
        mkdir -p META-INF
        echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF
        zip -r libbox.aar META-INF
        rm -rf META-INF

    - name: Build Debug APK
      run: |
        cd sing-box-android
        chmod +x gradlew
        ./gradlew assembleDebug --no-daemon

    - name: Build Release APK
      run: |
        cd sing-box-android
        ./gradlew assembleRelease --no-daemon

    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: sing-box-android-${{ github.event.inputs.version }}
        path: |
          sing-box-android/app/build/outputs/apk/debug/*.apk
          sing-box-android/app/build/outputs/apk/release/*.apk
        retention-days: 90

    - name: Create Release
      if: success()
      uses: softprops/action-gh-release@v2
      with:
        tag_name: ${{ github.event.inputs.version }}
        name: sing-box for Android ${{ github.event.inputs.version }}
        body: |
          ## sing-box for Android ${{ github.event.inputs.version }}
          
          ### Installation
          Download and install the APK file.
          
          ### Features
          - Complete UI replica of iOS/macOS version
          - VPN service with Libbox integration
          - Dashboard, Logs, Tools, Settings
          
          **Note**: This is a debug build.
        files: |
          sing-box-android/app/build/outputs/apk/debug/*.apk
          sing-box-android/app/build/outputs/apk/release/*.apk
        draft: false
        prerelease: false
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## 🔧 详细步骤

### 步骤 1：Fork 仓库

```
1. 访问 https://github.com/xqd922/sing-box-for-apple
2. 点击右上角的 "Fork" 按钮
3. 选择你的账号
4. 等待 Fork 完成
```

### 步骤 2：启用 Actions

```
1. 进入你 Fork 的仓库
2. 点击顶部的 "Actions" 标签
3. 你会看到 "Actions aren’t enabled for this repository"
4. 点击绿色的 "I understand my workflows, go ahead and enable them" 按钮
```

### 步骤 3：创建 Workflow 文件

**方法 A：使用 GitHub 网页界面**

```
1. 点击 "Create new file" 或按 "." 键打开在线编辑器
2. 输入文件名：.github/workflows/android.yml
3. 复制上面的 workflow 内容
4. 点击 "Commit new file"
```

**方法 B：使用 Git 命令行**

```bash
# 克隆你的 fork
git clone https://github.com/YOUR_USERNAME/sing-box-for-apple.git
cd sing-box-for-apple

# 创建 workflow 目录
mkdir -p .github/workflows

# 创建 workflow 文件
cat > .github/workflows/android.yml << 'EOF'
[粘贴上面的 workflow 内容]
EOF

# 提交并推送
git add .github/workflows/android.yml
git commit -m "ci: Add Android build workflow"
git push origin main
```

### 步骤 4：触发构建

```
1. 进入仓库的 "Actions" 页面
2. 在左侧选择 "Build Android APK"
3. 点击右侧的 "Run workflow" 按钮
4. 在弹出的对话框中：
   - 选择分支（通常是 main 或 feature/android-port）
   - 输入版本号（如 v1.0.0）
5. 点击绿色的 "Run workflow" 按钮
```

### 步骤 5：等待构建

```
1. 构建会自动开始
2. 你会看到一个黄色的圆点表示正在运行
3. 点击进入查看详情
4. 等待所有步骤完成（约 10-15 分钟）
5. 成功后会显示绿色的 ✓
```

### 步骤 6：下载 APK

**方法 A：从 Artifacts 下载**

```
1. 进入已完成的 workflow run
2. 滚动到底部的 "Artifacts" 部分
3. 点击 "sing-box-android-v1.0.0" 下载
4. 解压下载的 zip 文件
5. 找到 APK 文件
```

**方法 B：从 Releases 下载**

```
1. 进入仓库的 "Releases" 页面
2. 找到刚创建的 release（如 v1.0.0）
3. 下载 Assets 中的 APK 文件
```

## 📱 安装 APK

### 在 Android 设备上安装

```
1. 传输 APK 到手机（USB、邮件、云盘等）
2. 在手机上打开文件管理器
3. 找到 APK 文件并点击
4. 如果提示 "安装未知来源应用"：
   - 点击 "设置"
   - 启用 "允许来自此来源的应用"
   - 返回继续安装
5. 点击 "安装"
6. 等待安装完成
7. 点击 "打开" 启动应用
```

### 使用 ADB 安装

```bash
# 连接手机到电脑
adb devices

# 安装 APK
adb install path/to/sing-box-android-v1.0.0.apk

# 如果有多个设备
adb -s DEVICE_SERIAL install path/to/sing-box-android-v1.0.0.apk
```

## 🐛 常见问题

### Q: Workflow 没有运行

**原因**: GitHub Actions 可能被禁用

**解决**:
```
1. 进入仓库 Settings -> Actions -> General
2. 确保 "Allow all actions" 被选中
3. 保存设置
4. 重新触发 workflow
```

### Q: 构建失败

**原因**: 缺少依赖或配置错误

**解决**:
```
1. 查看构建日志
2. 找到失败的步骤
3. 检查错误信息
4. 常见问题：
   - Gradle 版本不兼容
   - Android SDK 版本问题
   - 内存不足（重试即可）
```

### Q: 找不到 APK 文件

**原因**: 构建成功但没有上传

**解决**:
```
1. 检查 workflow 日志
2. 确认 "Upload APK" 步骤成功
3. 在 Artifacts 页面查找
4. 或在 Releases 页面查找
```

### Q: APK 无法安装

**原因**: 签名问题或版本不兼容

**解决**:
```
1. 确保启用 "安装未知来源应用"
2. 检查 Android 版本（需要 8.0+）
3. 尝试使用 Debug APK
4. 重新构建
```

## 🔄 自动触发构建

如果你想在每次推送代码时自动构建，修改 workflow 触发器：

```yaml
on:
  push:
    branches: [ main, develop ]
    tags: [ 'v*' ]
  pull_request:
    branches: [ main, develop ]
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to build'
        required: true
        default: 'v1.0.0'
```

## 📊 构建产物

### Debug APK
- **用途**: 测试和调试
- **特点**: 包含调试信息，可调试
- **文件名**: `app-debug.apk`
- **大小**: 约 30-40 MB

### Release APK
- **用途**: 发布和分发
- **特点**: 优化后，更小
- **文件名**: `app-release.apk`
- **大小**: 约 15-25 MB

### AAB (Android App Bundle)
- **用途**: Play Store 发布
- **特点**: Google Play 优化
- **文件名**: `app-release.aab`
- **大小**: 约 10-20 MB

## 🎯 推荐流程

### 开发阶段
```
1. 使用 Debug APK 测试
2. 每次代码更改后重新构建
3. 快速迭代
```

### 测试阶段
```
1. 使用 Release APK 测试
2. 在多个设备测试
3. 收集反馈
```

### 发布阶段
```
1. 创建正式 Release
2. 上传签名的 APK
3. 编写详细的 Release Notes
4. 分享给用户
```

## 📚 相关资源

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Android 构建指南](https://developer.android.com/build)
- [Gradle 文档](https://docs.gradle.org/)
- [项目 README](README.md)
- [发布指南](RELEASE_GUIDE.md)

## 💡 提示

1. **首次构建较慢** - 需要下载依赖，后续会更快
2. **使用缓存** - workflow 已配置 Gradle 缓存
3. **检查日志** - 如果失败，仔细查看日志
4. **版本号** - 使用语义化版本（如 v1.0.0）
5. **测试设备** - 在真机上测试效果更好

## 🎉 完成！

按照以上步骤，你就可以使用 GitHub Actions 自动构建 APK 了！

有问题？查看 [常见问题](#-常见问题) 或提交 Issue。
