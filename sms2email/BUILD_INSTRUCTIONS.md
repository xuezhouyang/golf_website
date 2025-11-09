# SMS2Email - Build Instructions

## 构建说明 / Build Instructions

由于Android构建需要完整的Android SDK和环境，请按照以下步骤在本地构建APK。

Since Android building requires full Android SDK and environment, please follow these steps to build the APK locally.

## 前置要求 / Prerequisites

1. **Android Studio** (Arctic Fox or later)
   - Download: https://developer.android.com/studio

2. **JDK 17**
   - Android Studio includes JDK, or download from: https://adoptium.net/

3. **Android SDK**
   - Install via Android Studio SDK Manager
   - Required SDK: API 34 (Android 14)
   - Minimum SDK: API 24 (Android 7.0)

## 构建步骤 / Build Steps

### 方法 1: 使用 Android Studio GUI

1. **打开项目 / Open Project**
   ```
   File -> Open -> 选择 sms2email 文件夹
   ```

2. **同步 Gradle / Sync Gradle**
   ```
   File -> Sync Project with Gradle Files
   ```

3. **构建 Release APK / Build Release APK**
   ```
   Build -> Generate Signed Bundle / APK
   -> 选择 APK
   -> 选择 release keystore (release-keystore.jks)
   -> 输入密码: flumenis2025
   -> 选择 release build variant
   -> Finish
   ```

4. **查找 APK / Find APK**
   ```
   APK 位置: app/build/outputs/apk/release/app-release.apk
   ```

### 方法 2: 使用命令行 / Using Command Line

1. **进入项目目录 / Navigate to Project**
   ```bash
   cd sms2email
   ```

2. **给 Gradle Wrapper 添加执行权限 / Make Gradle Wrapper Executable**
   ```bash
   chmod +x gradlew
   ```

3. **清理并构建 Release / Clean and Build Release**
   ```bash
   ./gradlew clean assembleRelease
   ```

4. **查找 APK / Find APK**
   ```bash
   ls -lh app/build/outputs/apk/release/
   # APK 文件: app-release.apk
   ```

### 方法 3: 构建 Android App Bundle (AAB) for Google Play

```bash
./gradlew clean bundleRelease
```

AAB 文件位置 / AAB Location:
```
app/build/outputs/bundle/release/app-release.aab
```

## 签名配置 / Signing Configuration

### Keystore 信息 / Keystore Information

- **文件名 / Filename**: `release-keystore.jks`
- **位置 / Location**: `sms2email/release-keystore.jks`
- **密码 / Password**: `flumenis2025`
- **别名 / Alias**: `sms2email`
- **Key 密码 / Key Password**: `flumenis2025`

### 重新生成 Keystore (如需要) / Regenerate Keystore (if needed)

```bash
keytool -genkey -v \
  -keystore release-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias sms2email \
  -dname "CN=Flumenis LLC, OU=Mobile, O=Flumenis LLC, L=Wilmington, S=Delaware, C=US" \
  -storepass flumenis2025 \
  -keypass flumenis2025
```

## 构建变体 / Build Variants

### Debug Build
```bash
./gradlew assembleDebug
```
- 不需要签名
- 包含调试信息
- APK 较大

### Release Build
```bash
./gradlew assembleRelease
```
- 已签名
- 代码混淆
- APK 较小
- 生产就绪

## ProGuard 配置 / ProGuard Configuration

Release 构建启用了代码混淆和优化：

- **混淆规则 / Obfuscation Rules**: `app/proguard-rules.pro`
- **优化级别 / Optimization Level**: Full
- **保留规则 / Keep Rules**:
  - JavaMail classes
  - Gson classes
  - Data models

## 验证 APK / Verify APK

### 检查签名 / Check Signature
```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

### 查看 APK 信息 / View APK Info
```bash
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

### 分析 APK 大小 / Analyze APK Size
```bash
# 在 Android Studio 中
Build -> Analyze APK -> 选择 app-release.apk
```

## 安装 APK / Install APK

### 使用 ADB / Using ADB
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### 通过文件传输 / Via File Transfer
1. 将 APK 复制到手机
2. 在手机上启用"未知来源"安装
3. 点击 APK 文件安装

## 故障排除 / Troubleshooting

### Gradle 同步失败 / Gradle Sync Failed
```bash
# 清理 Gradle 缓存
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf app/build

# 重新同步
./gradlew --refresh-dependencies
```

### 内存不足 / Out of Memory
编辑 `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### 签名失败 / Signing Failed
1. 验证 keystore 路径正确
2. 验证密码正确
3. 检查 keystore 文件存在

### 构建速度慢 / Slow Build
```bash
# 启用并行构建
./gradlew assembleRelease --parallel --max-workers=4

# 使用构建缓存
./gradlew assembleRelease --build-cache
```

## 发布到 Google Play / Publish to Google Play

### 1. 构建 AAB / Build AAB
```bash
./gradlew bundleRelease
```

### 2. 测试 AAB / Test AAB
```bash
# 使用 bundletool
bundletool build-apks \
  --bundle=app/build/outputs/bundle/release/app-release.aab \
  --output=app.apks \
  --mode=universal

bundletool install-apks --apks=app.apks
```

### 3. 上传到 Google Play Console / Upload to Google Play Console
1. 访问 https://play.google.com/console
2. 选择应用或创建新应用
3. Production -> Create new release
4. 上传 app-release.aab
5. 填写发布说明
6. 审核并发布

## 持续集成 / CI/CD

### GitHub Actions 示例 / GitHub Actions Example

创建 `.github/workflows/android-build.yml`:

```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      working-directory: sms2email

    - name: Build Release APK
      run: ./gradlew assembleRelease
      working-directory: sms2email

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: sms2email/app/build/outputs/apk/release/app-release.apk
```

## 版本管理 / Version Management

更新版本号在 `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2      // 递增整数
    versionName = "1.1.0" // 语义化版本
}
```

版本号规则 / Version Rules:
- **versionCode**: 每次发布递增 (1, 2, 3, ...)
- **versionName**: 语义化版本 (1.0.0, 1.1.0, 2.0.0)

## APK 大小优化 / APK Size Optimization

### 启用资源压缩 / Enable Resource Shrinking
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true  // 添加此行
        ...
    }
}
```

### 拆分 APK (多 APK) / APK Splits
```kotlin
splits {
    abi {
        enable = true
        reset()
        include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        universalApk = true
    }
}
```

## 技术支持 / Technical Support

构建问题请联系 / For build issues contact:
- **Email**: support@flumenis.com
- **GitHub Issues**: [项目地址]

---

**Flumenis LLC, Delaware**
**Version**: 1.0.0
**Last Updated**: 2025-11-09
