package com.flumenis.sms2email.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * PostaFide Room Database
 * 
 * Encrypted database for email send logs with 7-day retention policy
 * Uses SQLCipher for AES-256 encryption
 */
@Database(
    entities = [EmailLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun emailLogDao(): EmailLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get database instance (singleton) with encryption
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "postafide_database"
                )
                    .openHelperFactory(createEncryptedFactory(context))
                    .addCallback(DatabaseCallback())
                    .build()
                
                INSTANCE = instance
                instance
            }
        }

        /**
         * Create encrypted database factory using SQLCipher
         * Passphrase is derived from device-specific information
         */
        private fun createEncryptedFactory(context: Context): SupportSQLiteOpenHelper.Factory {
            // Generate device-specific passphrase
            val passphrase = generatePassphrase(context)
            return SupportFactory(passphrase)
        }

        /**
         * Generate a secure passphrase based on device information
         * This ensures the database can only be decrypted on this specific device
         */
        private fun generatePassphrase(context: Context): ByteArray {
            // Get Android ID (unique to device + app)
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "default_id"

            // Get device model and manufacturer
            val deviceInfo = "${Build.MANUFACTURER}-${Build.MODEL}-${Build.DEVICE}"

            // Get package name
            val packageName = context.packageName

            // Combine all information
            val combined = "$androidId-$deviceInfo-$packageName-postafide-encryption-key"

            // Hash using SHA-256 to create a 256-bit key
            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(combined.toByteArray(Charsets.UTF_8))
        }

        /**
         * Database callback for cleanup on open
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                
                // Clean up old logs (7-day retention) on database open
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
                        database.emailLogDao().deleteLogsOlderThan(sevenDaysAgo)
                    }
                }
            }
        }
    }
}
