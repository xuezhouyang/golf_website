package com.flumenis.sms2email

import android.app.Application
import android.util.Log
import com.flumenis.sms2email.BuildConfig
import com.flumenis.sms2email.security.SecurityManager
import com.flumenis.sms2email.service.KeepAliveManager
import com.flumenis.sms2email.util.CrashHandler

/**
 * Application class for PostaFide
 * Flumenis LLC, Delaware
 */
class SMS2EmailApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化崩溃日志处理器（必须最先执行）
        CrashHandler.init(this)

        // Perform security check
        performSecurityCheck()

        // Initialize keep-alive
        initializeKeepAlive()
    }

    private fun performSecurityCheck() {
        val securityManager = SecurityManager(this)
        val report = securityManager.performSecurityCheck()

        // Log security status (only in debug builds)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Security Check: ${report.getSummary()}")
        }

        // We don't block the app even if security issues are found
        // This ensures the app doesn't become a tool for privilege escalation
        // but allows users with root/xposed to use it
    }

    private fun initializeKeepAlive() {
        try {
            val keepAliveManager = KeepAliveManager(this)
            keepAliveManager.setupKeepAlive()
        } catch (e: Exception) {
            // Silently fail - keep-alive is best effort
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to initialize keep-alive", e)
            }
        }
    }

    companion object {
        private const val TAG = "PostaFideApp"

        lateinit var instance: SMS2EmailApplication
            private set
    }
}
