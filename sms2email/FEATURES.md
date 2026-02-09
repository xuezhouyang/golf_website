# SMS2Email - Complete Feature List

## 🎯 Core Features (v1.0)

### 1. Dual SIM Support
- ✅ Automatic detection of SIM slot (SIM 1 / SIM 2)
- ✅ Carrier name identification
- ✅ Works on all dual SIM Android devices
- ✅ No root required

### 2. Custom SMTP Configuration
- ✅ Configure any SMTP server (Gmail, Outlook, custom)
- ✅ TLS/SSL encryption support
- ✅ Custom ports (587, 465, or custom)
- ✅ App password support for Gmail
- ✅ Test connection feature
- ✅ Secure credential storage (Android DataStore)

### 3. Email Template System
- ✅ Customizable subject line
- ✅ Customizable email body
- ✅ HTML formatting support
- ✅ Rich variable system (15+ variables)
- ✅ Copy-to-clipboard for variables
- ✅ Real-time template preview

#### Supported Variables:
**Standard Variables:**
- `{{sender}}` - Phone number
- `{{message}}` - SMS content
- `{{timestamp}}` - Full datetime
- `{{sim_slot}}` - SIM card slot
- `{{carrier_name}}` - Carrier/operator
- `{{date}}` - Date only
- `{{time}}` - Time only

**Enhanced Variables:**
- `{{Text}}` - Message text
- `{{ContactName}}` - Contact from address book
- `{{FromNumber}}` - Sender number
- `{{ToNumber}}` - Device number
- `{{OccurredAt}}` - Timestamp
- `{{OsName}}` - Android version
- `{{DeviceName}}` - Device model

**HTML Tags:**
- `<b>text</b>` - Bold
- `<i>text</i>` - Italic
- `<br>` - Line break

### 4. Configuration Management
- ✅ Export configuration as JSON
- ✅ Import configuration from JSON
- ✅ Backup and restore settings
- ✅ Share config between devices
- ✅ Version tracking

### 5. Keep-Alive System
- ✅ Foreground service with notification
- ✅ Boot receiver (auto-start)
- ✅ START_STICKY service recovery
- ✅ Wake lock for processing
- ✅ WorkManager integration (planned)
- ✅ Battery optimization hints

### 6. Message Filtering
- ✅ Whitelist (allow specific senders)
- ✅ Blacklist (block specific senders)
- ✅ Keyword filtering
- ✅ Enable/disable filters
- ✅ Regex support (planned)

## 📱 User Interface

### Material Design 3
- ✅ Dynamic color theming (Android 12+)
- ✅ Dark mode support
- ✅ Smooth animations
- ✅ Responsive layouts
- ✅ Accessibility features
- ✅ Modern typography

### Screens
1. **Home Screen**
   - Service status toggle
   - Quick actions
   - Config summary
   - Import/Export buttons

2. **Settings Screen**
   - SMTP configuration
   - Security options
   - Email addresses
   - Connection testing

3. **Template Screen**
   - Variable reference
   - Subject editor
   - Body editor
   - HTML support guide

4. **About Screen**
   - Version info
   - Features list
   - Company info
   - Privacy policy

## 🌍 Internationalization (i18n)

### Supported Languages
- ✅ English (en)
- ✅ Simplified Chinese (zh-CN)
- 🔄 More languages planned

### Localized Elements
- All UI text
- Error messages
- Notifications
- Email templates (user customizable)

## 📞 Call Log Monitoring (Premium Feature)

### Features
- ✅ Monitor incoming calls
- ✅ Monitor outgoing calls
- ✅ Monitor missed calls
- ✅ Contact name lookup
- ✅ Call duration tracking
- ✅ Formatted email reports

### Email Format
```
Subject: Call Log: [Type] call from/to [Contact]
Body:
- Type: Incoming/Outgoing/Missed
- Contact: Name (if available)
- Number: Phone number
- Time: Timestamp
- Duration: HH:MM:SS
```

## 💳 Premium Features (Stripe Integration Ready)

### Architecture
- ✅ Billing manager class
- ✅ Feature flag system
- ✅ Premium status checking
- ✅ Subscription restoration
- 🔄 Stripe SDK integration (pending)

### Free Tier
- ✓ Up to 50 SMS forwards per day
- ✓ Basic templates
- ✓ Single recipient
- ✓ Standard keep-alive

