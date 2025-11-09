package com.flumenis.sms2email.util

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Validation utilities for user input
 */
object ValidationUtils {

    /**
     * Validate email address
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate phone number (international format)
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        if (phone.isBlank()) return false

        // Remove spaces, dashes, and parentheses
        val cleaned = phone.replace(Regex("[\\s\\-()]"), "")

        // Allow + prefix for international numbers
        val pattern = Pattern.compile("^\\+?[1-9]\\d{1,14}$")
        return pattern.matcher(cleaned).matches()
    }

    /**
     * Validate SMTP host
     */
    fun isValidHost(host: String): Boolean {
        if (host.isBlank()) return false

        // Check for valid domain or IP
        val domainPattern = Pattern.compile(
            "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
        )
        val ipPattern = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )

        return domainPattern.matcher(host).matches() || ipPattern.matcher(host).matches()
    }

    /**
     * Validate SMTP port
     */
    fun isValidPort(port: Int): Boolean {
        return port in 1..65535
    }

    /**
     * Validate activation code format
     * Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
     * Uses custom alphabet without confusing characters (0, O, 1, I removed)
     */
    fun isValidActivationCodeFormat(code: String): Boolean {
        if (code.isBlank()) return false

        // Remove any spaces and convert to uppercase
        val cleaned = code.trim().uppercase().replace(" ", "")

        // Check format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
        val parts = cleaned.split("-")
        if (parts.size != 5) return false

        // Each part must be exactly 5 characters
        if (parts.any { it.length != 5 }) return false

        // Valid characters: A-Z (except I, O) and 2-9 (no 0, 1)
        val validAlphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return parts.all { part ->
            part.all { char -> char in validAlphabet }
        }
    }

    /**
     * Validate invite code format (deprecated - use isValidActivationCodeFormat)
     */
    @Deprecated("Use isValidActivationCodeFormat instead")
    fun isValidInviteCodeFormat(code: String): Boolean {
        return isValidActivationCodeFormat(code)
    }

    /**
     * Sanitize input string (remove dangerous characters)
     */
    fun sanitizeInput(input: String): String {
        return input
            .replace(Regex("[<>\"']"), "")  // Remove HTML/SQL injection chars
            .trim()
    }

    /**
     * Validate and sanitize phone number for SMS forwarding
     */
    fun sanitizePhoneNumber(phone: String): String? {
        val cleaned = phone.replace(Regex("[\\s\\-()]"), "")
        return if (isValidPhoneNumber(cleaned)) cleaned else null
    }

    /**
     * Validate OAuth token format
     */
    fun isValidOAuthToken(token: String): Boolean {
        if (token.isBlank()) return false

        // OAuth tokens are typically alphanumeric with some special chars
        // Minimum length 20 characters
        return token.length >= 20 && token.matches(Regex("^[A-Za-z0-9._\\-]+$"))
    }

    /**
     * Validate email template
     */
    fun isValidEmailTemplate(template: String): Boolean {
        if (template.isBlank()) return false

        // Must contain sender placeholder
        return template.contains("\${sender}") || template.contains("\${message}")
    }

    /**
     * Error messages for validation failures
     */
    object ErrorMessages {
        const val INVALID_EMAIL = "Invalid email address format"
        const val INVALID_PHONE = "Invalid phone number format (use international format: +1234567890)"
        const val INVALID_HOST = "Invalid SMTP host (use domain name or IP address)"
        const val INVALID_PORT = "Invalid port number (must be 1-65535)"
        const val INVALID_ACTIVATION_CODE = "Invalid activation code format (expected: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX)"
        const val INVALID_INVITE_CODE = "Invalid activation code format (expected: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX)"
        const val INVALID_OAUTH_TOKEN = "Invalid OAuth token (minimum 20 characters)"
        const val EMPTY_FIELD = "This field cannot be empty"
        const val INVALID_TEMPLATE = "Template must contain \${sender} or \${message} placeholder"
    }
}
