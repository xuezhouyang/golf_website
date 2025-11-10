package com.flumenis.sms2email.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.flumenis.sms2email.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 全局崩溃日志处理器
 * 捕获未处理的异常并记录到文件
 */
class CrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private val logDir: File by lazy {
        File(context.getExternalFilesDir(null), "crash_logs").apply {
            if (!exists()) mkdirs()
        }
    }

    companion object {
        private const val TAG = "CrashHandler"
        private const val MAX_LOG_FILES = 10 // 最多保留10个崩溃日志文件

        @Volatile
        private var instance: CrashHandler? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = CrashHandler(context.applicationContext)
                        Thread.setDefaultUncaughtExceptionHandler(instance)
                    }
                }
            }
        }

        fun getInstance(): CrashHandler? = instance
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // 记录崩溃日志
            saveCrashLog(thread, throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        } finally {
            // 调用系统默认处理器
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashLog(thread: Thread, throwable: Throwable) {
        val timestamp = dateFormat.format(Date())
        val logFile = File(logDir, "crash_$timestamp.log")

        logFile.bufferedWriter().use { writer ->
            writer.write("═══════════════════════════════════════════════════════════\n")
            writer.write("PostaFide 崩溃日志\n")
            writer.write("═══════════════════════════════════════════════════════════\n\n")

            // 基本信息
            writer.write("时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
            writer.write("版本: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n")
            writer.write("构建类型: ${if (BuildConfig.DEBUG) "Debug" else "Release"}\n\n")

            // 设备信息
            writer.write("设备信息:\n")
            writer.write("  制造商: ${Build.MANUFACTURER}\n")
            writer.write("  型号: ${Build.MODEL}\n")
            writer.write("  Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            writer.write("  CPU ABI: ${Build.SUPPORTED_ABIS.joinToString(", ")}\n\n")

            // 线程信息
            writer.write("崩溃线程: ${thread.name} (ID: ${thread.id})\n\n")

            // 异常信息
            writer.write("异常类型: ${throwable.javaClass.name}\n")
            writer.write("异常消息: ${throwable.message ?: "无"}\n\n")

            // 堆栈跟踪
            writer.write("堆栈跟踪:\n")
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            throwable.printStackTrace(printWriter)
            writer.write(stringWriter.toString())

            // 如果有 cause，也记录
            var cause = throwable.cause
            while (cause != null) {
                writer.write("\n───────────────────────────────────────────────────────────\n")
                writer.write("由以下异常引起:\n")
                writer.write("异常类型: ${cause.javaClass.name}\n")
                writer.write("异常消息: ${cause.message ?: "无"}\n\n")

                val causeWriter = StringWriter()
                val causePrintWriter = PrintWriter(causeWriter)
                cause.printStackTrace(causePrintWriter)
                writer.write(causeWriter.toString())

                cause = cause.cause
            }

            writer.write("\n═══════════════════════════════════════════════════════════\n")
        }

        Log.e(TAG, "崩溃日志已保存: ${logFile.absolutePath}")

        // 清理旧日志
        cleanOldLogs()
    }

    /**
     * 清理旧的崩溃日志，只保留最近的 MAX_LOG_FILES 个
     */
    private fun cleanOldLogs() {
        try {
            val logFiles = logDir.listFiles { file ->
                file.isFile && file.name.startsWith("crash_") && file.name.endsWith(".log")
            } ?: return

            if (logFiles.size > MAX_LOG_FILES) {
                logFiles.sortedBy { it.lastModified() }
                    .take(logFiles.size - MAX_LOG_FILES)
                    .forEach { it.delete() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clean old logs", e)
        }
    }

    /**
     * 获取所有崩溃日志文件
     */
    fun getCrashLogs(): List<File> {
        return try {
            logDir.listFiles { file ->
                file.isFile && file.name.startsWith("crash_") && file.name.endsWith(".log")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get crash logs", e)
            emptyList()
        }
    }

    /**
     * 获取最新的崩溃日志
     */
    fun getLatestCrashLog(): File? {
        return getCrashLogs().firstOrNull()
    }

    /**
     * 删除所有崩溃日志
     */
    fun clearAllLogs() {
        try {
            logDir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "All crash logs cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear logs", e)
        }
    }
}