### Premium Tier
- ✓ Unlimited SMS forwards
- ✓ Call log monitoring
- ✓ Advanced filters
- ✓ Multiple recipients
- ✓ Advanced keep-alive modes
- ✓ Custom retry logic
- ✓ Priority support

### Stripe Integration Points
```kotlin
// Ready for integration:
const val STRIPE_PUBLISHABLE_KEY = "pk_live_XXXXX"
const val PRICE_ID_MONTHLY = "price_XXXXX"
const val PRICE_ID_YEARLY = "price_XXXXX"

// Functions to implement:
- purchasePremium()
- restorePurchases()
- checkSubscriptionStatus()
```

## 🔧 Advanced Settings (Planned)

### Battery Management
- Normal mode (standard Android)
- Advanced mode (accessibility service)
- Device admin mode (maximum reliability)
- Custom wake intervals

### Network Optimization
- WiFi only mode
- Mobile data allowed
- Low battery behavior
- Rate limiting per hour/day

### Keep-Alive Strategies
1. **Level 1 (Normal)**
   - Foreground service
   - Boot receiver

2. **Level 2 (Advanced)**
   - + Accessibility service
   - + WorkManager periodic tasks

3. **Level 3 (Maximum)**
   - + Device admin
   - + App lock prevention
   - + Custom watchdog

## 🏗️ Technical Architecture

### Technology Stack
```
Language: Kotlin 1.9.22
UI: Jetpack Compose + Material 3
Architecture: MVVM
Storage: DataStore Preferences
Email: JavaMail API 1.6.7
Async: Coroutines + Flow
Min SDK: 24 (Android 7.0)
Target SDK: 34 (Android 14)
```

### Project Structure
```
app/
├── data/              # Data models
├── service/           # Background services
├── ui/               # Compose UI
│   ├── screens/      # Screen composables
│   ├── theme/        # Material Design 3
│   └── navigation/   # Navigation graph
├── util/             # Utilities
└── billing/          # Stripe integration
```

### Dependencies (Google Play Approved)
- androidx.core:core-ktx
- androidx.compose.material3
- androidx.navigation:navigation-compose
- androidx.room:room-ktx
- androidx.work:work-runtime-ktx
- com.sun.mail:android-mail (JavaMail)
- com.google.code.gson:gson

## 🔒 Security & Privacy

### Data Protection
- Credentials encrypted in DataStore
- No third-party analytics
- No ads or tracking
- Direct SMTP connection only
- Local config storage

### Permissions
- RECEIVE_SMS - Receive messages
- READ_SMS - Read messages
- READ_CALL_LOG - Call monitoring
- READ_CONTACTS - Contact names
- INTERNET - Email sending
- FOREGROUND_SERVICE - Keep-alive
- BOOT_COMPLETED - Auto-start

## 📊 Google Play Compliance

### Checklist
- ✅ Privacy policy included
- ✅ Permissions justified
- ✅ No background location
- ✅ Approved dependencies only
- ✅ Material Design compliance
- ✅ Accessibility support
- ✅ 64-bit native code (N/A)
- ✅ Target SDK 33+

## 🚀 Future Enhancements

### Planned Features
- [ ] MMS support
- [ ] Image attachments
- [ ] Scheduled forwarding
- [ ] Multiple SMTP profiles
- [ ] Cloud sync
- [ ] Backup to cloud storage
- [ ] Advanced regex filters
- [ ] Statistics dashboard
- [ ] Widget support
- [ ] Tasker integration

### Premium Features Pipeline
- [ ] WhatsApp backup integration
- [ ] Telegram forwarding
- [ ] Custom email servers
- [ ] API access
- [ ] Webhooks

## 📝 Development Status

### Completed (v1.0)
✅ Core SMS forwarding
✅ Dual SIM support
✅ SMTP configuration
✅ Template system
✅ Config import/export
✅ Material Design 3 UI
✅ i18n (EN/ZH)
✅ Call log monitoring
✅ Billing architecture

### In Progress
🔄 Stripe integration
🔄 Advanced keep-alive modes
🔄 Rate limiting UI

### Testing Required
⚠️ Dual SIM devices
⚠️ Various SMTP servers
⚠️ Android 7-14 compatibility
⚠️ Battery optimization
⚠️ Call log permissions

## 📞 Support & Contact

**Developer:** Flumenis LLC, Delaware
**Email:** support@flumenis.com
**Website:** https://flumenis.com

---

*Last Updated: 2025-11-09*
*Version: 1.0.0*
