package com.flumenis.sms2email.util

import android.content.Context
import android.net.Uri
import com.flumenis.sms2email.data.AppConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.IOException

/**
 * Utility class for importing and exporting configuration
 */
class ConfigManager(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Export configuration to JSON string
     */
    fun exportToJson(config: AppConfig): String {
        return gson.toJson(config)
    }

    /**
     * Import configuration from JSON string
     */
    fun importFromJson(json: String): Result<AppConfig> {
        return try {
            val config = gson.fromJson(json, AppConfig::class.java)
            Result.success(config)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Export configuration to file URI
     */
    fun exportToFile(config: AppConfig, uri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val json = exportToJson(config)
                outputStream.write(json.toByteArray())
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    /**
     * Import configuration from file URI
     */
    fun importFromFile(uri: Uri): Result<AppConfig> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                importFromJson(json)
            } ?: Result.failure(IOException("Unable to open file"))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
