package com.flumenis.sms2email.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Email Send Log Entity
 * Stores history of email send attempts
 * Retention: Last 7 days only
 */
@Entity(tableName = "email_logs")
data class EmailLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Unix timestamp (milliseconds) when email was sent */
    val timestamp: Long,

    /** Sender phone number or name */
    val sender: String,

    /** Email subject */
    val subject: String,

    /** Recipient email address */
    val toEmail: String,

    /** Whether the email was sent successfully */
    val success: Boolean,

    /** Error message if send failed */
    val errorMessage: String? = null,

    /** SIM slot (0 or 1 for dual SIM, -1 for unknown) */
    val simSlot: Int = -1
)
