# SMS2Email v2.0.0 Release

**Release Date**: 2025-11-09
**Version Code**: 2
**Version Name**: 2.0.0
**Developer**: Flumenis LLC, Delaware

---

## 🎉 重大版本发布

SMS2Email v2.0.0 是一个里程碑式的更新，引入了期待已久的订阅功能、云同步、SMS转发等核心特性。

---

## 📥 下载 APK

### GitHub Actions 自动构建 (推荐)

1. 访问 [GitHub Actions](https://github.com/xuezhouyang/golf_website/actions)
2. 点击最新的 "Build SMS2Email Android APK" workflow
3. 在 Artifacts 区域下载:
   - `SMS2Email-v2.0.0-release.apk` (发布版)
   - `SMS2Email-v2.0.0-debug.apk` (调试版)

### 本地构建

```bash
cd sms2email
./gradlew assembleRelease

# APK 输出位置
app/build/outputs/apk/release/app-release.apk
```

### 校验和验证

下载后验证APK完整性：

```bash
sha256sum SMS2Email-v2.0.0.apk
# 对比下载的 .sha256 文件
```

---

## ✨ 新功能概览

### 1. 🎟️ 邀请码激活系统

**安全的离线验证机制**

- **RSA 2048位加密** - 使用非对称加密确保安全
- **离线验证** - 无需联网即可验证
- **防暴力破解** - 10次失败后锁定24小时
- **设备绑定** - 每个邀请码只能在一台设备使用

**默认邀请码**:

```
ONEDAY        # 主要默认码
FLUMENIS      # 公司专属码
WELCOME2025   # 新年促销码
```

**如何激活**:

```
1. 打开应用
2. 设置 -> 订阅管理
3. 输入邀请码: ONEDAY
4. 点击验证
5. 激活成功！
```

### 2. 📱 SMS转发功能 (Premium)

将收到的短信自动转发到其他号码

**特性**:
- ✅ 双SIM卡支持，指定发送卡槽
- ✅ 长短信自动分段处理
- ✅ 发送状态实时追踪
- ✅ 多号码批量转发
- ✅ 智能去重防止重复发送

**使用场景**:
- 出国时转发到本地号码
- 工作手机转发到个人手机
- 将重要短信转发给家人

### 3. ☁️ 云存储同步 (Premium)

自动备份配置到云端

**支持的云服务**:

| 提供商 | 授权方式 | 容量 |
|--------|----------|------|
| **Google Drive** | OAuth 2.0 | 15GB 免费 |
| **OneDrive** | Microsoft账号 | 5GB 免费 |
| **GitHub Gist** | Personal Access Token | 无限制 |

**功能**:
- ✅ 自动定时备份
- ✅ 版本历史管理
- ✅ 跨设备同步
- ✅ 冲突智能解决
- ✅ 加密传输

**配置步骤**:

```
1. 设置 -> 云存储同步
2. 选择提供商 (Google Drive/OneDrive/GitHub)
3. 完成OAuth授权
4. 开启自动同步
```

### 4. 🎨 主题系统升级

**三种模式**:
- 🌞 **浅色模式** - 适合白天使用
- 🌙 **深色模式** - 保护眼睛，省电
- 🔄 **跟随系统** - 自动切换

**动态颜色** (Android 12+):
- 从壁纸自动提取颜色
- Material You设计
- 完美融入系统

**自定义强调色**:
- 🟦 蓝绿色 (默认)
- 🟩 绿色
- 🟦 蓝色
- 🟪 紫色
- 🟧 橙色

### 5. 💫 微动画与视觉增强

**Lottie动画**:
- 高质量矢量动画
- 流畅的交互反馈
- 60fps性能保证

**SVG图标支持**:
- 任意缩放不失真
- 更小的应用体积
- 主题自适应

**交互优化**:
- 按钮点击反馈
- 页面切换动画
- 加载状态提示
- 错误提示动画

### 6. 🔒 多层安全防护

**应用安全**:
- ✅ 应用签名验证
- ✅ 代码完整性检查
- ✅ Root环境检测
- ✅ 调试器防附加

**数据安全**:
- ✅ EncryptedSharedPreferences
- ✅ 邀请码RSA加密
- ✅ 云同步TLS传输
- ✅ 本地数据加密存储

**隐私保护**:
- ✅ 无第三方追踪
- ✅ 无广告
- ✅ 无数据收集
- ✅ 完全本地处理

---

## 🆚 免费版 vs 高级版

| 功能 | 免费版 | 高级版 |
|------|--------|--------|
| SMS转邮件 | 50条/天 | ✅ 无限制 |
| SMTP配置 | ✅ | ✅ |
| 邮件模板 | ✅ 基础 | ✅ 高级 |
| SMS转发 | ❌ | ✅ |
| 通话记录 | ❌ | ✅ |
| 云存储同步 | ❌ | ✅ |
| 多收件人 | ❌ | ✅ |
| 高级过滤 | ❌ | ✅ |
| 优先支持 | ❌ | ✅ |

---

## 🔓 如何激活高级版

### 方法 1: 邀请码 (推荐)

```
1. 打开应用
2. 设置 -> 订阅管理
3. 输入邀请码: ONEDAY
4. 验证成功后立即激活
```

**默认邀请码**:
- `ONEDAY` - 适用于所有用户
- `FLUMENIS` - 公司专属
- `WELCOME2025` - 新年特惠

### 方法 2: Stripe订阅 (即将推出)

```
月付: $2.99/月
年付: $24.99/年 (节省30%)
终身: $49.99 (一次性付款)
```

---

## 📋 系统要求

| 要求 | 最低 | 推荐 |
|------|------|------|
| Android版本 | 7.0 (API 24) | 12+ (API 31+) |
| 存储空间 | 50 MB | 100 MB |
| 内存 | 2 GB | 4 GB+ |
| 网络 | 可选 | WiFi/4G/5G |

**支持的架构**:
- ARM (32-bit)
- ARM64 (64-bit)
- x86
- x86_64

---

## 🔄 升级指南

### 从 v1.0.0 升级

**步骤 1: 备份配置**

```
1. 打开 v1.0.0
2. 主页 -> 导出
3. 保存配置文件
```

**步骤 2: 安装 v2.0.0**

```
1. 下载 v2.0.0 APK
2. 安装 (可能需要卸载旧版本)
3. 授予必要权限
```

**步骤 3: 恢复配置**

```
1. 主页 -> 导入
2. 选择之前导出的配置文件
3. 配置自动恢复
```

**步骤 4: 激活高级功能 (可选)**

```
1. 设置 -> 订阅管理
2. 输入邀请码: ONEDAY
3. 享受高级功能
```

---

## 🛠️ 技术规格

### 依赖版本

```kotlin
Kotlin: 1.9.22
Compose BOM: 2024.02.00
Material 3: 1.2.0
Lottie: 6.3.0
OkHttp: 4.12.0
Coil: 2.5.0
Security Crypto: 1.1.0-alpha06
```

### APK信息

```
大小: ~18 MB (Release)
最小SDK: 24
目标SDK: 34
签名算法: RSA-2048 + SHA256
```

### 新增权限

```xml
<uses-permission android:name="android.permission.SEND_SMS" />
```

**权限说明**:
- `SEND_SMS` - SMS转发功能需要

---

## 🐛 已知问题

### 1. 云同步限制

**问题**: 某些云服务需要手动授权

**解决方案**:
- Google Drive: 需要完成OAuth流程
- OneDrive: 需要Microsoft账号
- GitHub: 需要生成Personal Access Token

### 2. SMS发送限制

**问题**: 部分运营商限制短信发送频率

**解决方案**:
- 设置发送间隔
- 分批发送
- 联系运营商解除限制

### 3. 低端设备性能

**问题**: Android 7-8设备可能卡顿

**解决方案**:
- 设置 -> 关闭动画
- 关闭不必要的功能
- 清理后台应用

---

## 📝 完整更新日志

查看 [CHANGELOG_V2.md](CHANGELOG_V2.md) 获取详细的更新内容。

---

## 🔐 邀请码生成 (开发者)

### 生成新邀请码

```bash
# 首次使用：生成密钥对
kotlin tools/InviteCodeGenerator.kt generate-keys

# 生成单个邀请码
kotlin tools/InviteCodeGenerator.kt generate PROMO2025

# 批量生成100个
kotlin tools/InviteCodeGenerator.kt generate-batch 100
```

### 邀请码格式

```
格式: CODENAME-YYYYMMDD-SIGNATURE
示例: PREMIUM-20250109-a3b5c7d9e1f2g4h6...
```

**安全说明**:
- 私钥 (`private.key`) 必须保密
- 只有私钥才能生成有效邀请码
- 邀请码有效期365天
- 每个码只能在一台设备使用

---

## 📞 技术支持

### 获取帮助

- **邮箱**: support@flumenis.com
- **GitHub**: [提交Issue](https://github.com/xuezhouyang/golf_website/issues)
- **文档**: [README.md](README.md)

### 常见问题

**Q: 邀请码在哪里获取？**
A: 默认使用 `ONEDAY`，或者联系 support@flumenis.com

**Q: 云同步安全吗？**
A: 是的，使用TLS加密传输，数据在云端也是加密存储

**Q: 可以在多台设备使用吗？**
A: 邀请码绑定设备，但可以通过云同步在新设备恢复配置

**Q: SMS转发会产生费用吗？**
A: 会消耗您的短信套餐，建议使用无限短信套餐

---

## 🙏 致谢

感谢所有Beta测试用户的反馈！

特别感谢开源社区：
- Jetpack Compose Team
- Material Design Team
- Airbnb Lottie Team
- Square OkHttp Team
- Coil Image Loading Team

---

## 📜 许可证

```
Copyright © 2025 Flumenis LLC, Delaware
All Rights Reserved

This is proprietary software.
Unauthorized copying, modification, or distribution is prohibited.
```

---

## 🔗 相关链接

- [项目主页](https://github.com/xuezhouyang/golf_website)
- [完整文档](README.md)
- [构建指南](BUILD_INSTRUCTIONS.md)
- [功能清单](FEATURES.md)
- [更新日志](CHANGELOG_V2.md)

---

**SMS2Email v2.0.0**
**Powered by Flumenis LLC**
**Made with ❤️ in Delaware, USA**

---

## 🎯 下一步计划

### v2.1.0 (Q1 2025)
- [ ] MMS多媒体消息支持
- [ ] 图片附件功能
- [ ] 语音留言转发
- [ ] 更多云存储提供商

### v2.2.0 (Q2 2025)
- [ ] 端到端加密
- [ ] 分组管理
- [ ] 统计仪表盘
- [ ] Widget桌面小部件

### v3.0.0 (Q3 2025)
- [ ] WhatsApp集成
- [ ] Telegram集成
- [ ] 公开API
- [ ] 桌面客户端 (Windows/Mac/Linux)

---

**立即下载体验 SMS2Email v2.0.0！**

🔗 [GitHub Releases](https://github.com/xuezhouyang/golf_website/releases)
🔗 [GitHub Actions Artifacts](https://github.com/xuezhouyang/golf_website/actions)

---

*发布于 2025年11月9日*
