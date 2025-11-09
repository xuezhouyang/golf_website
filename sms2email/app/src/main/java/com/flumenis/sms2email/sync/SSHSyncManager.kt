package com.flumenis.sms2email.sync

import android.content.Context
import android.util.Log
import com.flumenis.sms2email.data.AppConfig
import com.flumenis.sms2email.security.ActivationManager
import com.flumenis.sms2email.util.ConfigManager
import com.flumenis.sms2email.util.RetryPolicy
import com.flumenis.sms2email.util.retryWithPolicy
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * SSH/SFTP Sync Manager (Premium Feature)
 *
 * ⚠️ SECURITY WARNING ⚠️
 * SSH/SFTP connections transmit credentials over the network.
 * Only use on trusted networks and with strong passwords/keys.
 * NAS users should consider:
 * - Using SSH keys instead of passwords
 * - Enabling fail2ban or similar protection
 * - Using non-standard SSH ports
 * - Keeping SSH server software updated
 *
 * Features:
 * - SFTP file upload/download
 * - Password or SSH key authentication
 * - Configurable port and path
 * - Retry mechanism with exponential backoff
 * - Connection timeout protection
 */
class SSHSyncManager(private val context: Context) {

    private val activationManager = ActivationManager(context)
    private val configManager = ConfigManager(context)

    companion object {
        private const val TAG = "SSHSyncManager"
        private const val BACKUP_FILENAME = "sms2email_config_backup.json"
        private const val DEFAULT_SSH_PORT = 22
        private const val CONNECTION_TIMEOUT = 30000 // 30 seconds
        private const val SFTP_TIMEOUT = 60000 // 60 seconds
    }

    /**
     * SSH connection configuration
     */
    data class SSHConfig(
        val host: String,
        val port: Int = DEFAULT_SSH_PORT,
        val username: String,
        val password: String? = null,
        val privateKey: String? = null,
        val remotePath: String = "/tmp",
        val strictHostKeyChecking: Boolean = false
    )

    /**
     * Check if activated
     */
    private fun checkActivated(): Boolean {
        return activationManager.isActivated()
    }

