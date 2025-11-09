package com.flumenis.sms2email.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Permission Manager
 *
 * Manages all app permissions without directly requesting them.
 * Provides detection and guidance for users to grant permissions manually.
 *
 * Design Philosophy:
 * - No direct permission requests (no ActivityCompat.requestPermissions)
 * - Only detection and guidance
 * - Compatible dialogs for different Android versions
 * - User-friendly instructions
 */
class PermissionManager(private val context: Context) {

    companion object {
        // Core permissions for basic functionality
        val CORE_PERMISSIONS = listOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        )

        // Optional permissions for enhanced functionality
        val OPTIONAL_PERMISSIONS = buildList {
            add(Manifest.permission.READ_PHONE_STATE)
            add(Manifest.permission.READ_CONTACTS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // All permissions
        val ALL_PERMISSIONS = CORE_PERMISSIONS + OPTIONAL_PERMISSIONS
    }

    /**
     * Permission status
     */
    data class PermissionStatus(
        val permission: String,
        val isGranted: Boolean,
        val isRequired: Boolean,
        val displayName: String,
        val description: String,
        val reasoning: String
    )

    /**
     * Check if a specific permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if all core permissions are granted
     */
    fun hasCorePermissions(): Boolean {
        return CORE_PERMISSIONS.all { isPermissionGranted(it) }
    }

    /**
     * Check if all permissions (core + optional) are granted
     */
    fun hasAllPermissions(): Boolean {
        return ALL_PERMISSIONS.all { isPermissionGranted(it) }
    }

    /**
     * Get status for all permissions
     */
    fun getAllPermissionStatuses(): List<PermissionStatus> {
        return ALL_PERMISSIONS.map { permission ->
            val isRequired = permission in CORE_PERMISSIONS
            PermissionStatus(
                permission = permission,
                isGranted = isPermissionGranted(permission),
                isRequired = isRequired,
                displayName = getPermissionDisplayName(permission),
                description = getPermissionDescription(permission),
                reasoning = getPermissionReasoning(permission)
            )
        }
    }

    /**
     * Get status for a specific permission
     */
    fun getPermissionStatus(permission: String): PermissionStatus {
        val isRequired = permission in CORE_PERMISSIONS
        return PermissionStatus(
            permission = permission,
            isGranted = isPermissionGranted(permission),
            isRequired = isRequired,
            displayName = getPermissionDisplayName(permission),
            description = getPermissionDescription(permission),
            reasoning = getPermissionReasoning(permission)
        )
    }

    /**
     * Get missing permissions
     */
    fun getMissingPermissions(): List<String> {
        return ALL_PERMISSIONS.filter { !isPermissionGranted(it) }
    }

    /**
     * Get missing core permissions
     */
    fun getMissingCorePermissions(): List<String> {
        return CORE_PERMISSIONS.filter { !isPermissionGranted(it) }
    }

    /**
     * Get permission display name
     */
    private fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.RECEIVE_SMS -> "Receive SMS"
            Manifest.permission.SEND_SMS -> "Send SMS"
            Manifest.permission.READ_SMS -> "Read SMS"
            Manifest.permission.READ_PHONE_STATE -> "Phone State"
            Manifest.permission.READ_CONTACTS -> "Read Contacts"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            else -> permission.substringAfterLast(".")
        }
    }

    /**
     * Get permission description
     */
    private fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.RECEIVE_SMS -> "Monitor incoming SMS messages"
            Manifest.permission.SEND_SMS -> "Forward SMS messages to other numbers"
            Manifest.permission.READ_SMS -> "Read SMS content for email forwarding"
            Manifest.permission.READ_PHONE_STATE -> "Detect dual SIM cards and phone status"
            Manifest.permission.READ_CONTACTS -> "Show contact names instead of phone numbers"
            Manifest.permission.POST_NOTIFICATIONS -> "Display foreground service notifications"
            else -> "Unknown permission"
        }
    }

    /**
     * Get permission reasoning (why we need it)
     */
    private fun getPermissionReasoning(permission: String): String {
        return when (permission) {
            Manifest.permission.RECEIVE_SMS -> "Required to monitor incoming SMS messages and trigger email forwarding"
            Manifest.permission.SEND_SMS -> "Required for SMS forwarding feature (Premium)"
            Manifest.permission.READ_SMS -> "Required to read SMS content and metadata"
            Manifest.permission.READ_PHONE_STATE -> "Required for dual SIM support and device identification"
            Manifest.permission.READ_CONTACTS -> "Optional: Display contact names for better readability"
            Manifest.permission.POST_NOTIFICATIONS -> "Required on Android 13+ to show service status notifications"
            else -> "Unknown reasoning"
        }
    }

    /**
     * Get instructions for granting permission
     * Compatible with different Android versions
     */
    fun getPermissionInstructions(permission: String): String {
        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
        val permissionName = getPermissionDisplayName(permission)

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                """
                Android 13+ Instructions:
                1. Tap "Open Settings" below
                2. Find and tap "Permissions"
                3. Locate "$permissionName"
                4. Select "Allow" or "Allow all the time"
                5. Return to $appName
                """.trimIndent()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                """
                Android 6.0+ Instructions:
                1. Tap "Open Settings" below
                2. Find and tap "Permissions"
                3. Locate "$permissionName"
                4. Toggle the permission ON
                5. Return to $appName
                """.trimIndent()
            }
            else -> {
                """
                Older Android Instructions:
                1. Go to Settings → Apps
                2. Find "$appName"
                3. Tap "Permissions"
                4. Enable "$permissionName"
                5. Return to $appName
                """.trimIndent()
            }
        }
    }

    /**
     * Open app settings page
     */
    fun openAppSettings(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Open notification settings (Android 8.0+)
     */
    fun openNotificationSettings(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            openAppSettings()
        }
    }

    /**
     * Check if permission settings page exists
     */
    fun canOpenSettings(): Boolean {
        val intent = openAppSettings()
        return intent.resolveActivity(context.packageManager) != null
    }

    /**
     * Get human-readable summary of permission status
     */
    fun getPermissionSummary(): PermissionSummary {
        val total = ALL_PERMISSIONS.size
        val granted = ALL_PERMISSIONS.count { isPermissionGranted(it) }
        val coreGranted = CORE_PERMISSIONS.count { isPermissionGranted(it) }
        val coreTotal = CORE_PERMISSIONS.size

        return PermissionSummary(
            totalPermissions = total,
            grantedPermissions = granted,
            corePermissions = coreTotal,
            coreGranted = coreGranted,
            hasCorePermissions = coreGranted == coreTotal,
            hasAllPermissions = granted == total,
            missingCore = CORE_PERMISSIONS.filter { !isPermissionGranted(it) },
            missingOptional = OPTIONAL_PERMISSIONS.filter { !isPermissionGranted(it) }
        )
    }

    data class PermissionSummary(
        val totalPermissions: Int,
        val grantedPermissions: Int,
        val corePermissions: Int,
        val coreGranted: Int,
        val hasCorePermissions: Boolean,
        val hasAllPermissions: Boolean,
        val missingCore: List<String>,
        val missingOptional: List<String>
    )
}
