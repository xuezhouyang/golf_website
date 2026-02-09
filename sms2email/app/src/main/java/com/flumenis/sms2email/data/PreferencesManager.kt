package com.flumenis.sms2email.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sms2email_preferences")

class PreferencesManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val SMTP_HOST = stringPreferencesKey("smtp_host")
        private val SMTP_PORT = intPreferencesKey("smtp_port")
        private val SMTP_USERNAME = stringPreferencesKey("smtp_username")
        private val SMTP_PASSWORD = stringPreferencesKey("smtp_password")
        private val SMTP_USE_TLS = booleanPreferencesKey("smtp_use_tls")
        private val SMTP_USE_SSL = booleanPreferencesKey("smtp_use_ssl")
        private val FROM_EMAIL = stringPreferencesKey("from_email")
        private val FROM_NAME = stringPreferencesKey("from_name")
        private val TO_EMAIL = stringPreferencesKey("to_email")
        private val SUBJECT_TEMPLATE = stringPreferencesKey("subject_template")
        private val BODY_TEMPLATE = stringPreferencesKey("body_template")
        private val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        private val FILTER_ENABLED = booleanPreferencesKey("filter_enabled")
        private val ALLOWED_SENDERS = stringPreferencesKey("allowed_senders")
        private val BLOCKED_SENDERS = stringPreferencesKey("blocked_senders")
        private val KEYWORD_FILTERS = stringPreferencesKey("keyword_filters")
    }

    val emailConfigFlow: Flow<EmailConfig> = context.dataStore.data.map { prefs ->
        EmailConfig(
            smtpHost = prefs[SMTP_HOST] ?: "",
            smtpPort = prefs[SMTP_PORT] ?: 587,
            smtpUsername = prefs[SMTP_USERNAME] ?: "",
            smtpPassword = prefs[SMTP_PASSWORD] ?: "",
            smtpUseTls = prefs[SMTP_USE_TLS] ?: true,
            smtpUseSsl = prefs[SMTP_USE_SSL] ?: false,
            fromEmail = prefs[FROM_EMAIL] ?: "",
            fromName = prefs[FROM_NAME] ?: "SMS2Email",
            toEmail = prefs[TO_EMAIL] ?: "",
            subjectTemplate = prefs[SUBJECT_TEMPLATE] ?: "SMS from {{sender}} ({{sim_slot}})",
            bodyTemplate = prefs[BODY_TEMPLATE] ?: """
                |From: {{sender}}
                |SIM Slot: {{sim_slot}}
                |Time: {{timestamp}}
                |
                |Message:
                |{{message}}
            """.trimMargin(),
            enabled = prefs[SERVICE_ENABLED] ?: false
        )
    }

    val appConfigFlow: Flow<AppConfig> = context.dataStore.data.map { prefs ->
        val emailConfig = EmailConfig(
            smtpHost = prefs[SMTP_HOST] ?: "",
            smtpPort = prefs[SMTP_PORT] ?: 587,
            smtpUsername = prefs[SMTP_USERNAME] ?: "",
            smtpPassword = prefs[SMTP_PASSWORD] ?: "",
            smtpUseTls = prefs[SMTP_USE_TLS] ?: true,
            smtpUseSsl = prefs[SMTP_USE_SSL] ?: false,
            fromEmail = prefs[FROM_EMAIL] ?: "",
            fromName = prefs[FROM_NAME] ?: "SMS2Email",
            toEmail = prefs[TO_EMAIL] ?: "",
            subjectTemplate = prefs[SUBJECT_TEMPLATE] ?: "SMS from {{sender}} ({{sim_slot}})",
            bodyTemplate = prefs[BODY_TEMPLATE] ?: """
                |From: {{sender}}
                |SIM Slot: {{sim_slot}}
                |Time: {{timestamp}}
                |
                |Message:
                |{{message}}
            """.trimMargin(),
            enabled = prefs[SERVICE_ENABLED] ?: false
        )

        AppConfig(
            emailConfig = emailConfig,
            filterEnabled = prefs[FILTER_ENABLED] ?: false,
            allowedSenders = parseStringList(prefs[ALLOWED_SENDERS] ?: ""),
            blockedSenders = parseStringList(prefs[BLOCKED_SENDERS] ?: ""),
            keywordFilters = parseStringList(prefs[KEYWORD_FILTERS] ?: "")
        )
    }

    suspend fun saveEmailConfig(config: EmailConfig) {
        context.dataStore.edit { prefs ->
            prefs[SMTP_HOST] = config.smtpHost
            prefs[SMTP_PORT] = config.smtpPort
            prefs[SMTP_USERNAME] = config.smtpUsername
            prefs[SMTP_PASSWORD] = config.smtpPassword
            prefs[SMTP_USE_TLS] = config.smtpUseTls
            prefs[SMTP_USE_SSL] = config.smtpUseSsl
            prefs[FROM_EMAIL] = config.fromEmail
            prefs[FROM_NAME] = config.fromName
            prefs[TO_EMAIL] = config.toEmail
            prefs[SUBJECT_TEMPLATE] = config.subjectTemplate
            prefs[BODY_TEMPLATE] = config.bodyTemplate
            prefs[SERVICE_ENABLED] = config.enabled
        }
    }

    suspend fun saveAppConfig(config: AppConfig) {
        context.dataStore.edit { prefs ->
            prefs[SMTP_HOST] = config.emailConfig.smtpHost
            prefs[SMTP_PORT] = config.emailConfig.smtpPort
            prefs[SMTP_USERNAME] = config.emailConfig.smtpUsername
            prefs[SMTP_PASSWORD] = config.emailConfig.smtpPassword
            prefs[SMTP_USE_TLS] = config.emailConfig.smtpUseTls
            prefs[SMTP_USE_SSL] = config.emailConfig.smtpUseSsl
            prefs[FROM_EMAIL] = config.emailConfig.fromEmail
            prefs[FROM_NAME] = config.emailConfig.fromName
            prefs[TO_EMAIL] = config.emailConfig.toEmail
            prefs[SUBJECT_TEMPLATE] = config.emailConfig.subjectTemplate
            prefs[BODY_TEMPLATE] = config.emailConfig.bodyTemplate
            prefs[SERVICE_ENABLED] = config.emailConfig.enabled
            prefs[FILTER_ENABLED] = config.filterEnabled
            prefs[ALLOWED_SENDERS] = serializeStringList(config.allowedSenders)
            prefs[BLOCKED_SENDERS] = serializeStringList(config.blockedSenders)
            prefs[KEYWORD_FILTERS] = serializeStringList(config.keywordFilters)
        }
    }

    private fun parseStringList(json: String): List<String> {
        return if (json.isBlank()) {
            emptyList()
        } else {
            try {
                gson.fromJson(json, Array<String>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun serializeStringList(list: List<String>): String {
        return gson.toJson(list)
    }
}
