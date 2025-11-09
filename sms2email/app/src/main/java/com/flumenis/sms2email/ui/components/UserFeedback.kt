package com.flumenis.sms2email.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * User feedback manager for consistent UX
 */
object UserFeedback {

    /**
     * Show success message
     */
    fun showSuccess(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "✓ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }

    /**
     * Show error message
     */
    fun showError(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = "Retry",
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "✗ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }

    /**
     * Show info message
     */
    fun showInfo(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "ℹ $message",
                duration = duration
            )
        }
    }

    /**
     * Show warning message
     */
    fun showWarning(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = "Dismiss",
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "⚠ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }

    /**
     * Show loading message
     */
    fun showLoading(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String
    ) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "⏳ $message...",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    /**
     * Dismiss current snackbar
     */
    fun dismiss(snackbarHostState: SnackbarHostState) {
        snackbarHostState.currentSnackbarData?.dismiss()
    }
}

/**
 * Common feedback messages
 */
object FeedbackMessages {
    // Success messages
    const val SAVE_SUCCESS = "Settings saved successfully"
    const val EMAIL_SENT = "Email sent successfully"
    const val SMS_FORWARDED = "SMS forwarded successfully"
    const val BACKUP_SUCCESS = "Backup completed"
    const val RESTORE_SUCCESS = "Restore completed"
    const val PREMIUM_ACTIVATED = "Premium subscription activated"
    const val CONFIG_IMPORTED = "Configuration imported"
    const val CONFIG_EXPORTED = "Configuration exported"

    // Error messages
    const val SAVE_ERROR = "Failed to save settings"
    const val EMAIL_ERROR = "Failed to send email"
    const val SMS_ERROR = "Failed to forward SMS"
    const val BACKUP_ERROR = "Backup failed"
    const val RESTORE_ERROR = "Restore failed"
    const val NETWORK_ERROR = "Network connection failed"
    const val PERMISSION_ERROR = "Permission denied"
    const val INVALID_INPUT = "Invalid input provided"

    // Info messages
    const val CHECKING_PERMISSIONS = "Checking permissions"
    const val CONNECTING = "Connecting to server"
    const val PROCESSING = "Processing request"

    // Warning messages
    const val PREMIUM_REQUIRED = "This feature requires Premium subscription"
    const val NO_NETWORK = "No network connection available"
    const val LOW_BATTERY = "Low battery - background processing may be limited"
}
