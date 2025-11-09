package com.flumenis.sms2email.security

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Secure invite code manager with RSA verification
 *
 * Features:
 * - RSA 2048-bit signature verification
 * - Date-based offline generation
 * - Brute-force protection
 * - Encrypted storage
 *
 * Invite Code Format: CODE-YYYYMMDD-SIGNATURE
 * Example: ONEDAY-20250109-a3b5c7d9...
 */
class InviteCodeManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "invite_code_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val TAG = "InviteCodeManager"

        // Security settings
        private const val MAX_VERIFICATION_ATTEMPTS = 10
        private const val LOCKOUT_DURATION_HOURS = 24L
        private const val CODE_VALID_DAYS = 365L

        // Preference keys
        private const val KEY_ACTIVATED_CODE = "activated_code"
        private const val KEY_ACTIVATION_DATE = "activation_date"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"
        private const val KEY_DEVICE_FINGERPRINT = "device_fingerprint"

        /**
         * Developer's RSA Public Key (2048-bit)
         * This is used to verify invite code signatures
         *
         * IMPORTANT: Keep the private key secure and offline!
         * Use the companion script to generate codes.
         */
        private val DEVELOPER_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw8kZL5TpVGx3YmE9qKJmF4KXoN2vP7RuN5xQH8fM9yL2WpXdJ3rYvKmN4fQ8zE6bC3tL9wS5gH2jV8oP4xYzL9mR6kT3nF5wQ2xH7vL4pN8cB3tM9xW2vK5dR3jN7fT8qL6mV9wE4zH2nS7cJ3xV8yK4pT6nL2wR5vM9xB3tQ7zN2cH4rV6jL9wS3xK8pN5tM7zQ2vE9bR4cT6nL3wH5yJ8pM2xV7zK4rN6tQ9cL3vH5xJ2nR8yP4zT6wM9vE7bS3xK5pN2tL7rV6cH4zQ8wJ9mN3xT5vK7pR2yL4cH6zV9wE8jM3nS7xT4pK2vR5bL6cN9zH3wQ7xJ8pM2vT5yK4pL6zR3wN7vH9cQ2xJ5tM8bS6yK4pT3nL7rV9cH2zE6wQIDAQAB"

        // Default invite codes (hardcoded for initial release)
        private val DEFAULT_CODES = setOf(
            "ONEDAY",      // Primary default code
            "FLUMENIS",    // Company code
            "WELCOME2025"  // Promotional code
        )
    }

    /**
     * Verify and activate an invite code
     */
    suspend fun verifyInviteCode(code: String): Result<Boolean> {
        try {
            // Check if already activated
            if (isActivated()) {
                return Result.success(true)
            }

            // Check lockout status
            if (isLockedOut()) {
                val remainingTime = getRemainingLockoutTime()
                return Result.failure(
                    SecurityException("Too many attempts. Try again in $remainingTime")
                )
            }

            // Verify the code
            val isValid = when {
                // Check default codes first
                DEFAULT_CODES.contains(code.uppercase()) -> {
                    Log.d(TAG, "Default code verified: $code")
                    true
                }
                // Verify signed codes
                else -> verifySignedCode(code)
            }

            return if (isValid) {
                // Activate premium
                activateCode(code)
                resetFailedAttempts()
                Result.success(true)
            } else {
                // Increment failed attempts
                incrementFailedAttempts()
                Result.failure(Exception("Invalid invite code"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying invite code", e)
            incrementFailedAttempts()
            return Result.failure(e)
        }
    }

    /**
     * Verify a signed invite code using RSA signature
     *
     * Code format: CODENAME-YYYYMMDD-SIGNATURE
     */
    private fun verifySignedCode(code: String): Boolean {
        return try {
            val parts = code.split("-")
            if (parts.size != 3) {
                Log.d(TAG, "Invalid code format")
                return false
            }

            val codeName = parts[0]
            val dateStr = parts[1]
            val signatureB64 = parts[2]

            // Parse date
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val codeDate = dateFormat.parse(dateStr) ?: return false

            // Check if code is still valid (not expired)
            val daysSinceCode = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - codeDate.time
            )
            if (daysSinceCode > CODE_VALID_DAYS) {
                Log.d(TAG, "Code expired")
                return false
            }

            // Verify signature
            val message = "$codeName-$dateStr"
            val signature = Base64.decode(signatureB64, Base64.URL_SAFE or Base64.NO_WRAP)

            verifyRSASignature(message, signature)
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying signed code", e)
            false
        }
    }

    /**
     * Verify RSA signature using public key
     */
    private fun verifyRSASignature(message: String, signature: ByteArray): Boolean {
        return try {
            val publicKeyBytes = Base64.decode(DEVELOPER_PUBLIC_KEY, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicKey = keyFactory.generatePublic(keySpec)

            val sig = Signature.getInstance("SHA256withRSA")
            sig.initVerify(publicKey)
            sig.update(message.toByteArray())

            val isValid = sig.verify(signature)
            Log.d(TAG, "Signature verification: $isValid")
            isValid
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying RSA signature", e)
            false
        }
    }

    /**
     * Check if premium is activated
     */
    fun isActivated(): Boolean {
        val code = encryptedPrefs.getString(KEY_ACTIVATED_CODE, null)
        val activationDate = encryptedPrefs.getLong(KEY_ACTIVATION_DATE, 0)

        if (code.isNullOrBlank() || activationDate == 0L) {
            return false
        }

        // Verify device fingerprint to prevent code sharing
        val storedFingerprint = encryptedPrefs.getString(KEY_DEVICE_FINGERPRINT, null)
        val currentFingerprint = getDeviceFingerprint()

        if (storedFingerprint != currentFingerprint) {
            Log.w(TAG, "Device fingerprint mismatch - possible code sharing")
            return false
        }

        return true
    }

    /**
     * Get activated invite code
     */
    fun getActivatedCode(): String? {
        return encryptedPrefs.getString(KEY_ACTIVATED_CODE, null)
    }

    /**
     * Activate with invite code
     */
    private fun activateCode(code: String) {
        encryptedPrefs.edit()
            .putString(KEY_ACTIVATED_CODE, code)
            .putLong(KEY_ACTIVATION_DATE, System.currentTimeMillis())
            .putString(KEY_DEVICE_FINGERPRINT, getDeviceFingerprint())
            .apply()

        Log.i(TAG, "Invite code activated successfully")
    }

    /**
     * Deactivate (for testing or transfer)
     */
    fun deactivate() {
        encryptedPrefs.edit()
            .remove(KEY_ACTIVATED_CODE)
            .remove(KEY_ACTIVATION_DATE)
            .remove(KEY_DEVICE_FINGERPRINT)
            .apply()
    }

    /**
     * Check if locked out due to too many attempts
     */
    fun isLockedOut(): Boolean {
        val lockoutUntil = encryptedPrefs.getLong(KEY_LOCKOUT_UNTIL, 0)
        return System.currentTimeMillis() < lockoutUntil
    }

    /**
     * Get remaining lockout time
     */
    fun getRemainingLockoutTime(): String {
        val lockoutUntil = encryptedPrefs.getLong(KEY_LOCKOUT_UNTIL, 0)
        val remaining = lockoutUntil - System.currentTimeMillis()
        if (remaining <= 0) return "0h 0m"
        val hours = TimeUnit.MILLISECONDS.toHours(remaining)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60
        return "${hours}h ${minutes}m"
    }

    /**
     * Get lockout end time in milliseconds
     */
    fun getLockoutEndTime(): Long {
        return encryptedPrefs.getLong(KEY_LOCKOUT_UNTIL, 0)
    }

    /**
     * Increment failed verification attempts
     */
    private fun incrementFailedAttempts() {
        val attempts = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        encryptedPrefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply()

        if (attempts >= MAX_VERIFICATION_ATTEMPTS) {
            // Lock out for 24 hours
            val lockoutUntil = System.currentTimeMillis() +
                    TimeUnit.HOURS.toMillis(LOCKOUT_DURATION_HOURS)
            encryptedPrefs.edit()
                .putLong(KEY_LOCKOUT_UNTIL, lockoutUntil)
                .apply()

            Log.w(TAG, "Account locked out due to too many attempts")
        }
    }

    /**
     * Reset failed attempts
     */
    private fun resetFailedAttempts() {
        encryptedPrefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0)
            .apply()
    }

    /**
     * Generate device fingerprint
     * Used to prevent code sharing between devices
     */
    private fun getDeviceFingerprint(): String {
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )

        val deviceInfo = "${androidId}-${android.os.Build.MODEL}-${android.os.Build.MANUFACTURER}"

        return MessageDigest.getInstance("SHA-256")
            .digest(deviceInfo.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(32)
    }

    /**
     * Get verification attempts remaining
     */
    fun getAttemptsRemaining(): Int {
        val attempts = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        return maxOf(0, MAX_VERIFICATION_ATTEMPTS - attempts)
    }
}
