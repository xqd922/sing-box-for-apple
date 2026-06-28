# sing-box Android 发布指南

## 发布前准备

### 1. 确保代码质量
```bash
# 运行所有测试
./gradlew test

# 检查代码风格
./gradlew ktlintCheck

# 运行 lint 检查
./gradlew lint
```

### 2. 更新版本号
编辑 `app/build.gradle.kts`：
```kotlin
android {
    defaultConfig {
        versionCode = 2  // 递增版本号
        versionName = "1.1.0"  // 更新版本名
    }
}
```

### 3. 更新 CHANGELOG.md
创建或更新 CHANGELOG.md，记录所有更改。

## 构建发布版本

### 方法一：使用构建脚本（推荐）
```bash
cd sing-box-android
./build_release.sh
```

### 方法二：手动构建
```bash
# 清理
./gradlew clean

# 构建 Release APK
./gradlew assembleRelease

# 构建 AAB (用于 Play Store)
./gradlew bundleRelease
```

### 方法三：使用 Android Studio
1. Build -> Generate Signed Bundle / APK
2. 选择 Android App Bundle 或 APK
3. 配置签名
4. 选择 release 构建类型
5. 点击 Finish

## 签名配置

### 1. 生成 Keystore
```bash
keytool -genkey -v -keystore sing-box.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias sing-box
```

### 2. 配置签名
在 `app/build.gradle.kts` 中添加：
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("sing-box.jks")
            storePassword = "your_store_password"
            keyAlias = "sing-box"
            keyPassword = "your_key_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}
```

### 3. 使用环境变量（更安全）
```bash
export KEYSTORE_PASSWORD=your_password
export KEY_PASSWORD=your_key_password

./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=sing-box.jks \
  -Pandroid.injected.signing.store.password=$KEYSTORE_PASSWORD \
  -Pandroid.injected.signing.key.alias=sing-box \
  -Pandroid.injected.signing.key.password=$KEY_PASSWORD
```

## 发布到 GitHub

### 1. 创建 Git Tag
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### 2. 创建 GitHub Release
1. 访问 GitHub 仓库页面
2. 点击 "Releases"
3. 点击 "Draft a new release"
4. 选择刚创建的 tag
5. 填写 release notes
6. 上传 APK 和 AAB 文件
7. 点击 "Publish release"

### 3. 自动发布（使用 GitHub Actions）
推送 tag 后会自动触发构建和发布：
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## 发布到 Google Play Store

### 1. 准备 Play Store 列表
- 应用名称：sing-box
- 简短描述：Universal proxy platform client
- 完整描述：从 README.md 复制
- 截图：准备手机和平板截图
- 功能图片：1024x500 像素

### 2. 上传 AAB
1. 登录 Google Play Console
2. 选择应用
3. 进入 "Release" -> "Production"
4. 点击 "Create new release"
5. 上传 AAB 文件
6. 填写 release notes
7. 选择发布范围
8. 点击 "Review and rollout"

### 3. 配置应用签名
在 Google Play Console 中：
1. 进入 "Setup" -> "App signing"
2. 选择 "Use Google-managed key"
3. 或者上传自己的签名密钥

## 发布到其他平台

### 1. F-Droid
F-Droid 是一个开源应用商店。要发布到 F-Droid：

1. Fork [fdroiddata](https://gitlab.com/fdroid/fdroiddata)
2. 添加应用元数据文件
3. 提交 Merge Request

### 2. GitHub Releases
已经在上面的步骤中完成。

### 3. 自建分发
```bash
# 生成 APK
./gradlew assembleRelease

