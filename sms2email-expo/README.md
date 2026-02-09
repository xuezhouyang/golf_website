# SMS2Email - Expo Edition

A modern, beautiful React Native application for forwarding SMS messages to email. Built with Expo, NativeWind, and Tamagui for a premium mobile experience.

![Version](https://img.shields.io/badge/version-2.0.0-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![License](https://img.shields.io/badge/license-MIT-orange)

## 🌟 Features

- **📧 SMS to Email Forwarding**: Automatically forward incoming SMS messages to your email
- **📱 Dual SIM Support**: Handle messages from both SIM cards separately
- **🎨 Beautiful Modern UI**: Clean, intuitive interface with dark mode support
- **✨ Premium Features**: Unlimited forwarding, call log monitoring, advanced filters
- **☁️ Cloud Sync**: Backup and restore configurations across devices
- **🔒 Secure**: Encrypted storage for sensitive credentials
- **📊 Statistics**: Track forwarded messages and service status
- **🎯 Smart Filtering**: Filter by sender, keywords, or custom rules
- **📝 Custom Templates**: Customize email subject and body with variables
- **🌈 Theme Customization**: Choose from multiple color schemes and dark mode

## 🏗️ Tech Stack

### Core Framework
- **Expo SDK** ~50.0.0 - Comprehensive React Native development platform
- **React Native** 0.73.4 - Cross-platform mobile framework
- **TypeScript** - Type-safe development

### UI & Styling
- **NativeWind** 4.0.1 - Tailwind CSS for React Native
- **Tamagui** 1.95.0 - Universal component library with native performance
- **Ionicons** - Beautiful icon set via @expo/vector-icons

### Navigation & Routing
- **expo-router** 3.4.0 - File-based routing for React Native
- Tab-based navigation with stack modals

### State Management
- **Zustand** 4.5.0 - Lightweight state management
- **AsyncStorage** - Persistent storage with automatic state rehydration

### Email Service
- **@react-native-async-storage/async-storage** - Secure credential storage
- SMTP email sending (implementation pending)

## 📁 Project Structure

```
sms2email-expo/
├── app/
│   ├── (tabs)/              # Tab navigation screens
│   │   ├── _layout.tsx      # Tab navigator setup
│   │   ├── index.tsx        # Home screen
│   │   ├── forwarding.tsx   # SMS forwarding config
│   │   ├── cloud.tsx        # Cloud sync settings
│   │   ├── theme.tsx        # Theme customization
│   │   └── about.tsx        # About & support
│   ├── _layout.tsx          # Root layout with providers
│   ├── settings.tsx         # SMTP settings modal
│   ├── template.tsx         # Email template editor
│   └── premium.tsx          # Premium subscription
├── store/
│   └── settings.ts          # Zustand store with persistence
├── assets/                  # Images, fonts, etc.
├── tamagui.config.ts        # Tamagui theme configuration
├── tailwind.config.js       # Tailwind CSS configuration
├── global.css               # Global CSS with Tailwind directives
├── app.json                 # Expo configuration
├── package.json             # Dependencies
└── tsconfig.json            # TypeScript configuration
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn
- Expo CLI: `npm install -g expo-cli`
- For Android: Android Studio with SDK 24+
- For iOS: Xcode 14+ (macOS only)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd sms2email-expo
   ```

2. **Install dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Start development server**
   ```bash
   npx expo start
   ```

4. **Run on device/emulator**
   - Press `a` for Android
   - Press `i` for iOS
   - Scan QR code with Expo Go app

## 🔧 Development

### Available Scripts

```bash
npm start          # Start Expo development server
npm run android    # Run on Android device/emulator
npm run ios        # Run on iOS simulator (macOS only)
npm run web        # Run in web browser (experimental)
```

### Environment Setup

1. **Android Development**
   ```bash
   # Install Android Studio
   # Set ANDROID_HOME environment variable
   # Create virtual device (AVD)
   ```

2. **SMS Permissions**
   - SMS reading requires runtime permissions
   - Implemented in expo-sms or custom native module
   - Auto-request on first launch

### Key Configuration Files

**app.json** - Expo configuration
- App name, bundle identifier, version
- Android permissions (READ_SMS, READ_PHONE_STATE)
- Icons and splash screen

**tamagui.config.ts** - UI theme
- Color schemes (light/dark)
- Typography and spacing
- Component variants

**store/settings.ts** - Application state
- Email SMTP configuration
- Filter rules and templates
- Theme preferences
- Cloud sync settings

## 📱 Features Guide

### SMS Forwarding

Configure automatic SMS forwarding:
1. Navigate to **Forwarding** tab
2. Choose single or multiple recipients
3. Add email addresses
4. Enable service from Home screen

### Email Templates

Customize email content:
- Available variables: `{{sender}}`, `{{message}}`, `{{timestamp}}`, `{{sim_slot}}`, `{{carrier_name}}`
- Subject template: "SMS from {{sender}} ({{sim_slot}})"
- Body template supports multi-line formatting
- Edit in **Template** screen

### SMTP Configuration

Set up your email server:
1. Tap **Settings** from Home
2. Enter SMTP server details:
   - Host (e.g., smtp.gmail.com)
   - Port (587 for TLS, 465 for SSL)
   - Username and password
3. Configure sender/recipient emails
4. Test connection before saving

### Premium Features

Unlock with invite codes:
- **ONEDAY** - One day trial
- **FLUMENIS** - Developer access
- **WELCOME2025** - New year promotion

Premium includes:
- Unlimited SMS forwarding
- Call log monitoring
- Advanced regex filters
- Multiple recipients
- Cloud backup
- Priority support

### Cloud Sync

Backup configurations to cloud:
- Google Drive integration
- OneDrive support
- GitHub Gist for tech users
- Auto-sync on changes
- Restore from any device

## 🎨 Theming

### Color Schemes

Six built-in accent colors:
- Indigo (default)
- Purple
- Blue
- Green
- Orange
- Pink

### Dark Mode

Three theme modes:
- **Light** - Always light theme
- **Dark** - Always dark theme
- **Auto** - Follow system preference

Customize in **Theme** tab.

## 🔒 Security

- **Encrypted Storage**: SMTP credentials encrypted with expo-secure-store
- **Secure Transmission**: TLS/SSL support for email
- **Permission Control**: Minimal required permissions
- **No Data Collection**: All data stays on device
- **Open Source**: Transparent codebase

## 🏢 Company Information

**Flumenis LLC**
- Registered in Delaware, USA
- Email: support@flumenis.com
- Website: https://flumenis.com
- GitHub: https://github.com/flumenis

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open pull request

## 📄 License

Copyright © 2025 Flumenis LLC. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.

## 🐛 Troubleshooting

### Common Issues

**SMS not forwarding**
- Check SMS permission granted
- Verify SMTP settings
- Test email connection
- Check service enabled on Home screen

**Build errors**
```bash
# Clear cache
npx expo start -c

# Reinstall dependencies
rm -rf node_modules
npm install
```

**Android permissions**
- Ensure AndroidManifest.xml includes READ_SMS
- Request runtime permissions
- Check Android version compatibility (SDK 24+)

## 🚧 Roadmap

- [ ] Implement native SMS reading module
- [ ] Add SMTP email service
- [ ] Implement cloud sync backends
- [ ] Add call log monitoring
- [ ] Implement advanced regex filters
- [ ] Add notification customization
- [ ] Support for MMS forwarding
- [ ] Multi-language support
- [ ] Backup/restore UI improvements

## 📞 Support

Need help?
- Email: support@flumenis.com
- GitHub Issues: [Report a bug](https://github.com/flumenis/sms2email/issues)
- Documentation: [Wiki](https://github.com/flumenis/sms2email/wiki)

---

Built with ❤️ by Flumenis LLC
