# Add project specific ProGuard rules here.
# JavaMail
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn javax.mail.**
-dontwarn javax.activation.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes
-keep class com.flumenis.sms2email.data.** { *; }
-keepclassmembers class com.flumenis.sms2email.data.** { *; }

# Google Error Prone Annotations (used by Tink)
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.checker.nullness.qual.**

# Google Tink (for EncryptedSharedPreferences)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# AndroidX Security Crypto
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Kotlin Serialization/Reflection
-keep class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations
-keepattributes AnnotationDefault

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# Coil
-dontwarn coil.**
-keep class coil.** { *; }

# Accompanist
-dontwarn com.google.accompanist.**
-keep class com.google.accompanist.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep class androidx.work.impl.WorkManagerInitializer
-dontwarn androidx.work.**

# AndroidX Lifecycle
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-dontwarn androidx.compose.**

# Keep service and receiver classes
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keepclassmembers class * extends android.content.BroadcastReceiver {
    <init>(...);
}

# ===== Security Hardening =====

# Obfuscation settings
-repackageclasses 'o'
-allowaccessmodification
-optimizationpasses 5
-overloadaggressively

# Build performance optimization
-dontpreverify

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove printStackTrace in release
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

# Keep security manager but obfuscate internals
-keep class com.flumenis.sms2email.security.SecurityManager {
    public <methods>;
}
-keep class com.flumenis.sms2email.security.SecurityReport { *; }
-keep enum com.flumenis.sms2email.security.SecurityIssue { *; }

# Keep activation manager (already secured)
-keep class com.flumenis.sms2email.security.ActivationManager {
    public <methods>;
}

# Obfuscate sensitive implementations
-keepclassmembernames class com.flumenis.sms2email.security.** {
    private <fields>;
    private <methods>;
}

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# Remove debug information but keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Anti-tampering: Remove reflection metadata where possible
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Optimize and obfuscate string constants
-optimizations !code/simplification/string

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep KeepAlive components
-keep class com.flumenis.sms2email.service.KeepAliveManager { *; }
-keep class com.flumenis.sms2email.service.KeepAliveWorker { *; }
-keep class com.flumenis.sms2email.service.KeepAliveReceiver { *; }

# Anti-hijack manager
-keep class com.flumenis.sms2email.security.AntiHijackManager {
    public <methods>;
}
