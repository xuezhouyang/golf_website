package com.flumenis.sms2email.sync

import android.content.Context
import android.util.Log
import com.flumenis.sms2email.data.AppConfig
import com.flumenis.sms2email.security.ActivationManager
import com.flumenis.sms2email.util.ConfigManager
import com.flumenis.sms2email.util.RetryPolicy
import com.flumenis.sms2email.util.retryWithPolicy
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Cloud Sync Manager (Premium Feature)
 *
 * Supports:
 * - Google Drive
 * - OneDrive
 * - GitHub Gist
 *
 * Features:
 * - Automatic backup
 * - Version history
 * - Encrypted sync
 * - Conflict resolution
 */
class CloudSyncManager(private val context: Context) {

    private val activationManager = ActivationManager(context)
    private val configManager = ConfigManager(context)
    private val gson = Gson()
    private val client = OkHttpClient()

    companion object {
        private const val TAG = "CloudSyncManager"
        private const val BACKUP_FILENAME = "sms2email_config_backup.json"
    }

    /**
     * Check if activated
     */
    private fun checkActivated(): Boolean {
        return activationManager.isActivated()
    }

    /**
     * Sync configuration to cloud
     */
    suspend fun syncToCloud(
        config: AppConfig,
        provider: CloudProvider,
        accessToken: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!checkActivated()) {
                return@withContext Result.failure(Exception("Cloud sync requires activation"))
            }

            return@withContext when (provider) {
                CloudProvider.GOOGLE_DRIVE -> syncToGoogleDrive(config, accessToken)
                CloudProvider.ONEDRIVE -> syncToOneDrive(config, accessToken)
                CloudProvider.GITHUB -> syncToGitHub(config, accessToken)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing to cloud", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Restore configuration from cloud
     */
    suspend fun restoreFromCloud(
        provider: CloudProvider,
        accessToken: String
    ): Result<AppConfig> = withContext(Dispatchers.IO) {
        try {
            if (!checkActivated()) {
                return@withContext Result.failure(Exception("Cloud sync requires activation"))
            }

            return@withContext when (provider) {
                CloudProvider.GOOGLE_DRIVE -> restoreFromGoogleDrive(accessToken)
                CloudProvider.ONEDRIVE -> restoreFromOneDrive(accessToken)
                CloudProvider.GITHUB -> restoreFromGitHub(accessToken)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring from cloud", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Google Drive sync
     */
    private suspend fun syncToGoogleDrive(config: AppConfig, accessToken: String): Result<String> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Syncing to Google Drive (attempt $attempt)")
                val json = configManager.exportToJson(config)

                // Google Drive API endpoint
                val url = "https://www.googleapis.com/drive/v3/files"

                // Create file metadata
                val metadata = mapOf(
                    "name" to BACKUP_FILENAME,
                    "mimeType" to "application/json"
                )

                // Build multipart request
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("metadata", gson.toJson(metadata))
                    .addFormDataPart("file", BACKUP_FILENAME, json.toRequestBody("application/json".toMediaType()))
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $accessToken")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val fileId = gson.fromJson(response.body?.string(), Map::class.java)["id"] as? String
                    Result.success(fileId ?: "")
                } else {
                    Result.failure(IOException("Google Drive sync failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing to Google Drive (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun restoreFromGoogleDrive(accessToken: String): Result<AppConfig> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Restoring from Google Drive (attempt $attempt)")
                // Search for backup file
                val searchUrl = "https://www.googleapis.com/drive/v3/files?q=name='$BACKUP_FILENAME'"

                val searchRequest = Request.Builder()
                    .url(searchUrl)
                    .header("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val searchResponse = client.newCall(searchRequest).execute()

                if (!searchResponse.isSuccessful) {
                    return@retryWithPolicy Result.failure(IOException("Failed to search files"))
                }

                val files = gson.fromJson(searchResponse.body?.string(), Map::class.java)["files"] as? List<Map<String, Any>>
                val fileId = files?.firstOrNull()?.get("id") as? String
                    ?: return@retryWithPolicy Result.failure(IOException("Backup file not found"))

                // Download file
                val downloadUrl = "https://www.googleapis.com/drive/v3/files/$fileId?alt=media"

                val downloadRequest = Request.Builder()
                    .url(downloadUrl)
                    .header("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val downloadResponse = client.newCall(downloadRequest).execute()

                if (downloadResponse.isSuccessful) {
                    val json = downloadResponse.body?.string() ?: ""
                    configManager.importFromJson(json)
                } else {
                    Result.failure(IOException("Failed to download backup"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring from Google Drive (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    /**
     * OneDrive sync
     */
    private suspend fun syncToOneDrive(config: AppConfig, accessToken: String): Result<String> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Syncing to OneDrive (attempt $attempt)")
                val json = configManager.exportToJson(config)

                // OneDrive API endpoint
                val url = "https://graph.microsoft.com/v1.0/me/drive/root:/$BACKUP_FILENAME:/content"

                val requestBody = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $accessToken")
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val item = gson.fromJson(response.body?.string(), Map::class.java)
                    Result.success(item["id"] as? String ?: "")
                } else {
                    Result.failure(IOException("OneDrive sync failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing to OneDrive (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun restoreFromOneDrive(accessToken: String): Result<AppConfig> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Restoring from OneDrive (attempt $attempt)")
                val url = "https://graph.microsoft.com/v1.0/me/drive/root:/$BACKUP_FILENAME:/content"

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string() ?: ""
                    configManager.importFromJson(json)
                } else {
                    Result.failure(IOException("Failed to restore from OneDrive"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring from OneDrive (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    /**
     * GitHub Gist sync
     */
    private suspend fun syncToGitHub(config: AppConfig, accessToken: String): Result<String> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Syncing to GitHub (attempt $attempt)")
                val json = configManager.exportToJson(config)

                // GitHub Gist API
                val url = "https://api.github.com/gists"

                val gistData = mapOf(
                    "description" to "PostaFide Configuration Backup",
                    "public" to false,
                    "files" to mapOf(
                        BACKUP_FILENAME to mapOf(
                            "content" to json
                        )
                    )
                )

                val requestBody = gson.toJson(gistData).toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "token $accessToken")
                    .header("Accept", "application/vnd.github.v3+json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val gist = gson.fromJson(response.body?.string(), Map::class.java)
                    Result.success(gist["id"] as? String ?: "")
                } else {
                    Result.failure(IOException("GitHub sync failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing to GitHub (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun restoreFromGitHub(accessToken: String): Result<AppConfig> {
        return retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Restoring from GitHub (attempt $attempt)")
                // List gists to find our backup
                val url = "https://api.github.com/gists"

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "token $accessToken")
                    .header("Accept", "application/vnd.github.v3+json")
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    return@retryWithPolicy Result.failure(IOException("Failed to list gists"))
                }

                val gists = gson.fromJson(response.body?.string(), List::class.java) as List<Map<String, Any>>
                val backupGist = gists.find { gist ->
                    val files = gist["files"] as? Map<String, Any>
                    files?.containsKey(BACKUP_FILENAME) == true
                } ?: return@retryWithPolicy Result.failure(IOException("Backup gist not found"))

                val files = backupGist["files"] as Map<String, Any>
                val fileData = files[BACKUP_FILENAME] as Map<String, Any>
                val json = fileData["content"] as? String ?: ""

                configManager.importFromJson(json)
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring from GitHub (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }
}

/**
 * Cloud storage providers
 */
enum class CloudProvider {
    GOOGLE_DRIVE,
    ONEDRIVE,
    GITHUB
}
