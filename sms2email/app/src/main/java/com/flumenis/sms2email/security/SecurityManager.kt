package com.flumenis.sms2email.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Debug
import java.io.File

/**
 * Security Manager for PostaFide
 * Detects root, debugging, tampering, and other security threats
 */
class SecurityManager(private val context: Context) {

    /**
     * Comprehensive security check
     * @return SecurityReport with all detected issues
     */
    fun performSecurityCheck(): SecurityReport {
        val issues = mutableListOf<SecurityIssue>()

        // Check for root
        if (isRooted()) {
            issues.add(SecurityIssue.ROOT_DETECTED)
        }

        // Check for debugger
        if (isDebuggerAttached()) {
            issues.add(SecurityIssue.DEBUGGER_ATTACHED)
        }

        // Check for emulator
        if (isEmulator()) {
            issues.add(SecurityIssue.EMULATOR_DETECTED)
        }

        // Check for Xposed/Frida
        if (isXposedInstalled()) {
            issues.add(SecurityIssue.XPOSED_DETECTED)
        }

        // Check if app is debuggable
        if (isDebuggable()) {
            issues.add(SecurityIssue.DEBUGGABLE_BUILD)
        }

        return SecurityReport(
            isSecure = issues.isEmpty(),
            issues = issues,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Check if device is rooted
     */
    private fun isRooted(): Boolean {
        // Check for common root binaries
        val rootBinaries = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        for (path in rootBinaries) {
            if (File(path).exists()) {
                return true
            }
        }

        // Check for root management apps
        val rootApps = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )

        for (packageName in rootApps) {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                return true
            } catch (e: Exception) {
                // Package not found, continue
            }
        }

        // Check build tags
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        return false
    }

    /**
     * Check if debugger is attached
     */
    private fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    /**
     * Check if running on emulator
     */
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

    /**
     * Check for Xposed framework
     */
    private fun isXposedInstalled(): Boolean {
        try {
            // Check for Xposed installer
            context.packageManager.getPackageInfo("de.robv.android.xposed.installer", 0)
            return true
        } catch (e: Exception) {
            // Not installed
        }

        try {
            // Check for EdXposed
            context.packageManager.getPackageInfo("org.meowcat.edxposed.manager", 0)
            return true
        } catch (e: Exception) {
            // Not installed
        }

        // Check for Xposed bridge
        try {
            val clazz = Class.forName("de.robv.android.xposed.XposedBridge")
            if (clazz != null) {
                return true
            }
        } catch (e: Exception) {
            // Not found
        }

        // Check for stack traces
        try {
            throw Exception("detect")
        } catch (e: Exception) {
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className.contains("xposed")) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Check if app is debuggable
     */
    private fun isDebuggable(): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /**
     * Check for Frida
     */
    fun isFridaDetected(): Boolean {
        // Check for Frida libraries
        val maps = File("/proc/self/maps")
        if (maps.exists()) {
            try {
                val content = maps.readText()
                if (content.contains("frida") || content.contains("gum-js-loop")) {
                    return true
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        return false
    }
}

/**
 * Security report data class
 */
data class SecurityReport(
    val isSecure: Boolean,
    val issues: List<SecurityIssue>,
    val timestamp: Long
) {
    fun hasIssue(issue: SecurityIssue): Boolean {
        return issues.contains(issue)
    }

    fun getSummary(): String {
        return if (isSecure) {
            "Environment is secure"
        } else {
            val issueDescriptions = issues.joinToString(", ") { it.description }
            "Security issues detected: $issueDescriptions"
        }
    }
}

/**
 * Security issues enum
 */
enum class SecurityIssue(val description: String, val severity: Severity) {
    ROOT_DETECTED("Root access detected", Severity.HIGH),
    DEBUGGER_ATTACHED("Debugger attached", Severity.CRITICAL),
    EMULATOR_DETECTED("Running on emulator", Severity.MEDIUM),
    XPOSED_DETECTED("Xposed framework detected", Severity.HIGH),
    DEBUGGABLE_BUILD("Debug build detected", Severity.LOW),
    FRIDA_DETECTED("Frida framework detected", Severity.CRITICAL),
    TAMPERING_DETECTED("App tampering detected", Severity.CRITICAL);

    enum class Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
