# 高迷网 Golf App

高尔夫电商移动应用的 Flutter 实现

## 功能特性

- 🏌️ 高尔夫球杆商品浏览和筛选
- 🛒 购物车管理
- 💳 订单支付流程
- 👤 用户中心（订单、收藏、地址管理）
- 🔐 用户认证（登录/注册）
- 📦 物流追踪
- 💰 退款退货管理
- 🔒 安全中心（修改密码、手机、邮箱）

## 项目结构

```
lib/
  ├── main.dart           # 应用入口
  ├── models/             # 数据模型
  ├── screens/            # 页面
  ├── widgets/            # 可复用组件
  └── services/           # 服务层（API、存储等）
```

## 技术栈

- Flutter SDK 3.0+
- Provider（状态管理）
- Dio（网络请求）
- GoRouter（路由导航）

## 运行项目

```bash
flutter pub get
flutter run
```

## 构建

```bash
# Android
flutter build apk

# iOS
flutter build ios
```
