# SMS2Email Releases / 发布版本

## 如何获取APK / How to Get APK

由于GitHub仓库限制，APK文件不直接包含在仓库中。请按照以下方式获取：

APK files are not included in the repository due to size limitations. Please obtain them via:

### 方法 1: 自行构建 / Method 1: Build Yourself

按照 `BUILD_INSTRUCTIONS.md` 中的说明构建APK。

Follow the instructions in `BUILD_INSTRUCTIONS.md` to build the APK.

```bash
cd sms2email
./gradlew assembleRelease
```

构建完成后，APK位于：
After building, the APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

### 方法 2: GitHub Releases (推荐) / Method 2: GitHub Releases (Recommended)

访问GitHub仓库的Releases页面下载预构建的APK：

Visit the GitHub repository's Releases page to download pre-built APK:

```
https://github.com/[your-username]/[repo-name]/releases
```

### 方法 3: CI/CD构建 / Method 3: CI/CD Build

如果启用了GitHub Actions，可以从Actions的Artifacts下载：

If GitHub Actions is enabled, download from Actions Artifacts:

```
Actions -> Latest build -> Artifacts -> app-release
```

## 版本历史 / Version History

### v1.0.0 (2025-11-09)
- **状态 / Status**: Initial Release
- **大小 / Size**: ~15-20 MB
- **SHA256**: (待生成 / To be generated)
- **下载 / Download**: 自行构建 / Build yourself

**主要功能 / Key Features**:
- 双卡双待SMS转发 / Dual SIM SMS forwarding
- 自定义SMTP配置 / Custom SMTP configuration
- 邮件模板系统 / Email template system
- 配置导入/导出 / Config import/export
- Material Design 3 UI
- 通话记录监控 / Call log monitoring
- i18n支持 / i18n support

## 发布流程 / Release Process

### 创建新版本 / Creating a New Release

1. **更新版本号 / Update Version**
   ```kotlin
   // app/build.gradle.kts
   versionCode = 2
   versionName = "1.1.0"
   ```

2. **构建APK / Build APK**
   ```bash
   ./gradlew clean assembleRelease
   ```

3. **生成校验和 / Generate Checksum**
   ```bash
   sha256sum app/build/outputs/apk/release/app-release.apk > app-release.apk.sha256
   ```

4. **创建GitHub Release / Create GitHub Release**
   ```bash
   gh release create v1.1.0 \
     app/build/outputs/apk/release/app-release.apk \
     --title "SMS2Email v1.1.0" \
     --notes "Release notes here"
   ```

5. **更新RELEASE_NOTES.md / Update RELEASE_NOTES.md**
   添加新版本的说明
   Add notes for the new version

## 安装说明 / Installation Instructions

### Android设备安装 / Installing on Android

1. **下载APK / Download APK**
   从Releases页面或自行构建
   From Releases page or build yourself

2. **启用未知来源 / Enable Unknown Sources**
   ```
   设置 -> 安全 -> 未知来源
   Settings -> Security -> Unknown Sources
   ```

3. **安装APK / Install APK**
   点击APK文件进行安装
   Tap the APK file to install

4. **授予权限 / Grant Permissions**
   首次运行时授予所需权限
   Grant required permissions on first run

### 通过ADB安装 / Installing via ADB

```bash
adb install -r app-release.apk
```

## 验证APK / Verify APK

### 检查签名 / Check Signature

```bash
jarsigner -verify -verbose -certs app-release.apk
```

### 验证校验和 / Verify Checksum

```bash
sha256sum -c app-release.apk.sha256
```

### 查看APK信息 / View APK Info

```bash
aapt dump badging app-release.apk
```

## 签名信息 / Signing Information

**重要 / IMPORTANT**: 官方发布的APK使用Flumenis LLC的发布密钥签名。

Official releases are signed with Flumenis LLC's release key.

- **证书指纹 / Certificate Fingerprint**: (待添加 / To be added)
- **签名者 / Signer**: Flumenis LLC
- **有效期 / Validity**: 10000 days

### 验证签名 / Verify Signature

```bash
keytool -printcert -jarfile app-release.apk
```

## 更新说明 / Update Notes

### 从旧版本升级 / Upgrading from Old Version

1. **备份配置 / Backup Configuration**
   在主界面点击"导出"保存配置
   Click "Export" on home screen to save config

2. **卸载旧版本 / Uninstall Old Version** (可选 / Optional)
   ```
   设置 -> 应用 -> SMS2Email -> 卸载
   Settings -> Apps -> SMS2Email -> Uninstall
   ```

3. **安装新版本 / Install New Version**
   安装下载的新APK
   Install the downloaded new APK

4. **恢复配置 / Restore Configuration**
   在主界面点击"导入"恢复配置
   Click "Import" on home screen to restore config

## 故障排除 / Troubleshooting

### 安装失败 / Installation Failed

**问题 / Issue**: "应用未安装"
**解决 / Solution**:
1. 确保启用了未知来源
2. 卸载旧版本后重试
3. 检查存储空间是否足够

### 签名不匹配 / Signature Mismatch

**问题 / Issue**: "已存在同名但签名不同的应用"
**解决 / Solution**:
1. 导出配置
2. 完全卸载旧版本
3. 安装新版本
4. 导入配置

### 权限问题 / Permission Issues

**问题 / Issue**: 应用无法正常工作
**解决 / Solution**:
1. 检查所有权限是否已授予
2. 设置 -> 应用 -> SMS2Email -> 权限
3. 确保所有权限都已开启

## 技术支持 / Technical Support

遇到问题请联系：
For issues, please contact:

- **邮箱 / Email**: support@flumenis.com
- **GitHub Issues**: [提交Issue / Submit Issue]
- **网站 / Website**: https://flumenis.com

## 许可证 / License

Copyright © 2025 Flumenis LLC, Delaware. All rights reserved.

---

**开发者 / Developer**: Flumenis LLC
**地址 / Location**: Delaware, United States
**更新日期 / Last Updated**: 2025-11-09
