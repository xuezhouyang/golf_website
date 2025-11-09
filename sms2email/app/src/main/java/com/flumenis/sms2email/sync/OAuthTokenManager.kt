package com.flumenis.sms2email.sync

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * OAuth Token Manager
 *
 * Securely stores and manages OAuth access tokens for cloud providers
 */
class OAuthTokenManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "oauth_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Save OAuth token for a provider
     */
    fun saveToken(provider: CloudProvider, accessToken: String, refreshToken: String? = null) {
        encryptedPrefs.edit()
            .putString("${provider.name}_access_token", accessToken)
            .apply {
                if (refreshToken != null) {
                    putString("${provider.name}_refresh_token", refreshToken)
                }
            }
            .putLong("${provider.name}_token_timestamp", System.currentTimeMillis())
            .apply()
    }

    /**
     * Get OAuth token for a provider
     */
    fun getToken(provider: CloudProvider): String? {
        return encryptedPrefs.getString("${provider.name}_access_token", null)
    }

    /**
     * Get refresh token for a provider
     */
    fun getRefreshToken(provider: CloudProvider): String? {
        return encryptedPrefs.getString("${provider.name}_refresh_token", null)
    }

    /**
     * Check if token exists for provider
     */
    fun hasToken(provider: CloudProvider): Boolean {
        return getToken(provider) != null
    }

    /**
     * Clear token for a provider
     */
    fun clearToken(provider: CloudProvider) {
        encryptedPrefs.edit()
            .remove("${provider.name}_access_token")
            .remove("${provider.name}_refresh_token")
            .remove("${provider.name}_token_timestamp")
            .apply()
    }

    /**
     * Clear all tokens
     */
    fun clearAllTokens() {
        encryptedPrefs.edit().clear().apply()
    }

    /**
     * Get token age in milliseconds
     */
    fun getTokenAge(provider: CloudProvider): Long {
        val timestamp = encryptedPrefs.getLong("${provider.name}_token_timestamp", 0)
        if (timestamp == 0L) return Long.MAX_VALUE
        return System.currentTimeMillis() - timestamp
    }

    /**
     * Check if token is expired (older than 1 hour)
     */
    fun isTokenExpired(provider: CloudProvider): Boolean {
        val age = getTokenAge(provider)
        return age > 3600000 // 1 hour in milliseconds
    }
}
