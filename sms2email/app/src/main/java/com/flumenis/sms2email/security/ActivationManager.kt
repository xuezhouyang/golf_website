package com.flumenis.sms2email.security

import android.content.Context
import android.provider.Settings
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.flumenis.sms2email.security.ActivationCodeGenerator.SubscriptionTier
import com.flumenis.sms2email.security.ActivationCodeGenerator.VerificationResult

/**
 * Activation Manager with anti-brute-force protection
 *
 * Features:
 * - 10 failed attempts → 24-hour lockout
 * - Exponential delay after each failure
 * - Device-bound activation
 * - Persistent activation state
 */
class ActivationManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "activation_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACTIVATION_CODE = "activation_code"
        private const val KEY_SUBSCRIPTION_TIER = "subscription_tier"
        private const val KEY_ISSUED_AT = "issued_at"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_ACTIVATED_AT = "activated_at"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LAST_FAILED_AT = "last_failed_at"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"

        private const val MAX_ATTEMPTS = 10
        private const val LOCKOUT_DURATION = 24 * 60 * 60 * 1000L // 24小时
        private const val BASE_DELAY = 1000L // 基础延迟1秒
    }

    /**
     * Get device ID (Android ID)
     */
    private fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown_device"
    }

    /**
     * Get device model
     */
    private fun getDeviceModel(): String {
        return "${Build.MANUFACTURER}_${Build.MODEL}".replace(" ", "_")
    }

    /**
     * Check if account is locked out
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

        if (remaining <= 0) return "0m"

        val hours = remaining / (60 * 60 * 1000)
        val minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000)

        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    /**
     * Get remaining attempts
     */
    fun getAttemptsRemaining(): Int {
        if (isLockedOut()) return 0
        val failed = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        return (MAX_ATTEMPTS - failed).coerceAtLeast(0)
    }

    /**
     * Calculate delay before next attempt (exponential backoff)
     */
    private fun calculateDelay(): Long {
        val failed = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        return BASE_DELAY * (1 shl failed.coerceIn(0, 6)) // 最多64秒延迟
    }

    /**
     * Activate with activation code
     *
     * @param code Activation code (format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX)
     * @return Activation result
     */
    suspend fun activate(code: String): ActivationResult {
        // 1. 检查锁定状态
        if (isLockedOut()) {
            return ActivationResult.LockedOut(getRemainingLockoutTime())
        }

        // 2. 检查是否需要延迟（防爆破）
        val lastFailed = encryptedPrefs.getLong(KEY_LAST_FAILED_AT, 0)
        val timeSinceLastFailed = System.currentTimeMillis() - lastFailed
        val requiredDelay = calculateDelay()

        if (timeSinceLastFailed < requiredDelay) {
            val waitTime = (requiredDelay - timeSinceLastFailed) / 1000
            return ActivationResult.TooManyAttempts(waitTime.toInt())
        }

        // 3. 验证激活码
        val deviceId = getDeviceId()
        val deviceModel = getDeviceModel()

        val result = ActivationCodeGenerator.verifyCode(code, deviceId, deviceModel)

        when (result) {
            is VerificationResult.Valid -> {
                // 激活成功
                encryptedPrefs.edit()
                    .putString(KEY_ACTIVATION_CODE, code)
                    .putInt(KEY_SUBSCRIPTION_TIER, result.tier.code)
                    .putLong(KEY_ISSUED_AT, result.issuedAt)
                    .putLong(KEY_EXPIRES_AT, result.expiresAt)
                    .putLong(KEY_ACTIVATED_AT, System.currentTimeMillis())
                    .putInt(KEY_FAILED_ATTEMPTS, 0)
                    .putLong(KEY_LAST_FAILED_AT, 0)
                    .putLong(KEY_LOCKOUT_UNTIL, 0)
                    .apply()

                return ActivationResult.Success(result.tier, result.expiresAt)
            }

            is VerificationResult.Expired -> {
                // 激活码已过期
                recordFailedAttempt()
                return ActivationResult.Expired
            }

            is VerificationResult.Invalid -> {
                // 激活码无效
                recordFailedAttempt()
                val remaining = getAttemptsRemaining()

                return if (remaining <= 0) {
                    ActivationResult.LockedOut(getRemainingLockoutTime())
                } else {
                    ActivationResult.InvalidCode(result.reason, remaining)
                }
            }
        }
    }

    /**
     * Record failed attempt
     */
    private fun recordFailedAttempt() {
        val failed = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        val now = System.currentTimeMillis()

        encryptedPrefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, failed)
            .putLong(KEY_LAST_FAILED_AT, now)
            .apply()

        // 达到最大次数，锁定账户
        if (failed >= MAX_ATTEMPTS) {
            encryptedPrefs.edit()
                .putLong(KEY_LOCKOUT_UNTIL, now + LOCKOUT_DURATION)
                .apply()
        }
    }

    /**
     * Check if activated and subscription is active
     */
    fun isActivated(): Boolean {
        val code = encryptedPrefs.getString(KEY_ACTIVATION_CODE, null) ?: return false
        val expiresAt = encryptedPrefs.getLong(KEY_EXPIRES_AT, 0)

        // 检查是否过期
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime < expiresAt
    }

    /**
     * Get current subscription tier
     */
    fun getSubscriptionTier(): SubscriptionTier? {
        if (!isActivated()) return null

        val tierCode = encryptedPrefs.getInt(KEY_SUBSCRIPTION_TIER, -1)
        return SubscriptionTier.values().find { it.code == tierCode }
    }

    /**
     * Get activation info
     */
    fun getActivationInfo(): ActivationInfo? {
        if (!isActivated()) return null

        val code = encryptedPrefs.getString(KEY_ACTIVATION_CODE, null) ?: return null
        val tier = getSubscriptionTier() ?: return null
        val issuedAt = encryptedPrefs.getLong(KEY_ISSUED_AT, 0)
        val expiresAt = encryptedPrefs.getLong(KEY_EXPIRES_AT, 0)
        val activatedAt = encryptedPrefs.getLong(KEY_ACTIVATED_AT, 0)

        return ActivationInfo(
            code = code,
            tier = tier,
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            activatedAt = activatedAt
        )
    }

    /**
     * Get activated code (last 8 characters for display)
     */
    fun getActivatedCode(): String? {
        val code = encryptedPrefs.getString(KEY_ACTIVATION_CODE, null) ?: return null
        return if (code.length >= 8) {
            "****-****-****-${code.takeLast(11)}"
        } else {
            code
        }
    }

    /**
     * Deactivate (for testing or account reset)
     */
    fun deactivate() {
        encryptedPrefs.edit()
            .remove(KEY_ACTIVATION_CODE)
            .remove(KEY_SUBSCRIPTION_TIER)
            .remove(KEY_ISSUED_AT)
            .remove(KEY_EXPIRES_AT)
            .remove(KEY_ACTIVATED_AT)
            .apply()
    }

    /**
     * Reset failed attempts (admin function)
     */
    fun resetFailedAttempts() {
        encryptedPrefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LAST_FAILED_AT, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0)
            .apply()
    }

    /**
     * Get device info for code generation
     */
    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            deviceId = getDeviceId(),
            deviceModel = getDeviceModel()
        )
    }

    data class ActivationInfo(
        val code: String,
        val tier: SubscriptionTier,
        val issuedAt: Long,
        val expiresAt: Long,
        val activatedAt: Long
    )

    data class DeviceInfo(
        val deviceId: String,
        val deviceModel: String
    )

    sealed class ActivationResult {
        data class Success(val tier: SubscriptionTier, val expiresAt: Long) : ActivationResult()
        data class InvalidCode(val reason: String, val attemptsRemaining: Int) : ActivationResult()
        data class LockedOut(val remainingTime: String) : ActivationResult()
        data class TooManyAttempts(val waitSeconds: Int) : ActivationResult()
        object Expired : ActivationResult()
    }
}
