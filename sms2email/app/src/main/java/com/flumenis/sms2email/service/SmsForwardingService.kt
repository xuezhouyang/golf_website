package com.flumenis.sms2email.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import com.flumenis.sms2email.data.PreferencesManager
import com.flumenis.sms2email.security.InviteCodeManager
import kotlinx.coroutines.flow.first

/**
 * SMS Forwarding Service (Premium Feature)
 *
 * Forwards incoming SMS to specified phone numbers
 * Supports dual SIM card slot selection
 */
class SmsForwardingService(private val context: Context) {

    private val preferencesManager = PreferencesManager(context)
    private val inviteCodeManager = InviteCodeManager(context)

    companion object {
        private const val TAG = "SmsForwardingService"
        const val ACTION_SMS_SENT = "com.flumenis.sms2email.SMS_SENT"
        const val ACTION_SMS_DELIVERED = "com.flumenis.sms2email.SMS_DELIVERED"
    }

    /**
     * Forward SMS to specified number
     *
     * @param originalMessage The original SMS message received
     * @param sender The sender of the original message
     * @param targetNumber The number to forward to
     * @param simSlot The SIM card slot to use for sending (0 or 1, -1 for default)
     */
    suspend fun forwardSms(
        originalMessage: String,
        sender: String,
        targetNumber: String,
        simSlot: Int = -1
    ): Result<Unit> {
        return try {
            // Check if premium is activated
            if (!inviteCodeManager.isActivated()) {
                return Result.failure(Exception("SMS forwarding requires premium subscription"))
            }

            // Validate target number
            if (targetNumber.isBlank()) {
                return Result.failure(Exception("Target number is empty"))
            }

            // Format the forwarded message
            val forwardedMessage = "Forwarded from $sender:\n$originalMessage"

            // Send SMS using specified SIM slot
            if (simSlot >= 0) {
                sendSmsWithSimSlot(targetNumber, forwardedMessage, simSlot)
            } else {
                sendSms(targetNumber, forwardedMessage)
            }

            Log.d(TAG, "SMS forwarded to $targetNumber via SIM slot $simSlot")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error forwarding SMS", e)
            Result.failure(e)
        }
    }

    /**
     * Send SMS using default SIM
     */
    private fun sendSms(destinationNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()

            // Split message if too long
            val parts = smsManager.divideMessage(message)

            // Create pending intents for delivery
            val sentIntents = ArrayList<PendingIntent>()
            val deliveredIntents = ArrayList<PendingIntent>()

            parts.forEach { _ ->
                sentIntents.add(
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_SMS_SENT),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                deliveredIntents.add(
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_SMS_DELIVERED),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }

            // Send SMS
            if (parts.size == 1) {
                smsManager.sendTextMessage(
                    destinationNumber,
                    null,
                    message,
                    sentIntents[0],
                    deliveredIntents[0]
                )
            } else {
                smsManager.sendMultipartTextMessage(
                    destinationNumber,
                    null,
                    parts,
                    sentIntents,
                    deliveredIntents
                )
            }

            Log.d(TAG, "SMS sent to $destinationNumber (${parts.size} parts)")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS", e)
            throw e
        }
    }

    /**
     * Send SMS using specific SIM card slot (Dual SIM support)
     */
    private fun sendSmsWithSimSlot(destinationNumber: String, message: String, simSlot: Int) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager

                subscriptionManager?.let { sm ->
                    val subscriptionInfoList = sm.activeSubscriptionInfoList
                    val subscriptionInfo = subscriptionInfoList?.find { it.simSlotIndex == simSlot }

                    if (subscriptionInfo != null) {
                        val smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.subscriptionId)

                        // Split message if too long
                        val parts = smsManager.divideMessage(message)

                        // Create pending intents
                        val sentIntents = ArrayList<PendingIntent>()
                        val deliveredIntents = ArrayList<PendingIntent>()

                        parts.forEach { _ ->
                            sentIntents.add(
                                PendingIntent.getBroadcast(
                                    context,
                                    0,
                                    Intent(ACTION_SMS_SENT),
                                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            )
                            deliveredIntents.add(
                                PendingIntent.getBroadcast(
                                    context,
                                    0,
                                    Intent(ACTION_SMS_DELIVERED),
                                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            )
                        }

                        // Send SMS
                        if (parts.size == 1) {
                            smsManager.sendTextMessage(
                                destinationNumber,
                                null,
                                message,
                                sentIntents[0],
                                deliveredIntents[0]
                            )
                        } else {
                            smsManager.sendMultipartTextMessage(
                                destinationNumber,
                                null,
                                parts,
                                sentIntents,
                                deliveredIntents
                            )
                        }

                        Log.d(TAG, "SMS sent via SIM slot $simSlot")
                    } else {
                        throw Exception("SIM slot $simSlot not available")
                    }
                } ?: throw Exception("SubscriptionManager not available")
            } else {
                // Fallback to default SIM for older Android versions
                Log.w(TAG, "Dual SIM not supported on this Android version, using default SIM")
                sendSms(destinationNumber, message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS with SIM slot", e)
            throw e
        }
    }

    /**
     * Get available SIM slots
     */
    fun getAvailableSimSlots(): List<SimSlotInfo> {
        val slots = mutableListOf<SimSlotInfo>()

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager

                subscriptionManager?.activeSubscriptionInfoList?.forEach { info ->
                    slots.add(
                        SimSlotInfo(
                            slotIndex = info.simSlotIndex,
                            carrierName = info.carrierName?.toString() ?: "Unknown",
                            phoneNumber = info.number ?: "Unknown",
                            displayName = info.displayName?.toString() ?: "SIM ${info.simSlotIndex + 1}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting SIM slots", e)
        }

        return slots
    }

    data class SimSlotInfo(
        val slotIndex: Int,
        val carrierName: String,
        val phoneNumber: String,
        val displayName: String
    )
}
