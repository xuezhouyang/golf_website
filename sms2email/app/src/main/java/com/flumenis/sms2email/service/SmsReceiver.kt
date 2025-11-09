package com.flumenis.sms2email.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.telephony.SubscriptionManager
import android.util.Log
import com.flumenis.sms2email.data.PreferencesManager
import com.flumenis.sms2email.data.SmsMessage as AppSmsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Broadcast receiver for incoming SMS messages with dual SIM support
 */
class SmsReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isEmpty()) return

            // Get SIM slot information
            val simSlot = getSimSlot(intent)
            val carrierName = getCarrierName(context, simSlot)

            // Process each SMS message
            for (smsMessage in messages) {
                val sender = smsMessage.displayOriginatingAddress ?: smsMessage.originatingAddress ?: "Unknown"
                val messageBody = smsMessage.messageBody ?: ""
                val timestamp = smsMessage.timestampMillis

                val appMessage = AppSmsMessage(
                    sender = sender,
                    message = messageBody,
                    timestamp = timestamp,
                    simSlot = simSlot,
                    carrierName = carrierName
                )

                Log.d(TAG, "SMS received from $sender on SIM slot $simSlot: $messageBody")

                // Process the message asynchronously
                scope.launch {
                    processSmsMessage(context, appMessage)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing SMS", e)
        }
    }

    private suspend fun processSmsMessage(context: Context, smsMessage: AppSmsMessage) {
        try {
            val preferencesManager = PreferencesManager(context)
            val config = preferencesManager.emailConfigFlow.first()

            // Check SMS forwarding configuration (Premium feature)
            val smsForwardingPrefs = context.getSharedPreferences("sms_forwarding", Context.MODE_PRIVATE)
            val isForwardingEnabled = smsForwardingPrefs.getBoolean("enabled", false)
            val targetNumber = smsForwardingPrefs.getString("target_number", "") ?: ""
            val selectedSimSlot = smsForwardingPrefs.getInt("sim_slot", -1)

            // Execute SMS forwarding first if enabled
            if (isForwardingEnabled && targetNumber.isNotBlank()) {
                try {
                    val smsForwardingService = SmsForwardingService(context)
                    val forwardResult = smsForwardingService.forwardSms(
                        originalMessage = smsMessage.message,
                        sender = smsMessage.sender,
                        targetNumber = targetNumber,
                        simSlot = selectedSimSlot
                    )

                    forwardResult.onSuccess {
                        Log.d(TAG, "SMS forwarded successfully to $targetNumber via SIM slot $selectedSimSlot")
                    }.onFailure { error ->
                        Log.e(TAG, "Failed to forward SMS: ${error.message}", error)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in SMS forwarding", e)
                }
            }

            // Then process email forwarding
            if (!config.enabled) {
                Log.d(TAG, "Email forwarding is disabled")
                return
            }

            // Check filters
            val appConfig = preferencesManager.appConfigFlow.first()
            if (appConfig.filterEnabled) {
                // Check blocked senders
                if (appConfig.blockedSenders.any { it.isNotBlank() && smsMessage.sender.contains(it, ignoreCase = true) }) {
                    Log.d(TAG, "SMS from ${smsMessage.sender} is blocked")
                    return
                }

                // Check allowed senders (if list is not empty)
                if (appConfig.allowedSenders.isNotEmpty() &&
                    !appConfig.allowedSenders.any { it.isNotBlank() && smsMessage.sender.contains(it, ignoreCase = true) }) {
                    Log.d(TAG, "SMS from ${smsMessage.sender} is not in allowed list")
                    return
                }

                // Check keyword filters
                if (appConfig.keywordFilters.any { it.isNotBlank() && smsMessage.message.contains(it, ignoreCase = true) }) {
                    Log.d(TAG, "SMS contains blocked keywords")
                    return
                }
            }

            // Send email
            val emailService = EmailService(context)
            val result = emailService.sendEmail(smsMessage, config)

            result.onSuccess {
                Log.d(TAG, "Email sent successfully for SMS from ${smsMessage.sender}")
            }.onFailure { error ->
                Log.e(TAG, "Failed to send email", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in processSmsMessage", e)
        }
    }

    /**
     * Get SIM slot from intent (dual SIM support)
     */
    private fun getSimSlot(intent: Intent): Int {
        return try {
            // Try to get subscription ID (works on Android 5.1+)
            val subscription = intent.getIntExtra("subscription", -1)
            if (subscription >= 0) {
                // Convert subscription ID to slot index
                subscription
            } else {
                // Try alternative methods
                intent.getIntExtra("simId", -1)
                    .takeIf { it >= 0 }
                    ?: intent.getIntExtra("simSlot", -1)
                    .takeIf { it >= 0 }
                    ?: intent.getIntExtra("slot", -1)
                    .takeIf { it >= 0 }
                    ?: -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting SIM slot", e)
            -1
        }
    }

    /**
     * Get carrier name for SIM slot
     */
    private fun getCarrierName(context: Context, simSlot: Int): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                subscriptionManager?.let { sm ->
                    val activeSubscriptions = sm.activeSubscriptionInfoList
                    activeSubscriptions?.find { it.simSlotIndex == simSlot }?.carrierName?.toString()
                } ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting carrier name", e)
            "Unknown"
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
