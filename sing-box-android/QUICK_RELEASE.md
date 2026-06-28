# 快速发版指南

## 🚀 立即发版

### 方法一：使用构建脚本（推荐）

```bash
cd sing-box-android
./build_and_release.sh
```

脚本会自动：
1. ✅ 检查依赖
2. ✅ 清理旧构建
3. ✅ 运行测试
4. ✅ 构建 Debug APK
5. ✅ 构建 Release APK
6. ✅ 构建 Release AAB
7. ✅ 创建发布包

构建完成后，在 `release/v1.0.0/` 目录找到所有文件。

### 方法二：手动构建

```bash
cd sing-box-android

# 构建 Release APK
./gradlew assembleRelease

# 构建 AAB (用于 Play Store)
./gradlew bundleRelease
```

输出文件：
- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/bundle/release/app-release.aab`

## 📦 发布到 GitHub

### 使用 GitHub CLI（最简单）

```bash
# 在仓库根目录执行
gh release create v1.0.0 \
  --title "sing-box for Android v1.0.0" \
  --notes "Initial release of sing-box for Android" \
  sing-box-android/release/v1.0.0/*
```

### 手动发布

1. 访问：https://github.com/xqd922/sing-box-for-apple/releases/new
2. Tag: `v1.0.0`
3. Title: `sing-box for Android v1.0.0`
4. 描述：复制 `CHANGELOG.md` 内容
5. 上传文件：
   - `sing-box-android-v1.0.0.apk`
   - `sing-box-android-v1.0.0.aab`
6. 点击 "Publish release"

## 📱 测试 APK

```bash
# 安装到设备
adb install sing-box-android/release/v1.0.0/sing-box-android-v1.0.0.apk

# 或者直接传输到手机
# 文件位置: sing-box-android/release/v1.0.0/sing-box-android-v1.0.0.apk
```

## 🏪 发布到 Play Store

1. 登录 [Google Play Console](https://play.google.com/console)
2. 创建新应用或选择现有应用
3. 进入 "Release" -> "Production"
4. 点击 "Create new release"
5. 上传 `sing-box-android-v1.0.0.aab`
6. 填写 release notes
7. 提交审核

## 🔧 签名配置（可选）

如果你想发布签名版本：

### 1. 生成 Keystore
```bash
keytool -genkey -v -keystore sing-box.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias sing-box
```

### 2. 配置签名
编辑 `app/build.gradle.kts`：
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("sing-box.jks")
            storePassword = "your_password"
            keyAlias = "sing-box"
            keyPassword = "your_key_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. 重新构建
```bash
./gradlew assembleRelease
```

## 📋 发布检查清单

### 发布前
- [ ] 所有测试通过
- [ ] APK 大小合理（< 50MB）
- [ ] 已在真机测试
- [ ] 版本号正确（v1.0.0）

### 发布时
- [ ] GitHub Release 已创建
- [ ] Release notes 已填写
- [ ] APK 已上传
- [ ] AAB 已上传

### 发布后
- [ ] 测试下载链接
- [ ] 分享到社交媒体
- [ ] 监控 Issues
- [ ] 收集反馈

## 🎯 快速命令

### 完整流程（一键）
```bash
cd sing-box-android
./build_and_release.sh
gh release create v1.0.0 \
  --title "sing-box for Android v1.0.0" \
  --notes-file CHANGELOG.md \
  release/v1.0.0/*
```

### 仅构建
```bash
cd sing-box-android
./gradlew assembleRelease
```

### 仅发布
```bash
gh release create v1.0.0 \
  --title "sing-box for Android v1.0.0" \
  sing-box-android/app/build/outputs/apk/release/*.apk
```

## 📊 文件大小参考

典型文件大小：
- Debug APK: ~30-40 MB
- Release APK: ~15-25 MB
- Release AAB: ~10-20 MB

## ⚠️ 注意事项

### 1. Libbox 依赖
发布前必须确保 `libbox.aar` 存在：
```bash
ls -lh app/libs/libbox.aar
```

### 2. 签名
未签名 APK 需要用户开启 "安装未知来源应用"

### 3. 版本号
确保 `app/build.gradle.kts` 中版本号正确：
```kotlin
versionCode = 1
versionName = "1.0.0"
```

## 🐛 常见问题

### Q: 构建失败
A: 清理后重试
```bash
./gradlew clean
./gradlew assembleRelease
```

### Q: 找不到 APK
A: 检查构建输出
```bash
find . -name "*.apk" -type f
```

### Q: Libbox 缺失
A: 按照 BUILD_LIBBOX.md 构建或下载预编译版本

## 📚 相关文档

- [README.md](README.md) - 项目说明
- [BUILD_LIBBOX.md](BUILD_LIBBOX.md) - Libbox 构建指南
- [RELEASE_GUIDE.md](RELEASE_GUIDE.md) - 详细发布指南
- [CHANGELOG.md](CHANGELOG.md) - 版本历史

## 🎉 完成！

执行以上步骤后，你的应用就会发布到 GitHub Releases！

用户可以：
1. ✅ 下载 APK 安装
2. ✅ 查看源代码
3. ✅ 提交 Issues
4. ✅ 贡献代码

祝你发布顺利！🚀
