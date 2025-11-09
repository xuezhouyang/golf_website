package com.flumenis.sms2email.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flumenis.sms2email.data.AppConfig
import com.flumenis.sms2email.data.AppDatabase
import com.flumenis.sms2email.data.EmailConfig
import com.flumenis.sms2email.data.EmailLog
import com.flumenis.sms2email.data.PreferencesManager
import com.flumenis.sms2email.security.ActivationManager
import com.flumenis.sms2email.service.EmailService
import com.flumenis.sms2email.service.SmsMonitorService
import com.flumenis.sms2email.service.SmsForwardingService
import com.flumenis.sms2email.sync.CloudSyncManager
import com.flumenis.sms2email.sync.CloudProvider
import com.flumenis.sms2email.sync.OAuthTokenManager
import com.flumenis.sms2email.ui.theme.ThemeManager
import com.flumenis.sms2email.ui.theme.ThemeMode
import com.flumenis.sms2email.ui.theme.AccentColor
import com.flumenis.sms2email.util.ConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val emailService = EmailService(application)
    private val configManager = ConfigManager(application)
    private val activationManager = ActivationManager(application)
    private val cloudSyncManager = CloudSyncManager(application)
    private val themeManager = ThemeManager(application)
    private val smsForwardingService = SmsForwardingService(application)
    private val oauthTokenManager = OAuthTokenManager(application)
    private val database = AppDatabase.getDatabase(application)

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
        // Check activation status on initialization
        _isPremiumActive.value = activationManager.isActivated()
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

    // Activation Management
    fun activateWithCode(code: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Verifying activation code...")
            val result = activationManager.activate(code)

            when (result) {
                is ActivationManager.ActivationResult.Success -> {
                    _isPremiumActive.value = true
                    val tierName = result.tier.name
                    _uiState.value = UiState.Success("✅ Activated successfully! ($tierName)")
                }
                is ActivationManager.ActivationResult.InvalidCode -> {
                    _uiState.value = UiState.Error("❌ ${result.reason}\nAttempts remaining: ${result.attemptsRemaining}")
                }
                is ActivationManager.ActivationResult.LockedOut -> {
                    _uiState.value = UiState.Error("🔒 Account locked. Try again in ${result.remainingTime}")
                }
                is ActivationManager.ActivationResult.TooManyAttempts -> {
                    _uiState.value = UiState.Error("⏱️ Please wait ${result.waitSeconds} seconds before trying again")
                }
                is ActivationManager.ActivationResult.Expired -> {
                    _uiState.value = UiState.Error("⌛ Activation code has expired")
                }
            }
        }
    }

    fun getAttemptsRemaining(): Int {
        return activationManager.getAttemptsRemaining()
    }

    fun getActivatedCode(): String? {
        return activationManager.getActivatedCode()
    }

    fun isLockedOut(): Boolean {
        return activationManager.isLockedOut()
    }

    fun getRemainingLockoutTime(): String {
        return activationManager.getRemainingLockoutTime()
    }

    fun getLockoutEndTime(): Long {
        val lockoutUntil = activationManager.getRemainingLockoutTime()
        return System.currentTimeMillis() + parseLockoutTime(lockoutUntil)
    }

    private fun parseLockoutTime(timeStr: String): Long {
        // Parse "23h 45m" format
        val hours = Regex("(\\d+)h").find(timeStr)?.groupValues?.get(1)?.toLongOrNull() ?: 0
        val minutes = Regex("(\\d+)m").find(timeStr)?.groupValues?.get(1)?.toLongOrNull() ?: 0
        return (hours * 60 + minutes) * 60 * 1000
    }

    // Get device info for activation
    fun getDeviceInfo(): ActivationManager.DeviceInfo {
        return activationManager.getDeviceInfo()
    }

    // Get activation info
    fun getActivationInfo(): ActivationManager.ActivationInfo? {
        return activationManager.getActivationInfo()
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

            // Save token for future use
            oauthTokenManager.saveToken(provider, accessToken)

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

            // Save token for future use
            oauthTokenManager.saveToken(provider, accessToken)

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

    // OAuth Token Management
    fun hasCloudToken(provider: CloudProvider): Boolean {
        return oauthTokenManager.hasToken(provider)
    }

    fun getCloudToken(provider: CloudProvider): String? {
        return oauthTokenManager.getToken(provider)
    }

    fun isCloudTokenExpired(provider: CloudProvider): Boolean {
        return oauthTokenManager.isTokenExpired(provider)
    }

    fun clearCloudToken(provider: CloudProvider) {
        oauthTokenManager.clearToken(provider)
    }

    // SMS Forwarding Management
    fun getAvailableSimSlots(): List<SmsForwardingService.SimSlotInfo> {
        return smsForwardingService.getAvailableSimSlots()
    }

    fun forwardSms(originalMessage: String, sender: String, targetNumber: String, simSlot: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Forwarding SMS...")
            val result = smsForwardingService.forwardSms(originalMessage, sender, targetNumber, simSlot)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success("SMS forwarded successfully") },
                onFailure = { UiState.Error(it.message ?: "SMS forwarding failed") }
            )
        }
    }

    // Email Log Management
    fun getLogsForDate(dateMillis: Long): Flow<List<EmailLog>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endTime = calendar.timeInMillis

        return database.emailLogDao().getLogsByDateRange(startTime, endTime)
    }

    fun getTodayLogs(): Flow<List<EmailLog>> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return database.emailLogDao().getTodayLogs(todayStart)
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading("Clearing logs...")
            try {
                database.emailLogDao().deleteAllLogs()
                _uiState.value = UiState.Success("All logs cleared")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to clear logs")
            }
        }
    }

    suspend fun insertEmailLog(log: EmailLog): Long {
        return database.emailLogDao().insertLog(log)
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
