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
import com.flumenis.sms2email.service.EmailService
import com.flumenis.sms2email.service.SmsMonitorService
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

    val emailConfig = preferencesManager.emailConfigFlow
    val appConfig = preferencesManager.appConfigFlow

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
