package com.flumenis.sms2email.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Keep Alive Manager for PostaFide
 * Ensures service stays running using multiple strategies
 *
 * Note: Android 12+ restricts background foreground service starts.
 * We rely on WorkManager and system broadcasts which are allowed.
 */
class KeepAliveManager(private val context: Context) {

    companion object {
        private const val TAG = "KeepAliveManager"
        private const val WORK_NAME = "postafide_keep_alive"
    }

    /**
     * Setup comprehensive keep-alive mechanisms
     */
    fun setupKeepAlive() {
        // 1. WorkManager periodic check (Primary mechanism)
        setupWorkManager()

        // 2. System broadcast listeners (Allowed by Android)
        setupBroadcastReceivers()

        // Note: AlarmManager removed - not allowed to start foreground services
        // on Android 12+ (API 31+) when triggered from background
    }

    /**
     * Setup WorkManager for periodic service check
     * WorkManager is allowed to start foreground services
     */
    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val keepAliveWork = PeriodicWorkRequestBuilder<KeepAliveWorker>(
            15, TimeUnit.MINUTES,  // Repeat every 15 minutes
            5, TimeUnit.MINUTES    // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            keepAliveWork
        )

        Log.d(TAG, "WorkManager keep-alive scheduled")
    }

    /**
     * Setup broadcast receivers for system events
     * These broadcasts are allowed to start foreground services
     */
    private fun setupBroadcastReceivers() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }

        ContextCompat.registerReceiver(
            context,
            systemEventReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        Log.d(TAG, "System broadcast receivers registered")
    }

    /**
     * Ensure service is running
     *
     * IMPORTANT: On Android 12+ (API 31+), starting foreground services from
     * background is restricted. This method includes proper exception handling.
     */
    fun ensureServiceRunning() {
        try {
            val serviceIntent = Intent(context, SmsMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
                Log.d(TAG, "Foreground service start requested")
            } else {
                context.startService(serviceIntent)
                Log.d(TAG, "Service start requested")
            }
        } catch (e: IllegalStateException) {
            // Android 12+ (API 31+): ForegroundServiceStartNotAllowedException
            // This is expected when starting from background
            Log.w(TAG, "Cannot start foreground service from background: ${e.message}")
            // Service will be started next time app comes to foreground or
            // through WorkManager/system broadcasts
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception starting service: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception starting service", e)
        }
    }

    /**
     * System event receiver
     * These system broadcasts are allowed to start foreground services
     */
    private val systemEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON,
                Intent.ACTION_USER_PRESENT,
                Intent.ACTION_BOOT_COMPLETED -> {
                    Log.d(TAG, "System event received: ${intent.action}")
                    ensureServiceRunning()
                }
            }
        }
    }
}

/**
 * Keep Alive Worker
 * WorkManager is allowed to start foreground services on Android 12+
 */
class KeepAliveWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("KeepAliveWorker", "Worker executing")

        // Check if service is running and restart if needed
        val keepAliveManager = KeepAliveManager(applicationContext)
        keepAliveManager.ensureServiceRunning()

        return Result.success()
    }
}
