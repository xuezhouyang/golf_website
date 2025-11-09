package com.flumenis.sms2email.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Keep Alive Manager for PostaFide
 * Ensures service stays running using multiple strategies
 */
class KeepAliveManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Setup comprehensive keep-alive mechanisms
     */
    fun setupKeepAlive() {
        // 1. WorkManager periodic check
        setupWorkManager()

        // 2. AlarmManager backup
        setupAlarmManager()

        // 3. System broadcast listeners
        setupBroadcastReceivers()
    }

    /**
     * Setup WorkManager for periodic service check
     */
    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val keepAliveWork = PeriodicWorkRequestBuilder<KeepAliveWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
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
    }

    /**
     * Setup AlarmManager as backup mechanism
     */
    private fun setupAlarmManager() {
        val intent = Intent(context, KeepAliveReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set repeating alarm
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + ALARM_INTERVAL_MS,
            ALARM_INTERVAL_MS,
            pendingIntent
        )
    }

    /**
     * Setup broadcast receivers for system events
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
    }

    /**
     * Ensure service is running
     */
    fun ensureServiceRunning() {
        val serviceIntent = Intent(context, SmsMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    /**
     * System event receiver
     */
    private val systemEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON,
                Intent.ACTION_USER_PRESENT,
                Intent.ACTION_BOOT_COMPLETED -> {
                    ensureServiceRunning()
                }
            }
        }
    }

    companion object {
        private const val WORK_NAME = "postafide_keep_alive"
        private const val ALARM_REQUEST_CODE = 1001
        private const val ALARM_INTERVAL_MS = 10 * 60 * 1000L // 10 minutes
    }
}

/**
 * Keep Alive Worker
 */
class KeepAliveWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        // Check if service is running and restart if needed
        val keepAliveManager = KeepAliveManager(applicationContext)
        keepAliveManager.ensureServiceRunning()

        return Result.success()
    }
}

/**
 * Keep Alive Broadcast Receiver
 */
class KeepAliveReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val keepAliveManager = KeepAliveManager(context)
        keepAliveManager.ensureServiceRunning()
    }
}
