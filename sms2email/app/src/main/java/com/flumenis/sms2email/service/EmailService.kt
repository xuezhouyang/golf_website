package com.flumenis.sms2email.service

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.flumenis.sms2email.data.EmailConfig
import com.flumenis.sms2email.data.SmsMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Email service for sending SMS via SMTP
 */
class EmailService(private val context: Context) {

    /**
     * Send SMS message via email using configured SMTP settings
     */
    suspend fun sendEmail(smsMessage: SmsMessage, config: EmailConfig): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!config.enabled) {
                return@withContext Result.failure(Exception("Email service is not enabled"))
            }

            if (config.smtpHost.isBlank() || config.toEmail.isBlank()) {
                return@withContext Result.failure(Exception("SMTP configuration incomplete"))
            }

            val properties = Properties().apply {
                put("mail.smtp.host", config.smtpHost)
                put("mail.smtp.port", config.smtpPort.toString())
                put("mail.smtp.auth", "true")

                if (config.smtpUseSsl) {
                    put("mail.smtp.ssl.enable", "true")
                    put("mail.smtp.socketFactory.port", config.smtpPort.toString())
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                } else if (config.smtpUseTls) {
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.starttls.required", "true")
                }

                put("mail.smtp.timeout", "30000")
                put("mail.smtp.connectiontimeout", "30000")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(config.smtpUsername, config.smtpPassword)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(config.fromEmail, config.fromName))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.toEmail))

                // Process subject template
                subject = processTemplate(config.subjectTemplate, smsMessage, context)

                // Process body template
                setText(processTemplate(config.bodyTemplate, smsMessage, context), "UTF-8", "html")
            }

            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Process template with variables
     */
    private fun processTemplate(template: String, sms: SmsMessage, context: Context): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeOnlyFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timestamp = Date(sms.timestamp)

        // Get contact name if available
        val contactName = getContactName(context, sms.sender) ?: sms.sender

        // Get device information
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        val osName = "Android ${Build.VERSION.RELEASE}"

        // Get phone number (if available)
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val phoneNumber = try {
            telephonyManager?.line1Number ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }

        return template
            // Standard variables
            .replace("{{sender}}", sms.sender)
            .replace("{{message}}", sms.message)
            .replace("{{timestamp}}", dateFormat.format(timestamp))
            .replace("{{sim_slot}}", if (sms.simSlot >= 0) "SIM ${sms.simSlot + 1}" else "Unknown")
            .replace("{{carrier_name}}", sms.carrierName.ifBlank { "Unknown" })
            .replace("{{date}}", dateOnlyFormat.format(timestamp))
            .replace("{{time}}", timeOnlyFormat.format(timestamp))
            // Enhanced variables (user requested format)
            .replace("{{Text}}", sms.message)
            .replace("{{ContactName}}", contactName)
            .replace("{{FromNumber}}", sms.sender)
            .replace("{{ToNumber}}", phoneNumber)
            .replace("{{OccurredAt}}", dateFormat.format(timestamp))
            .replace("{{OsName}}", osName)
            .replace("{{DeviceName}}", deviceName)
            // HTML formatting support
            .replace("<b>", "<b>")
            .replace("</b>", "</b>")
            .replace("<br>", "<br/>")
    }

    /**
     * Get contact name from phone number
     */
    private fun getContactName(context: Context, phoneNumber: String): String? {
        return try {
            val uri = android.net.Uri.withAppendedPath(
                android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(phoneNumber)
            )
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME),
                null,
                null,
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    it.getString(0)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Test SMTP connection
     */
    suspend fun testConnection(config: EmailConfig): Result<String> = withContext(Dispatchers.IO) {
        try {
            val properties = Properties().apply {
                put("mail.smtp.host", config.smtpHost)
                put("mail.smtp.port", config.smtpPort.toString())
                put("mail.smtp.auth", "true")

                if (config.smtpUseSsl) {
                    put("mail.smtp.ssl.enable", "true")
                } else if (config.smtpUseTls) {
                    put("mail.smtp.starttls.enable", "true")
                }

                put("mail.smtp.timeout", "10000")
                put("mail.smtp.connectiontimeout", "10000")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(config.smtpUsername, config.smtpPassword)
                }
            })

            val transport = session.getTransport("smtp")
            transport.connect()
            transport.close()

            Result.success("Connection successful!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
