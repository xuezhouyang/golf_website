# SMS2Email - Android SMS to Email Forwarder

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

A modern Android application that forwards SMS messages to email using custom SMTP configuration. Supports dual SIM cards, customizable email templates with variables, and advanced keep-alive mechanisms.

**Developed by Flumenis LLC, Delaware**

## Features

### Core Features
- ✅ **Dual SIM Support** - Automatically detects and forwards SMS from both SIM cards
- ✅ **Custom SMTP Configuration** - Configure your own email server (Gmail, Outlook, custom server, etc.)
- ✅ **No Root Required** - Works on any Android device without root access
- ✅ **Template System** - Customize email subject and body with variables
- ✅ **Configuration Import/Export** - Backup and restore settings as JSON
- ✅ **Material Design 3** - Modern, beautiful UI following 2025 design trends
- ✅ **Google Play Compatible** - All dependencies approved for Google Play Store

### Template Variables

The app supports rich template variables for customizing your email format:

#### Standard Variables
- `{{sender}}` - Sender phone number
- `{{message}}` - SMS message content
- `{{timestamp}}` - Full date and time
- `{{sim_slot}}` - SIM card slot (SIM 1 or SIM 2)
- `{{carrier_name}}` - Carrier/operator name
- `{{date}}` - Date only (YYYY-MM-DD)
- `{{time}}` - Time only (HH:mm:ss)

#### Enhanced Variables
- `{{Text}}` - Message text
- `{{ContactName}}` - Contact name from address book
- `{{FromNumber}}` - Sender phone number
- `{{ToNumber}}` - Device phone number
- `{{OccurredAt}}` - Full timestamp
- `{{OsName}}` - Operating system (Android version)
- `{{DeviceName}}` - Device model and manufacturer

#### HTML Support
Templates support HTML formatting:
- `<b>text</b>` - Bold text
- `<i>text</i>` - Italic text
- `<br>` - Line break

### Example Template

**Subject:**
```
SMS from {{sender}} ({{sim_slot}})
```

**Body:**
```html
<b>{{Text}}</b><br>
From {{ContactName}} {{FromNumber}}<br>
To {{ToNumber}}<br>
{{OccurredAt}}<br>
via {{OsName}} {{DeviceName}}
```

### Keep-Alive Features

The app includes advanced keep-alive mechanisms to ensure reliable SMS forwarding:

- **Foreground Service** - Runs as a foreground service with persistent notification
- **Boot Receiver** - Automatically restarts after device reboot
- **START_STICKY** - Service automatically restarts if killed by system
- **Wake Lock** - Prevents device from sleeping during message processing
- **WorkManager Support** - Scheduled tasks for reliability

### Filtering Options

- **Sender Whitelist** - Only forward SMS from specific numbers
- **Sender Blacklist** - Block SMS from specific numbers
- **Keyword Filters** - Filter messages by content keywords

## Requirements

- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 14 (API 34)
- **Permissions Required:**
  - `RECEIVE_SMS` - Receive incoming SMS messages
  - `READ_SMS` - Read SMS messages
  - `READ_CONTACTS` - Get contact names (optional)
  - `INTERNET` - Send emails via SMTP
  - `POST_NOTIFICATIONS` - Show foreground service notification (Android 13+)
  - `FOREGROUND_SERVICE` - Run foreground service
  - `RECEIVE_BOOT_COMPLETED` - Auto-start after reboot

## Installation

### From Source

1. Clone this repository
2. Open in Android Studio (Arctic Fox or later)
3. Build and run on your device

```bash
git clone <repository-url>
cd sms2email
./gradlew assembleRelease
```

### APK Installation

1. Download the latest APK from releases
2. Enable "Install from Unknown Sources" in your device settings
3. Install the APK
4. Grant required permissions

## Configuration

### SMTP Setup

1. Open the app and navigate to **Settings**
2. Configure your SMTP server:
   - **SMTP Server:** smtp.gmail.com (for Gmail)
   - **Port:** 587 (TLS) or 465 (SSL)
   - **Username:** your email address
   - **Password:** your email password or app password
   - **Security:** TLS (recommended) or SSL
   - **From Email:** sender email address
   - **To Email:** recipient email address

