# 如何构建 SMS2Email v2.0.0 APK

本文档提供了三种构建APK的方法。

---

## 方法 1: GitHub Actions (推荐 - 全自动)

✅ **所有问题已修复，现在可以自动构建！**

### 已修复的问题
- ✅ gradlew执行权限
- ✅ gradle-wrapper.jar文件
- ✅ gradlew换行符格式 (CRLF -> LF)

### 如何获取APK

1. **访问 GitHub Actions**
   ```
   https://github.com/xuezhouyang/golf_website/actions
   ```

2. **查看最新构建**
   - 点击 "Build SMS2Email Android APK"
   - 查看最新的 workflow run

3. **下载 Artifacts**
   - 等待构建完成（约3-5分钟）
   - 在 Artifacts 区域找到：
     - `SMS2Email-v2.0.0-release` (发布版)
     - `SMS2Email-v2.0.0-debug` (调试版)

4. **下载并解压**
   ```bash
   # 下载后解压zip文件
   unzip SMS2Email-v2.0.0-release.zip

   # 获得APK文件
   # SMS2Email-v2.0.0.apk
   # SMS2Email-v2.0.0.apk.sha256
   ```

### 触发新构建

如果需要手动触发构建：

```bash
# 方法1: 推送代码触发
git commit --allow-empty -m "Trigger build"
git push

# 方法2: 创建tag触发
git tag v2.0.0
git push origin v2.0.0

# 方法3: 在GitHub网页上手动触发
# Actions -> Build SMS2Email Android APK -> Run workflow
```

---

## 方法 2: Android Studio (GUI)

如果您有Android Studio：

### 步骤

1. **打开项目**
   ```
   File -> Open -> 选择 sms2email 文件夹
   ```

2. **等待Gradle同步**
   - Android Studio会自动下载依赖
   - 等待同步完成

3. **构建APK**
   ```
   Build -> Build Bundle(s) / APK(s) -> Build APK(s)
   ```

4. **查找APK**
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

5. **生成签名APK（推荐）**
   ```
   Build -> Generate Signed Bundle / APK
   -> 选择 APK
   -> 使用 ../release-keystore.jks
   -> 密码: flumenis2025
   -> 别名: sms2email
   -> 密码: flumenis2025
   ```

---

## 方法 3: 命令行构建

如果您有Android SDK：

### 前置要求

1. **安装 Java 17**
   ```bash
   # Ubuntu/Debian
   sudo apt install openjdk-17-jdk

   # macOS (使用Homebrew)
   brew install openjdk@17

   # 设置JAVA_HOME
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   ```

2. **安装 Android SDK**
   ```bash
   # 下载Android Studio或命令行工具
   # https://developer.android.com/studio

   # 设置环境变量
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

3. **安装SDK组件**
   ```bash
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

### 构建步骤

```bash
# 1. 进入项目目录
cd sms2email

# 2. 清理旧构建
./gradlew clean

# 3. 构建Debug APK
./gradlew assembleDebug

# 4. 构建Release APK（已签名）
./gradlew assembleRelease

# 5. 查看构建结果
ls -lh app/build/outputs/apk/release/
ls -lh app/build/outputs/apk/debug/
```

### 输出位置

```
app/build/outputs/apk/
├── debug/
│   └── app-debug.apk           (~20 MB)
└── release/
    └── app-release.apk          (~18 MB, 已签名)
```

### 验证签名

```bash
# 查看APK签名信息
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# 查看证书详情
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

---

## 方法 4: Docker构建（跨平台）

使用Docker确保环境一致：

### Dockerfile

创建 `Dockerfile`:

```dockerfile
FROM openjdk:17-slim

# 安装Android SDK
RUN apt-get update && apt-get install -y wget unzip

# 下载Android命令行工具
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
RUN unzip commandlinetools-linux-9477386_latest.zip -d /opt/android-sdk

# 设置环境变量
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/bin:$ANDROID_HOME/platform-tools

# 接受许可并安装SDK
RUN yes | sdkmanager --sdk_root=$ANDROID_HOME --licenses
RUN sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew assembleRelease

CMD ["cp", "app/build/outputs/apk/release/app-release.apk", "/output/"]
```

### 构建

```bash
# 构建Docker镜像
docker build -t sms2email-builder .

# 运行构建
docker run -v $(pwd)/output:/output sms2email-builder

# APK输出到 output/app-release.apk
```

---

## 故障排除

### 问题 1: gradlew permission denied

```bash
chmod +x gradlew
git update-index --chmod=+x gradlew
```

### 问题 2: gradle-wrapper.jar not found

```bash
# 已包含在仓库中
# 如需重新下载：
wget -O gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
```

### 问题 3: CRLF line endings

```bash
# 转换为Unix换行符
dos2unix gradlew
# 或
sed -i 's/\r$//' gradlew
```

### 问题 4: Out of memory

```bash
# 增加Gradle内存
export GRADLE_OPTS="-Xmx4096m -Xms1024m"
./gradlew assembleRelease
```

### 问题 5: SDK not found

```bash
# 设置ANDROID_HOME
export ANDROID_HOME=$HOME/Android/Sdk

# 或创建local.properties
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

---

## 构建优化

### 加速构建

```bash
# 使用并行构建
./gradlew assembleRelease --parallel --max-workers=4

# 使用构建缓存
./gradlew assembleRelease --build-cache

# 离线模式（如果依赖已下载）
./gradlew assembleRelease --offline
```

### 清理构建

```bash
# 清理所有构建产物
./gradlew clean

# 清理Gradle缓存
rm -rf .gradle
rm -rf build
rm -rf app/build
```

---

## 验证APK

### 检查APK内容

```bash
# 列出APK内容
unzip -l app-release.apk

# 查看AndroidManifest
aapt dump badging app-release.apk
```

### 安装测试

```bash
# 通过ADB安装
adb install app-release.apk

# 查看日志
adb logcat | grep SMS2Email
```

### 性能分析

```bash
# Android Studio中
Build -> Analyze APK -> 选择app-release.apk

# 查看大小、方法数、资源等
```

---

## 发布检查清单

构建发布版本前的检查：

- [ ] 版本号正确 (versionCode, versionName)
- [ ] 签名配置正确
- [ ] ProGuard规则完整
- [ ] 所有测试通过
- [ ] 文档已更新
- [ ] CHANGELOG已更新
- [ ] 生成SHA256校验和
- [ ] 在真机上测试

---

## 生成校验和

```bash
# SHA256
sha256sum app-release.apk > app-release.apk.sha256

# MD5
md5sum app-release.apk > app-release.apk.md5

# 验证
sha256sum -c app-release.apk.sha256
```

---

## 当前状态

✅ **所有构建问题已修复！**

- ✅ gradle-wrapper.jar 已添加
- ✅ gradlew权限已设置
- ✅ gradlew换行符已转换为Unix格式
- ✅ 签名配置已完成
- ✅ GitHub Actions workflow已配置

**GitHub Actions 应该可以正常构建APK了！**

访问: https://github.com/xuezhouyang/golf_website/actions

---

## 联系支持

如果构建遇到问题：
- Email: support@flumenis.com
- GitHub Issues: [提交问题](https://github.com/xuezhouyang/golf_website/issues)

---

**Flumenis LLC, Delaware**
**© 2025 All Rights Reserved**
