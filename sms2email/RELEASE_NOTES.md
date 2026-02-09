# SMS2Email Release Notes

## Version 1.0.0 (2025-11-09)

### 🎉 初始发布 / Initial Release

这是SMS2Email的首个正式版本，由Flumenis LLC开发。

This is the first official release of SMS2Email, developed by Flumenis LLC.

### ✨ 核心功能 / Core Features

#### 📱 短信转发 / SMS Forwarding
- ✅ 双卡双待支持 / Dual SIM support
- ✅ 自动检测SIM卡槽 / Automatic SIM slot detection
- ✅ 运营商名称识别 / Carrier name identification
- ✅ 实时转发到邮箱 / Real-time email forwarding

#### ✉️ SMTP配置 / SMTP Configuration
- ✅ 自定义SMTP服务器 / Custom SMTP server
- ✅ TLS/SSL加密支持 / TLS/SSL encryption support
- ✅ Gmail、Outlook等主流邮箱支持 / Gmail, Outlook support
- ✅ 连接测试功能 / Connection testing
- ✅ 安全的凭证存储 / Secure credential storage

#### 📝 模板系统 / Template System
- ✅ 15+可用变量 / 15+ available variables
- ✅ HTML格式支持 / HTML formatting support
- ✅ 自定义主题和正文 / Customizable subject and body
- ✅ 联系人名称查询 / Contact name lookup
- ✅ 设备信息包含 / Device information included

#### 💾 配置管理 / Configuration Management
- ✅ JSON格式导出 / JSON format export
- ✅ 一键导入配置 / One-click import
- ✅ 跨设备同步 / Cross-device sync
- ✅ 配置备份恢复 / Backup and restore

#### 🎨 用户界面 / User Interface
- ✅ Material Design 3 / Material Design 3
- ✅ 动态颜色主题 / Dynamic color theming
- ✅ 深色模式 / Dark mode
- ✅ 流畅动画 / Smooth animations
- ✅ 响应式设计 / Responsive design

#### 🔄 保活机制 / Keep-Alive Features
- ✅ 前台服务 / Foreground service
- ✅ 开机自启动 / Boot auto-start
- ✅ 服务恢复机制 / Service recovery
- ✅ Wake Lock支持 / Wake lock support
- ✅ 持久化通知 / Persistent notification

#### 📞 通话记录 / Call Log (Premium)
- ✅ 来电监控 / Incoming call monitoring
- ✅ 去电监控 / Outgoing call monitoring
- ✅ 未接来电 / Missed calls
- ✅ 通话时长统计 / Call duration tracking
- ✅ 格式化邮件报告 / Formatted email reports

#### 🌍 国际化 / Internationalization
- ✅ 英语 (English)
- ✅ 简体中文 (Simplified Chinese)
- ⏳ 更多语言即将推出 / More languages coming

#### 🔒 安全与隐私 / Security & Privacy
- ✅ 本地数据加密 / Local data encryption
- ✅ 无第三方追踪 / No third-party tracking
- ✅ 无广告 / No ads
- ✅ 直连SMTP服务器 / Direct SMTP connection
- ✅ 权限最小化原则 / Minimal permissions

### 🛠️ 技术规格 / Technical Specifications

#### 平台支持 / Platform Support
- **最低版本 / Minimum**: Android 7.0 (API 24)
- **目标版本 / Target**: Android 14 (API 34)
- **架构 / Architecture**: ARM, ARM64, x86, x86_64

#### 应用大小 / App Size
- **APK大小 / APK Size**: ~15-20 MB
- **安装后 / Installed**: ~30-40 MB

#### 性能 / Performance
- **启动时间 / Startup**: < 1s
- **内存占用 / Memory**: ~50-80 MB
- **电池消耗 / Battery**: 最小化 / Minimized
- **转发延迟 / Forwarding Delay**: < 3s

### 📋 权限说明 / Permissions Explained

```
RECEIVE_SMS        - 接收短信 / Receive SMS
READ_SMS           - 读取短信 / Read SMS
READ_CALL_LOG      - 读取通话记录 / Read call log
READ_CONTACTS      - 读取联系人 / Read contacts
INTERNET           - 发送邮件 / Send emails
FOREGROUND_SERVICE - 前台服务 / Foreground service
BOOT_COMPLETED     - 开机启动 / Boot start
```

### 🔄 已知问题 / Known Issues

1. **电池优化 / Battery Optimization**
   - 某些设备可能需要手动禁用电池优化
   - 解决方案：设置 -> 应用 -> SMS2Email -> 电池 -> 不优化

2. **MIUI / EMUI 设备 / MIUI / EMUI Devices**
   - 需要在安全中心添加自启动权限
   - 需要锁定后台应用

3. **Android 14+ / Android 14+**
   - 首次安装需要手动授予所有权限
   - 某些权限需要在设置中开启

### 🚀 即将推出 / Coming Soon

#### v1.1.0 (计划中 / Planned)
- [ ] MMS支持 / MMS support
- [ ] 图片附件 / Image attachments
- [ ] 多收件人 / Multiple recipients
- [ ] 定时转发 / Scheduled forwarding
- [ ] 高级过滤规则 / Advanced filters

#### v1.2.0 (计划中 / Planned)
- [ ] 云同步 / Cloud sync
- [ ] 统计面板 / Statistics dashboard
- [ ] Widget支持 / Widget support
- [ ] Tasker集成 / Tasker integration

#### v2.0.0 (规划中 / Roadmap)
- [ ] WhatsApp集成 / WhatsApp integration
- [ ] Telegram转发 / Telegram forwarding
- [ ] Webhook支持 / Webhook support
- [ ] API访问 / API access

### 💳 订阅计划 / Subscription Plans

#### 免费版 / Free Tier
- ✓ 每日50条短信转发
- ✓ 基本模板功能
- ✓ 单个收件人
- ✓ 标准保活

#### 高级版 / Premium (即将推出 / Coming Soon)
- ✓ 无限制短信转发
- ✓ 通话记录监控
- ✓ 高级过滤器
- ✓ 多个收件人
- ✓ 优先支持

### 📝 升级说明 / Upgrade Notes

这是首个版本，无需升级步骤。

This is the initial release, no upgrade steps needed.

### 🐛 Bug修复 / Bug Fixes

N/A - 首次发布 / Initial release

### 📞 技术支持 / Technical Support

- **邮箱 / Email**: support@flumenis.com
- **网站 / Website**: https://flumenis.com
- **文档 / Documentation**: README.md

### 🙏 鸣谢 / Acknowledgments

感谢所有测试用户的反馈和建议。

Thanks to all beta testers for their feedback and suggestions.

### 📄 许可证 / License

专有软件 / Proprietary Software
Copyright © 2025 Flumenis LLC, Delaware. All rights reserved.

---

**开发者 / Developer**: Flumenis LLC, Delaware
**发布日期 / Release Date**: 2025-11-09
**版本 / Version**: 1.0.0
**构建号 / Build**: 1
