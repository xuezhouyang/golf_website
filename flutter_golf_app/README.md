# 🏌️ 高迷网 Golf App

> 采用 2025 年温暖毛玻璃设计风格的高尔夫电商移动应用

## ✨ 设计特色

- 🎨 **温暖毛玻璃设计**：采用 Glassmorphism 设计风格，柔和的渐变色彩和半透明效果
- 🌈 **温暖配色方案**：珊瑚橙、温暖杏黄、桃色渐变，营造舒适的视觉体验
- 📱 **全平台支持**：Android、iOS、Web、macOS、Windows、Linux
- 🎯 **Material Design 3**：最新的 Material You 设计规范

## 🚀 功能特性

### 商品浏览
- 🏌️ 多分类球杆展示（一号木、球道木、铁杆、铁木杆、推杆、挖起杆）
- 🔍 强大的搜索功能，支持搜索历史
- 📊 多维度筛选和排序（价格、评分、销量）
- ⭐ 商品收藏管理

### 购物体验
- 🛒 实时购物车管理
- 💳 完整的订单支付流程
- 📦 订单状态追踪
- 🚚 实时物流信息查询

### 用户中心
- 👤 个人信息管理
- 📋 订单管理（待支付、待发货、待收货、待评价）
- ❤️ 我的收藏
- 📍 收货地址管理
- 🔐 安全中心（密码、手机、邮箱修改）
- ⚙️ 应用设置

### 用户认证
- 🔑 邮箱登录/注册
- 📱 手机号验证
- 🔒 密码找回
- 🌐 第三方登录（微信、微博）

## 📁 项目结构

```
flutter_golf_app/
├── lib/
│   ├── main.dart                      # 应用入口
│   ├── theme/
│   │   └── app_theme.dart            # 毛玻璃主题配置
│   ├── models/                        # 数据模型
│   │   ├── product.dart              # 商品模型
│   │   ├── cart_item.dart            # 购物车项
│   │   ├── user.dart                 # 用户模型
│   │   ├── order.dart                # 订单模型
│   │   └── address.dart              # 地址模型
│   ├── services/                      # 状态管理与服务
│   │   ├── auth_provider.dart        # 认证状态
│   │   ├── cart_provider.dart        # 购物车状态
│   │   ├── favorite_provider.dart    # 收藏状态
│   │   └── product_service.dart      # 商品服务
│   └── screens/                       # 页面
│       ├── home_screen.dart          # 首页
│       ├── product_list_screen.dart  # 商品列表
│       ├── product_detail_screen.dart # 商品详情
│       ├── search_screen.dart        # 搜索
│       ├── cart_screen.dart          # 购物车
│       ├── login_screen.dart         # 登录
│       ├── register_screen.dart      # 注册
│       ├── profile_screen.dart       # 个人中心
│       ├── orders_screen.dart        # 订单管理
│       ├── favorites_screen.dart     # 收藏列表
│       ├── address_manage_screen.dart # 地址管理
│       ├── address_edit_screen.dart  # 地址编辑
│       ├── profile_edit_screen.dart  # 个人信息编辑
│       ├── security_center_screen.dart # 安全中心
│       ├── change_password_screen.dart # 修改密码
│       ├── change_phone_screen.dart  # 修改手机
│       ├── change_email_screen.dart  # 修改邮箱
│       ├── logistics_screen.dart     # 物流追踪
│       └── settings_screen.dart      # 设置
└── assets/                            # 资源文件
    └── images/                        # 图片资源
```

## 🛠️ 技术栈

- **Flutter SDK 3.0+** - 跨平台 UI 框架
- **Material Design 3** - 最新设计规范
- **Provider 6.1+** - 轻量级状态管理
- **Dio 5.4+** - 强大的 HTTP 客户端
- **Cached Network Image** - 图片缓存
- **Carousel Slider** - 轮播图组件

## 🎨 设计系统

### 颜色方案
- **主色调**：温暖珊瑚橙 `#FF9A76`
- **辅助色**：温暖杏黄 `#FFCF96`
- **强调色**：温暖桃色 `#FEC6A1`
- **背景色**：温暖米白 `#FFF8F0`

### 组件库
- `GlassContainer` - 毛玻璃容器组件
- `GradientCard` - 渐变卡片组件
- `SoftCard` - 柔和阴影卡片

## 📱 支持平台

- ✅ Android
- ✅ iOS
- ✅ Web
- ✅ macOS
- ✅ Windows
- ✅ Linux

## 🚀 快速开始

### 环境要求
- Flutter SDK 3.0 或更高版本
- Dart SDK 3.0 或更高版本

### 安装依赖
```bash
cd flutter_golf_app
flutter pub get
```

### 运行项目
```bash
# 在模拟器/真机上运行
flutter run

# 指定平台
flutter run -d chrome       # Web
flutter run -d macos        # macOS
flutter run -d windows      # Windows
flutter run -d linux        # Linux
```

### 构建发布版本

```bash
# Android APK
flutter build apk --release

# Android App Bundle
flutter build appbundle --release

# iOS
flutter build ios --release

# Web
flutter build web --release

# Desktop
flutter build macos --release
flutter build windows --release
flutter build linux --release
```

## 📝 开发说明

### 添加新页面
1. 在 `lib/screens/` 创建新的屏幕文件
2. 在 `main.dart` 中注册路由
3. 使用主题组件保持设计一致性

### 状态管理
使用 Provider 进行状态管理：
- `CartProvider` - 购物车状态
- `AuthProvider` - 用户认证状态
- `FavoriteProvider` - 收藏状态

### 主题自定义
在 `lib/theme/app_theme.dart` 中修改主题配置。

## 🎯 未来计划

- [ ] 集成真实 API 接口
- [ ] 添加支付集成（微信、支付宝）
- [ ] 实现推送通知
- [ ] 添加社交分享功能
- [ ] 支持多语言国际化
- [ ] 暗黑模式支持
- [ ] 性能优化和代码分割

## 📄 许可证

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

---

**Made with ❤️ using Flutter**
