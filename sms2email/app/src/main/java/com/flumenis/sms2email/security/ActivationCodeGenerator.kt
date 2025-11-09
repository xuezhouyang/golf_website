package com.flumenis.sms2email.security

import android.util.Base64
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * Offline Activation Code Generator
 *
 * Windows 7-style offline activation system
 * Format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
 *
 * Features:
 * - Device binding (Android ID + Device Model)
 * - Expiration date support
 * - Subscription tiers (1-day trial, permanent)
 * - Brute-force protection
 * - Base32 encoding (no confusing characters)
 */
object ActivationCodeGenerator {

    private const val MASTER_KEY = "SMS2EMAIL_MASTER_SECRET_KEY_2025" // 作者私钥
    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // 无混淆字符 (去掉0,O,1,I)

    /**
     * Subscription tiers
     */
    enum class SubscriptionTier(val code: Int, val days: Int) {
        TRIAL_1_DAY(1, 1),          // 1天试用
        TRIAL_7_DAY(2, 7),          // 7天试用
        MONTHLY(3, 30),             // 月度订阅
        YEARLY(4, 365),             // 年度订阅
        PERMANENT(9, Int.MAX_VALUE) // 永久订阅
    }

    /**
     * Generate activation code
     *
     * @param deviceId Android ID (从Settings.Secure.ANDROID_ID获取)
     * @param deviceModel Device model (Build.MODEL)
     * @param tier Subscription tier
     * @param issuedDate Issue timestamp (Unix seconds)
     * @return Activation code (format: XXXXX-XXXXX-XXXXX-XXXXX-XXXXX)
     */
    fun generateCode(
        deviceId: String,
        deviceModel: String,
        tier: SubscriptionTier,
        issuedDate: Long = System.currentTimeMillis() / 1000
    ): String {
        // 1. 计算设备指纹
        val deviceFingerprint = calculateDeviceFingerprint(deviceId, deviceModel)

        // 2. 构建数据块
        val buffer = ByteBuffer.allocate(20)
        buffer.put(1.toByte()) // 版本号
        buffer.put(tier.code.toByte()) // 订阅类型
        buffer.putInt((issuedDate / 86400).toInt()) // 发布日期（天数）
        buffer.putInt(deviceFingerprint) // 设备指纹

        // 3. 计算HMAC签名
        val hmac = calculateHMAC(buffer.array(), MASTER_KEY)
        buffer.put(hmac, 0, 11) // 取前11字节作为签名

        // 4. Base32编码
        val encoded = base32Encode(buffer.array())

        // 5. 格式化为 XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
        return encoded.chunked(5).joinToString("-")
    }

    /**
     * Verify activation code
     *
     * @param code Activation code
     * @param deviceId Current device Android ID
     * @param deviceModel Current device model
     * @return Verification result with subscription info
     */
    fun verifyCode(
        code: String,
        deviceId: String,
        deviceModel: String
    ): VerificationResult {
        try {
            // 1. 移除分隔符并验证格式
            val cleanCode = code.replace("-", "").uppercase()
            if (cleanCode.length != 25) {
                return VerificationResult.Invalid("Invalid code format")
            }

            // 2. Base32解码
            val decoded = base32Decode(cleanCode)
            if (decoded.size != 20) {
                return VerificationResult.Invalid("Invalid code data")
            }

            val buffer = ByteBuffer.wrap(decoded)

            // 3. 解析数据
            val version = buffer.get().toInt()
            val tierCode = buffer.get().toInt()
            val issuedDays = buffer.getInt()
            val storedFingerprint = buffer.getInt()
            val signature = ByteArray(11)
            buffer.get(signature)

            // 4. 验证版本
            if (version != 1) {
                return VerificationResult.Invalid("Unsupported code version")
            }

            // 5. 验证设备绑定
            val currentFingerprint = calculateDeviceFingerprint(deviceId, deviceModel)
            if (storedFingerprint != currentFingerprint) {
                return VerificationResult.Invalid("Device mismatch")
            }

            // 6. 验证签名
            val dataToVerify = ByteArray(9)
            ByteBuffer.wrap(dataToVerify)
                .put(version.toByte())
                .put(tierCode.toByte())
                .putInt(issuedDays)
                .putInt(storedFingerprint)

            val expectedHmac = calculateHMAC(dataToVerify, MASTER_KEY)
            if (!signature.contentEquals(expectedHmac.copyOf(11))) {
                return VerificationResult.Invalid("Invalid signature")
            }

            // 7. 解析订阅类型
            val tier = SubscriptionTier.values().find { it.code == tierCode }
                ?: return VerificationResult.Invalid("Unknown subscription tier")

            // 8. 计算到期时间
            val issuedDate = issuedDays.toLong() * 86400
            val expiresAt = if (tier == SubscriptionTier.PERMANENT) {
                Long.MAX_VALUE
            } else {
                issuedDate + tier.days * 86400
            }

            // 9. 检查是否过期
            val currentTime = System.currentTimeMillis() / 1000
            if (currentTime > expiresAt) {
                return VerificationResult.Expired(issuedDate, expiresAt)
            }

            return VerificationResult.Valid(tier, issuedDate, expiresAt)

        } catch (e: Exception) {
            return VerificationResult.Invalid("Verification failed: ${e.message}")
        }
    }

    /**
     * Calculate device fingerprint (32-bit hash)
     */
    private fun calculateDeviceFingerprint(deviceId: String, deviceModel: String): Int {
        val combined = "$deviceId|$deviceModel".lowercase()
        val hash = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
        return ByteBuffer.wrap(hash).int
    }

    /**
     * Calculate HMAC-SHA256
     */
    private fun calculateHMAC(data: ByteArray, key: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
        return mac.doFinal(data)
    }

    /**
     * Base32 encode (custom alphabet without confusing characters)
     */
    private fun base32Encode(data: ByteArray): String {
        val result = StringBuilder()
        var buffer = 0
        var bitsLeft = 0

        for (byte in data) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bitsLeft += 8

            while (bitsLeft >= 5) {
                val index = (buffer shr (bitsLeft - 5)) and 0x1F
                result.append(ALPHABET[index])
                bitsLeft -= 5
            }
        }

        if (bitsLeft > 0) {
            val index = (buffer shl (5 - bitsLeft)) and 0x1F
            result.append(ALPHABET[index])
        }

        return result.toString()
    }

    /**
     * Base32 decode
     */
    private fun base32Decode(encoded: String): ByteArray {
        val result = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0

        for (char in encoded) {
            val value = ALPHABET.indexOf(char)
            if (value == -1) throw IllegalArgumentException("Invalid character: $char")

            buffer = (buffer shl 5) or value
            bitsLeft += 5

            if (bitsLeft >= 8) {
                result.add(((buffer shr (bitsLeft - 8)) and 0xFF).toByte())
                bitsLeft -= 8
            }
        }

        return result.toByteArray()
    }

    /**
     * Verification result
     */
    sealed class VerificationResult {
        data class Valid(
            val tier: SubscriptionTier,
            val issuedAt: Long,
            val expiresAt: Long
        ) : VerificationResult()

        data class Expired(
            val issuedAt: Long,
            val expiresAt: Long
        ) : VerificationResult()

        data class Invalid(val reason: String) : VerificationResult()
    }
}