### Gmail Setup

For Gmail, you need to use an **App Password**:

1. Enable 2-Factor Authentication on your Google account
2. Go to [Google App Passwords](https://myaccount.google.com/apppasswords)
3. Generate a new app password for "Mail"
4. Use this password in the app configuration

### Template Setup

1. Navigate to **Template** screen
2. Customize the subject and body templates
3. Use available variables (tap to copy)
4. Supports HTML formatting
5. Save your template

### Export/Import Configuration

**Export:**
1. Go to Home screen
2. Tap "Export" button
3. Choose save location
4. Configuration saved as JSON

**Import:**
1. Go to Home screen
2. Tap "Import" button
3. Select JSON file
4. Configuration automatically applied

## Architecture

### Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Manual DI
- **Storage:** DataStore for preferences
- **Email:** JavaMail API (android-mail 1.6.7)
- **Async:** Kotlin Coroutines and Flow
- **Navigation:** Jetpack Navigation Compose

### Project Structure

```
com.flumenis.sms2email/
├── data/                   # Data models and configuration
│   ├── EmailConfig.kt      # Email configuration data class
│   ├── PreferencesManager.kt # DataStore preferences
│   └── ...
├── service/                # Background services
│   ├── SmsReceiver.kt      # SMS broadcast receiver
│   ├── SmsMonitorService.kt # Foreground service
│   ├── EmailService.kt     # SMTP email sender
│   └── BootReceiver.kt     # Boot receiver
├── ui/                     # UI components
│   ├── screens/            # Compose screens
│   ├── theme/              # Material Design 3 theme
│   ├── navigation/         # Navigation
│   └── MainViewModel.kt    # Main ViewModel
└── util/                   # Utility classes
    └── ConfigManager.kt    # Import/export manager
```

## Privacy & Security

- All SMS data is sent **directly** to your configured SMTP server
- **No third-party servers** are involved
- **No data collection** or analytics
- **No ads** or tracking
- SMTP credentials stored securely using Android DataStore
- Configuration export contains sensitive data - keep files secure

## Google Play Compliance

This app is designed to comply with Google Play policies:

- Uses approved dependencies (JavaMail, AndroidX)
- Requests only necessary permissions
- Follows Material Design guidelines
- No background location access
- No unauthorized data collection
- Privacy policy included

## Troubleshooting

### Emails Not Sending

1. Check SMTP configuration
2. Test connection using "Test Connection" button
3. Verify internet connection
4. Check if app has internet permission
5. For Gmail: Ensure app password is correct
6. Check spam folder

### SMS Not Being Received

1. Grant SMS permissions
2. Ensure service is enabled (toggle on home screen)
3. Check if app is running (notification visible)
4. Restart the app
5. Check battery optimization settings

### Service Stops Automatically

1. Disable battery optimization for the app
2. Add app to auto-start list (device-specific)
3. Lock app in recent apps (device-specific)
4. Check if foreground notification is visible

### Dual SIM Not Detected

1. Ensure both SIM cards are active
2. Check if device supports dual SIM
3. Grant all required permissions
4. Restart the app

## Building for Production

```bash
# Create release build
./gradlew assembleRelease

# Create signed APK (configure signing in build.gradle)
./gradlew bundleRelease
```

## Contributing

This is a proprietary application developed by Flumenis LLC. For feature requests or bug reports, please contact support.

## License

Copyright © 2025 Flumenis LLC, Delaware. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited.

## Support

For support, feature requests, or bug reports:
- Email: support@flumenis.com
- Website: https://flumenis.com

## Changelog

### Version 1.0.0 (2025)
- Initial release
- Dual SIM support
- Custom SMTP configuration
- Template system with variables
- Configuration import/export
- Material Design 3 UI
- Foreground service keep-alive
- Google Play compatible

---

**Made with ❤️ by Flumenis LLC, Delaware**
