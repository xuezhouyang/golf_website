package com.flumenis.sms2email.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.flumenis.sms2email.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receiver to restart monitoring service after device boot
 */
class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON") {
            return
        }

        Log.d(TAG, "Boot completed, checking service status")

        scope.launch {
            try {
                val preferencesManager = PreferencesManager(context)
                val config = preferencesManager.emailConfigFlow.first()

                if (config.enabled) {
                    startMonitoringService(context)
                    Log.d(TAG, "SMS monitoring service started after boot")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting service after boot", e)
            }
        }
    }

    private fun startMonitoringService(context: Context) {
        val serviceIntent = Intent(context, SmsMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