    /**
     * Test SSH/SFTP connection
     */
    suspend fun testConnection(config: SSHConfig): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!checkActivated()) {
                return@withContext Result.failure(Exception("SSH/SFTP sync requires activation"))
            }

            val jsch = JSch()
            var session: Session? = null

            try {
                // Add private key if provided
                if (config.privateKey != null) {
                    jsch.addIdentity("key", config.privateKey.toByteArray(), null, null)
                }

                // Create session
                session = jsch.getSession(config.username, config.host, config.port)

                // Set password if provided
                if (config.password != null) {
                    session.setPassword(config.password)
                }

                // Configure session
                val properties = java.util.Properties()
                if (!config.strictHostKeyChecking) {
                    properties["StrictHostKeyChecking"] = "no"
                }
                properties["PreferredAuthentications"] = if (config.privateKey != null) {
                    "publickey"
                } else {
                    "password"
                }
                session.setConfig(properties)
                session.timeout = CONNECTION_TIMEOUT

                // Connect
                session.connect()

                // Open SFTP channel
                val channel = session.openChannel("sftp") as ChannelSftp
                channel.connect(SFTP_TIMEOUT)

                // Try to access remote path
                try {
                    channel.cd(config.remotePath)
                } catch (e: Exception) {
                    channel.disconnect()
                    return@withContext Result.failure(
                        Exception("Remote path '${config.remotePath}' not accessible: ${e.message}")
                    )
                }

                channel.disconnect()

                Result.success("✓ SSH/SFTP connection successful\n✓ Remote path accessible")
            } finally {
                session?.disconnect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "SSH connection test failed", e)
            Result.failure(e)
        }
    }

    /**
     * Upload configuration to SSH/SFTP server
     */
    suspend fun uploadConfig(
        appConfig: AppConfig,
        sshConfig: SSHConfig
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Uploading to SSH/SFTP (attempt $attempt)")

                if (!checkActivated()) {
                    return@retryWithPolicy Result.failure(Exception("SSH/SFTP sync requires activation"))
                }

                // Export config to JSON
                val json = configManager.exportToJson(appConfig)

                // Connect to SSH server
                val jsch = JSch()
                var session: Session? = null

                try {
                    // Add private key if provided
                    if (sshConfig.privateKey != null) {
                        jsch.addIdentity("key", sshConfig.privateKey.toByteArray(), null, null)
                    }

                    // Create session
                    session = jsch.getSession(sshConfig.username, sshConfig.host, sshConfig.port)

                    // Set password if provided
                    if (sshConfig.password != null) {
                        session.setPassword(sshConfig.password)
                    }

                    // Configure session
                    val properties = java.util.Properties()
                    if (!sshConfig.strictHostKeyChecking) {
                        properties["StrictHostKeyChecking"] = "no"
                    }
                    properties["PreferredAuthentications"] = if (sshConfig.privateKey != null) {
                        "publickey"
                    } else {
                        "password"
                    }
                    session.setConfig(properties)
                    session.timeout = CONNECTION_TIMEOUT

                    // Connect
                    session.connect()

                    // Open SFTP channel
                    val channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect(SFTP_TIMEOUT)

                    // Change to remote directory
                    channel.cd(sshConfig.remotePath)

                    // Upload file
                    val inputStream = ByteArrayInputStream(json.toByteArray())
                    channel.put(inputStream, BACKUP_FILENAME)

                    inputStream.close()
                    channel.disconnect()

                    val remotePath = "${sshConfig.remotePath}/$BACKUP_FILENAME"
                    Result.success(remotePath)
                } finally {
                    session?.disconnect()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading to SSH/SFTP (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Download configuration from SSH/SFTP server
     */
    suspend fun downloadConfig(
        sshConfig: SSHConfig
    ): Result<AppConfig> = withContext(Dispatchers.IO) {
        return@withContext retryWithPolicy(RetryPolicy.DEFAULT) { attempt ->
            try {
                Log.d(TAG, "Downloading from SSH/SFTP (attempt $attempt)")

                if (!checkActivated()) {
                    return@retryWithPolicy Result.failure(Exception("SSH/SFTP sync requires activation"))
                }

                // Connect to SSH server
                val jsch = JSch()
                var session: Session? = null

                try {
                    // Add private key if provided
                    if (sshConfig.privateKey != null) {
                        jsch.addIdentity("key", sshConfig.privateKey.toByteArray(), null, null)
                    }

                    // Create session
                    session = jsch.getSession(sshConfig.username, sshConfig.host, sshConfig.port)

                    // Set password if provided
                    if (sshConfig.password != null) {
                        session.setPassword(sshConfig.password)
                    }

                    // Configure session
                    val properties = java.util.Properties()
                    if (!sshConfig.strictHostKeyChecking) {
                        properties["StrictHostKeyChecking"] = "no"
                    }
                    properties["PreferredAuthentications"] = if (sshConfig.privateKey != null) {
                        "publickey"
                    } else {
                        "password"
                    }
                    session.setConfig(properties)
                    session.timeout = CONNECTION_TIMEOUT

                    // Connect
                    session.connect()

                    // Open SFTP channel
                    val channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect(SFTP_TIMEOUT)

                    // Change to remote directory
                    channel.cd(sshConfig.remotePath)

                    // Download file
                    val outputStream = ByteArrayOutputStream()
                    channel.get(BACKUP_FILENAME, outputStream)
                    val json = outputStream.toString("UTF-8")

                    outputStream.close()
                    channel.disconnect()

                    // Parse JSON
                    configManager.importFromJson(json)
                } finally {
                    session?.disconnect()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading from SSH/SFTP (attempt $attempt)", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Get security warnings for SSH/SFTP usage
     */
    fun getSecurityWarnings(): List<String> {
        return listOf(
            "⚠️ SSH/SFTP transmits credentials over the network",
            "⚠️ Only use on trusted networks (not public WiFi)",
            "⚠️ Use strong passwords or SSH keys",
            "⚠️ Consider using non-standard SSH ports",
            "⚠️ Enable fail2ban or similar protection",
            "⚠️ Keep your SSH server software updated",
            "⚠️ Review SSH server logs regularly",
            "⚠️ Use firewall rules to limit access"
        )
    }

    /**
     * Validate SSH configuration
     */
    fun validateConfig(config: SSHConfig): Result<Unit> {
        if (config.host.isBlank()) {
            return Result.failure(Exception("Host cannot be empty"))
        }

        if (config.port !in 1..65535) {
            return Result.failure(Exception("Invalid port number"))
        }

        if (config.username.isBlank()) {
            return Result.failure(Exception("Username cannot be empty"))
        }

        if (config.password == null && config.privateKey == null) {
            return Result.failure(Exception("Either password or private key must be provided"))
        }

        if (config.remotePath.isBlank()) {
            return Result.failure(Exception("Remote path cannot be empty"))
        }

        return Result.success(Unit)
    }
}
