package com.flumenis.sms2email.util

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * Phone Number Utilities
 *
 * Features:
 * - Phone number masking (privacy protection)
 * - Auto-detect country code based on device location
 * - Format phone numbers with country code
 * - Detect country from phone number
 */
object PhoneNumberUtils {

    /**
     * Country code mapping
     */
    private val COUNTRY_CODES = mapOf(
        "US" to "+1",
        "CA" to "+1",
        "CN" to "+86",
        "HK" to "+852",
        "TW" to "+886",
        "JP" to "+81",
        "KR" to "+82",
        "GB" to "+44",
        "FR" to "+33",
        "DE" to "+49",
        "IT" to "+39",
        "ES" to "+34",
        "RU" to "+7",
        "IN" to "+91",
        "AU" to "+61",
        "BR" to "+55",
        "SG" to "+65",
        "MY" to "+60",
        "TH" to "+66",
        "VN" to "+84",
        "PH" to "+63",
        "ID" to "+62"
    )

    /**
     * Get country code from device
     * Uses TelephonyManager and fallback to Locale
     */
    fun getDeviceCountryCode(context: Context): String {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            // Try to get from SIM card
            val simCountry = telephonyManager?.simCountryIso?.uppercase()
            if (!simCountry.isNullOrBlank() && COUNTRY_CODES.containsKey(simCountry)) {
                return COUNTRY_CODES[simCountry]!!
            }

            // Try to get from network
            val networkCountry = telephonyManager?.networkCountryIso?.uppercase()
            if (!networkCountry.isNullOrBlank() && COUNTRY_CODES.containsKey(networkCountry)) {
                return COUNTRY_CODES[networkCountry]!!
            }

            // Fallback to Locale
            val localeCountry = Locale.getDefault().country.uppercase()
            if (COUNTRY_CODES.containsKey(localeCountry)) {
                return COUNTRY_CODES[localeCountry]!!
            }
        } catch (e: Exception) {
            // Ignore and fallback
        }

        // Default to +1 (US/Canada)
        return "+1"
    }

    /**
     * Get country ISO code (2-letter) from device
     */
    fun getDeviceCountryISO(context: Context): String {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            // Try SIM country
            val simCountry = telephonyManager?.simCountryIso?.uppercase()
            if (!simCountry.isNullOrBlank()) {
                return simCountry
            }

            // Try network country
            val networkCountry = telephonyManager?.networkCountryIso?.uppercase()
            if (!networkCountry.isNullOrBlank()) {
                return networkCountry
            }

            // Fallback to Locale
            return Locale.getDefault().country.uppercase()
        } catch (e: Exception) {
            return "US"
        }
    }

    /**
     * Mask phone number for privacy
     * Examples:
     * - +1234567890 → +123****890
     * - 1234567890 → 123****890
     * - 12345 → 12345 (too short, no masking)
     */
    fun maskPhoneNumber(phoneNumber: String, maskChar: String = "x", visibleDigits: Int = 3): String {
        // Clean the phone number (keep only digits and +)
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }

        // If too short, don't mask
        if (cleaned.length <= visibleDigits * 2) {
            return cleaned
        }

        val hasPlus = cleaned.startsWith("+")
        val digits = cleaned.filter { it.isDigit() }

        // Calculate mask length
        val maskLength = (digits.length - visibleDigits * 2).coerceAtLeast(0)

        // Build masked number
        val prefix = if (hasPlus) "+" else ""
        val start = digits.take(visibleDigits)
        val mask = maskChar.repeat(maskLength)
        val end = digits.takeLast(visibleDigits)

        return "$prefix$start$mask$end"
    }

    /**
     * Format phone number with country code
     * Auto-detects if number already has country code
     */
    fun formatWithCountryCode(phoneNumber: String, countryCode: String): String {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }

        // Already has country code
        if (cleaned.startsWith("+")) {
            return cleaned
        }

        // Already has country code without +
        if (COUNTRY_CODES.values.any { cleaned.startsWith(it.drop(1)) }) {
            return "+$cleaned"
        }

        // Add country code
        return "$countryCode$cleaned"
    }

    /**
     * Get placeholder text for phone number input
     * Based on device country
     */
    fun getPhoneNumberPlaceholder(context: Context): String {
        val countryISO = getDeviceCountryISO(context)
        val countryCode = getDeviceCountryCode(context)

        return when (countryISO) {
            "CN" -> "$countryCode 138 xxxx 8888"
            "US", "CA" -> "$countryCode (555) xxxx-1234"
            "HK" -> "$countryCode 9123 xxxx"
            "TW" -> "$countryCode 912 xxx xxx"
            "JP" -> "$countryCode 90-xxxx-1234"
            "KR" -> "$countryCode 10-xxxx-5678"
            "GB" -> "$countryCode 7911 xxxx78"
            "FR" -> "$countryCode 6 12 34 xx 78"
            "DE" -> "$countryCode 151 xxxx 789"
            "AU" -> "$countryCode 4xx xxx 789"
            "IN" -> "$countryCode 98765 xxxx0"
            "SG" -> "$countryCode 9123 xxxx"
            else -> "$countryCode xxx xxxx xxx"
        }
    }

    /**
     * Extract country code from phone number
     */
    fun extractCountryCode(phoneNumber: String): String? {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }

        if (!cleaned.startsWith("+")) {
            return null
        }

        // Try to match known country codes
        return COUNTRY_CODES.values.find { cleaned.startsWith(it) }
    }

    /**
     * Format phone number for display
     * Auto-formats based on country
     */
    fun formatForDisplay(phoneNumber: String, countryISO: String? = null): String {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        val hasPlus = cleaned.startsWith("+")
        val digits = cleaned.filter { it.isDigit() }

        // Detect country from phone number if not provided
        val country = countryISO ?: run {
            val code = extractCountryCode(cleaned)
            COUNTRY_CODES.entries.find { it.value == code }?.key
        }

        // Format based on country
        return when (country) {
            "CN" -> {
                // China: +86 138 1234 5678
                if (digits.length == 13) {
                    "+${digits.substring(0, 2)} ${digits.substring(2, 5)} ${digits.substring(5, 9)} ${digits.substring(9)}"
                } else if (digits.length == 11) {
                    "${digits.substring(0, 3)} ${digits.substring(3, 7)} ${digits.substring(7)}"
                } else cleaned
            }
            "US", "CA" -> {
                // US/Canada: +1 (555) 123-4567
                if (digits.length == 11) {
                    "+${digits[0]} (${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7)}"
                } else if (digits.length == 10) {
                    "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
                } else cleaned
            }
            "HK" -> {
                // Hong Kong: +852 9123 4567
                if (digits.length == 11) {
                    "+${digits.substring(0, 3)} ${digits.substring(3, 7)} ${digits.substring(7)}"
                } else if (digits.length == 8) {
                    "${digits.substring(0, 4)} ${digits.substring(4)}"
                } else cleaned
            }
            else -> cleaned
        }
    }

    /**
     * Validate phone number format
     */
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }

        // Must have at least 8 digits
        val digitCount = cleaned.count { it.isDigit() }
        if (digitCount < 8) return false

        // Must not have more than 15 digits (E.164 standard)
        if (digitCount > 15) return false

        // If starts with +, must have valid country code
        if (cleaned.startsWith("+")) {
            return COUNTRY_CODES.values.any { cleaned.startsWith(it) }
        }

        return true
    }
}
