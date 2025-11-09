package com.flumenis.sms2email.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flumenis.sms2email.data.AppConfig
import com.flumenis.sms2email.data.EmailConfig
import com.flumenis.sms2email.data.PreferencesManager
import com.flumenis.sms2email.security.InviteCodeManager
import com.flumenis.sms2email.service.EmailService
import com.flumenis.sms2email.service.SmsMonitorService
import com.flumenis.sms2email.sync.CloudSyncManager
import com.flumenis.sms2email.sync.CloudProvider
import com.flumenis.sms2email.ui.theme.ThemeManager
import com.flumenis.sms2email.ui.theme.ThemeMode
import com.flumenis.sms2email.ui.theme.AccentColor
import com.flumenis.sms2email.util.ConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val emailService = EmailService(application)
    private val configManager = ConfigManager(application)
    private val inviteCodeManager = InviteCodeManager(application)
    private val cloudSyncManager = CloudSyncManager(application)
    private val themeManager = ThemeManager(application)

    val emailConfig = preferencesManager.emailConfigFlow
    val appConfig = preferencesManager.appConfigFlow

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Premium status
    private val _isPremiumActive = MutableStateFlow(false)
    val isPremiumActive: StateFlow<Boolean> = _isPremiumActive.asStateFlow()

    // Theme flows
    val themeModeFlow = themeManager.themeModeFlow
    val accentColorFlow = themeManager.accentColorFlow
    val useDynamicColorFlow = themeManager.useDynamicColorFlow

    init {
        // Check premium status on initialization
        _isPremiumActive.value = inviteCodeManager.isActivated()
    }

    fun saveEmailConfig(config: EmailConfig) {
        viewModelScope.launch {
            preferencesManager.saveEmailConfig(config)

            // Start or stop service based on enabled status
            if (config.enabled) {
                startMonitoringService()
            } else {
                stopMonitoringService()
            }
        }
    }

    fun saveAppConfig(config: AppConfig) {
        viewModelScope.launch {
            preferencesManager.saveAppConfig(config)
        }
    }

    fun testConnection(config: EmailConfig) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Testing connection...")
            val result = emailService.testConnection(config)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Connection failed") }
            )
        }
    }

    fun exportConfig(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Exporting configuration...")
            val config = appConfig.first()
            val result = configManager.exportToFile(config, uri)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success("Configuration exported successfully") },
                onFailure = { UiState.Error(it.message ?: "Export failed") }
            )
        }
    }

    fun importConfig(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Importing configuration...")
            val result = configManager.importFromFile(uri)
            _uiState.value = result.fold(
                onSuccess = { config ->
                    saveAppConfig(config)
                    UiState.Success("Configuration imported successfully")
                },
                onFailure = { UiState.Error(it.message ?: "Import failed") }
            )
        }
    }

    fun clearUiState() {
        _uiState.value = UiState.Idle
    }

    // Premium Management
    fun verifyInviteCode(code: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Verifying invite code...")
            val result = inviteCodeManager.verifyInviteCode(code)
            result.fold(
                onSuccess = {
                    _isPremiumActive.value = true
                    _uiState.value = UiState.Success("Premium activated successfully!")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Invalid invite code")
                }
            )
        }
    }

    fun getAttemptsRemaining(): Int {
        return inviteCodeManager.getAttemptsRemaining()
    }

    fun getActivatedCode(): String? {
        return inviteCodeManager.getActivatedCode()
    }

    // Theme Management
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }

    fun setAccentColor(color: AccentColor) {
        viewModelScope.launch {
            themeManager.setAccentColor(color)
        }
    }

    fun setUseDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themeManager.setUseDynamicColor(enabled)
        }
    }

    // Cloud Sync Management
    fun backupToCloud(provider: CloudProvider, accessToken: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Backing up to cloud...")
            val config = appConfig.first()
            val result = cloudSyncManager.syncToCloud(config, provider, accessToken)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success("Backup successful") },
                onFailure = { UiState.Error(it.message ?: "Backup failed") }
            )
        }
    }

    fun restoreFromCloud(provider: CloudProvider, accessToken: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Restoring from cloud...")
            val result = cloudSyncManager.restoreFromCloud(provider, accessToken)
            _uiState.value = result.fold(
                onSuccess = { config ->
                    saveAppConfig(config)
                    UiState.Success("Restore successful")
                },
                onFailure = { UiState.Error(it.message ?: "Restore failed") }
            )
        }
    }

    private fun startMonitoringService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, SmsMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun stopMonitoringService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, SmsMonitorService::class.java)
        context.stopService(serviceIntent)
    }
}

sealed class UiState {
    object Idle : UiState()
    data class Loading(val message: String) : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