# 上传到你的服务器
scp app/build/outputs/apk/release/*.apk user@server:/path/to/releases/
```

## 版本管理

### 语义化版本
使用 [语义化版本](https://semver.org/)：
- MAJOR.MINOR.PATCH
- 例如：1.0.0, 1.1.0, 1.1.1

### 版本号规则
- **MAJOR**: 不兼容的 API 更改
- **MINOR**: 向后兼容的新功能
- **PATCH**: 向后兼容的 bug 修复

### 示例
```bash
# 版本 1.0.0
git tag -a v1.0.0 -m "Initial release"

# 版本 1.1.0 (新功能)
git tag -a v1.1.0 -m "Add new features"

# 版本 1.1.1 (bug fix)
git tag -a v1.1.1 -m "Fix critical bug"
```

## 发布检查清单

### 发布前
- [ ] 所有测试通过
- [ ] 代码风格检查通过
- [ ] lint 检查通过
- [ ] 版本号已更新
- [ ] CHANGELOG 已更新
- [ ] 文档已更新
- [ ] 已在真机测试
- [ ] 已测试所有功能

### 构建
- [ ] Clean build 成功
- [ ] Release APK 生成
- [ ] Release AAB 生成
- [ ] APK 大小合理（< 50MB）
- [ ] 已签名

### 测试
- [ ] 在 Android 8.0 测试
- [ ] 在 Android 10 测试
- [ ] 在 Android 12 测试
- [ ] 在 Android 14 测试
- [ ] 测试 VPN 功能
- [ ] 测试所有 UI 页面
- [ ] 测试后台运行
- [ ] 测试电池消耗

### 发布
- [ ] GitHub Release 已创建
- [ ] Release notes 已编写
- [ ] APK 已上传
- [ ] AAB 已上传（Play Store）
- [ ] Tag 已创建

### 发布后
- [ ] 监控崩溃报告
- [ ] 收集用户反馈
- [ ] 准备下一个版本

## 自动化发布流程

### 使用 GitHub Actions
项目已配置 GitHub Actions，支持：

1. **自动构建**: 每次 push 和 PR 自动构建
2. **自动测试**: 运行所有测试
3. **自动发布**: 推送 tag 时自动发布

### 配置 Secrets
在 GitHub 仓库设置中添加以下 Secrets：

```
KEYSTORE_BASE64: <base64 encoded keystore file>
KEYSTORE_PASSWORD: <keystore password>
KEY_ALIAS: <key alias>
KEY_PASSWORD: <key password>
```

### 生成 Base64 编码的 Keystore
```bash
base64 -i sing-box.jks | tr -d '\n' > keystore_base64.txt
```

## 回滚策略

### 如果发布有问题
1. **GitHub Release**: 标记为 pre-release 或删除
2. **Play Store**: 停止 rollout，发布修复版本
3. **F-Droid**: 等待自动更新或手动触发

### 紧急修复流程
```bash
# 1. 修复代码
git commit -m "fix: critical bug"

# 2. 创建 hotfix tag
git tag -a v1.0.1 -m "Hotfix: critical bug"

# 3. 推送
git push origin v1.0.1

# 4. GitHub Actions 会自动构建和发布
```

## 最佳实践

### 1. 发布节奏
- 每 2-4 周发布一个 minor 版本
- 随时发布 patch 版本修复 bug
- 每 3-6 个月发布一个 major 版本

### 2. 测试策略
- 发布前在多个设备测试
- 使用 Firebase Test Lab 进行自动化测试
- 收集 beta 测试者反馈

### 3. 文档
- 保持 README 更新
- 维护 CHANGELOG
- 编写详细的 release notes

### 4. 监控
- 使用 Firebase Crashlytics 监控崩溃
- 监控用户评价和反馈
- 跟踪应用性能指标

## 常见问题

### Q: APK 签名失败
A: 检查 keystore 文件路径和密码是否正确

### Q: 构建失败
A: 运行 `./gradlew clean` 后重试

### Q: Play Store 拒绝
A: 检查应用是否符合 [Play Store 政策](https://play.google.com/about/developer-content-policy/)

### Q: 版本冲突
A: 确保 versionCode 递增，versionName 更新

## 参考资源

- [Android App Bundle](https://developer.android.com/guide/app-bundle)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Semantic Versioning](https://semver.org/)
