package com.flumenis.sms2email.data

import com.google.gson.annotations.SerializedName

/**
 * Email configuration for SMTP and POP3 settings
 */
data class EmailConfig(
    @SerializedName("smtp_host")
    val smtpHost: String = "",

    @SerializedName("smtp_port")
    val smtpPort: Int = 587,

    @SerializedName("smtp_username")
    val smtpUsername: String = "",

    @SerializedName("smtp_password")
    val smtpPassword: String = "",

    @SerializedName("smtp_use_tls")
    val smtpUseTls: Boolean = true,

    @SerializedName("smtp_use_ssl")
    val smtpUseSsl: Boolean = false,

    @SerializedName("from_email")
    val fromEmail: String = "",

    @SerializedName("from_name")
    val fromName: String = "SMS2Email",

    @SerializedName("to_email")
    val toEmail: String = "",

    @SerializedName("subject_template")
    val subjectTemplate: String = "SMS from {{sender}} ({{sim_slot}})",

    @SerializedName("body_template")
    val bodyTemplate: String = """
        |From: {{sender}}
        |SIM Slot: {{sim_slot}}
        |Time: {{timestamp}}
        |
        |Message:
        |{{message}}
    """.trimMargin(),

    @SerializedName("enabled")
    val enabled: Boolean = false
)

/**
 * SIM card information for dual SIM support
 */
data class SimInfo(
    @SerializedName("slot")
    val slot: Int,

    @SerializedName("carrier_name")
    val carrierName: String = "",

    @SerializedName("display_name")
    val displayName: String = ""
)

/**
 * SMS message data
 */
data class SmsMessage(
    @SerializedName("sender")
    val sender: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("sim_slot")
    val simSlot: Int = -1,

    @SerializedName("carrier_name")
    val carrierName: String = ""
)

/**
 * Application configuration for import/export
 */
data class AppConfig(
    @SerializedName("version")
    val version: Int = 1,

    @SerializedName("email_config")
    val emailConfig: EmailConfig = EmailConfig(),

    @SerializedName("filter_enabled")
    val filterEnabled: Boolean = false,

    @SerializedName("allowed_senders")
    val allowedSenders: List<String> = emptyList(),

    @SerializedName("blocked_senders")
    val blockedSenders: List<String> = emptyList(),

    @SerializedName("keyword_filters")
    val keywordFilters: List<String> = emptyList(),

    @SerializedName("created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @SerializedName("company")
    val company: String = "Flumenis LLC, Delaware"
)

/**
 * Template variables available for email templates
 */
object TemplateVariables {
    const val SENDER = "{{sender}}"
    const val MESSAGE = "{{message}}"
    const val TIMESTAMP = "{{timestamp}}"
    const val SIM_SLOT = "{{sim_slot}}"
    const val CARRIER_NAME = "{{carrier_name}}"
    const val DATE = "{{date}}"
    const val TIME = "{{time}}"

    val ALL_VARIABLES = listOf(
        SENDER to "Sender phone number",
        MESSAGE to "SMS message content",
        TIMESTAMP to "Full date and time",
        SIM_SLOT to "SIM card slot (1 or 2)",
        CARRIER_NAME to "Carrier/operator name",
        DATE to "Date only (YYYY-MM-DD)",
        TIME to "Time only (HH:mm:ss)"
    )
}
