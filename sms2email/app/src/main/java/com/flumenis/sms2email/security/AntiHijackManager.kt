package com.flumenis.sms2email.security

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference

/**
 * Anti-Hijack Manager for PostaFide
 * Prevents activity hijacking and task manipulation
 */
class AntiHijackManager(context: Context) {

    private val appContext = context.applicationContext
    private val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val handler = Handler(Looper.getMainLooper())
    private var currentActivity: WeakReference<Activity>? = null
    private var isMonitoring = false

    private val monitorRunnable = object : Runnable {
        override fun run() {
            if (isMonitoring) {
                checkTopActivity()
                handler.postDelayed(this, CHECK_INTERVAL_MS)
            }
        }
    }

    /**
     * Register an activity for monitoring
     */
    fun registerActivity(activity: Activity) {
        currentActivity = WeakReference(activity)
    }

    /**
     * Unregister activity
     */
    fun unregisterActivity() {
        currentActivity = null
    }

    /**
     * Start monitoring for hijacking
     */
    fun startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true
            handler.post(monitorRunnable)
        }
    }

    /**
     * Stop monitoring
     */
    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(monitorRunnable)
    }

    /**
     * Check if current top activity is our app
     */
    private fun checkTopActivity() {
        val activity = currentActivity?.get() ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ requires different approach
            // We check if our activity is resumed
            if (!activity.isFinishing && !activity.isDestroyed) {
                // Activity is still valid
                return
            }
        } else {
            // For older Android versions
            try {
                val tasks = activityManager.getRunningTasks(1)
                if (tasks.isNotEmpty()) {
                    val topActivity = tasks[0].topActivity
                    if (topActivity?.packageName != appContext.packageName) {
                        onHijackDetected(activity)
                    }
                }
            } catch (e: Exception) {
                // Permission denied or API restrictions
            }
        }
    }

    /**
     * Called when hijacking is detected
     */
    private fun onHijackDetected(activity: Activity) {
        // Finish activity and clear task
        activity.finishAndRemoveTask()

        // Optional: Show security alert
        // You can emit an event here to show a dialog
    }

    /**
     * Verify intent source
     * Call this in Activity.onCreate to verify intent is from trusted source
     */
    fun verifyIntentSource(activity: Activity): Boolean {
        val intent = activity.intent ?: return true

        // Check if intent has our signature
        val referrer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            activity.referrer
        } else {
            null
        }

        // If launched from external source, verify
        if (referrer != null && referrer.host != appContext.packageName) {
            // External launch detected
            // You can add additional verification here
            return false
        }

        return true
    }

    /**
     * Prevent screen overlay attacks
     */
    fun checkScreenOverlay(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use obscured touch detector
            val window = activity.window
            window.decorView.filterTouchesWhenObscured = true
        }
        return true
    }

    companion object {
        private const val CHECK_INTERVAL_MS = 1000L // Check every second
    }
}
