package com.flumenis.sms2email.service

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.util.Log
import com.flumenis.sms2email.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Monitor call logs and forward to email
 * Requires READ_CALL_LOG permission
 */
class CallLogMonitor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastCallId: Long = 0

    private val callLogObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            scope.launch {
                checkNewCalls()
            }
        }
    }

    fun startMonitoring() {
        context.contentResolver.registerContentObserver(
            CallLog.Calls.CONTENT_URI,
            true,
            callLogObserver
        )

        // Get the latest call ID to avoid processing old calls
        scope.launch {
            lastCallId = getLatestCallId()
        }
    }

    fun stopMonitoring() {
        context.contentResolver.unregisterContentObserver(callLogObserver)
    }

    private suspend fun checkNewCalls() {
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.CACHED_NAME
                ),
                "${CallLog.Calls._ID} > ?",
                arrayOf(lastCallId.toString()),
                "${CallLog.Calls._ID} DESC"
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls._ID))
                        val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: "Unknown"
                        val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                        val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                        val duration = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                        val name = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)) ?: number

                        if (id > lastCallId) {
                            lastCallId = id
                            processCall(number, name, type, date, duration)
                        }
                    } while (it.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking new calls", e)
        }
    }

    private suspend fun processCall(
        number: String,
        name: String,
        type: Int,
        date: Long,
        duration: Long
    ) {
        val preferencesManager = PreferencesManager(context)
        val config = preferencesManager.emailConfigFlow.first()

        if (!config.enabled) return

        val callType = when (type) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            CallLog.Calls.REJECTED_TYPE -> "Rejected"
            else -> "Unknown"
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date(date))

        val durationFormatted = formatDuration(duration)

        val subject = "Call Log: $callType call ${if (type == CallLog.Calls.OUTGOING_TYPE) "to" else "from"} $name"

        val body = """
            <b>Call Log Entry</b><br>
            <br>
            Type: $callType<br>
            ${if (type == CallLog.Calls.OUTGOING_TYPE) "To" else "From"}: $name<br>
            Number: $number<br>
            Time: $timestamp<br>
            Duration: $durationFormatted<br>
            <br>
            <i>Sent via PostaFide by Flumenis LLC</i>
        """.trimIndent()

        try {
            val emailService = EmailService(context)
            val smsMessage = com.flumenis.sms2email.data.SmsMessage(
                sender = number,
                message = body,
                timestamp = date,
                simSlot = -1,
                carrierName = ""
            )

            // Use custom subject for call logs
            val customConfig = config.copy(
                subjectTemplate = subject,
                bodyTemplate = body
            )

            emailService.sendEmail(smsMessage, customConfig)
            Log.d(TAG, "Call log email sent for $callType call from $number")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending call log email", e)
        }
    }

    private fun getLatestCallId(): Long {
        return try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls._ID),
                null,
                null,
                "${CallLog.Calls._ID} DESC LIMIT 1"
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    it.getLong(it.getColumnIndexOrThrow(CallLog.Calls._ID))
                } else {
                    0L
                }
            } ?: 0L
        } catch (e: Exception) {
            Log.e(TAG, "Error getting latest call ID", e)
            0L
        }
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, secs)
            minutes > 0 -> String.format("%d:%02d", minutes, secs)
            else -> "${secs}s"
        }
    }

    companion object {
        private const val TAG = "CallLogMonitor"
    }
}
