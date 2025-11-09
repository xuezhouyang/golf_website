package com.flumenis.sms2email

import android.app.Application

/**
 * Application class for SMS2Email
 * Flumenis LLC, Delaware
 */
class SMS2EmailApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SMS2EmailApplication
            private set
    }
}
