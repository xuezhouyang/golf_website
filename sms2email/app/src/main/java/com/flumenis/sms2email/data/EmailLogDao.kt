package com.flumenis.sms2email.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Email Logs
 */
@Dao
interface EmailLogDao {

    /**
     * Insert a new log entry
     */
    @Insert
    suspend fun insertLog(log: EmailLog): Long

    /**
     * Get all logs ordered by timestamp descending
     */
    @Query("SELECT * FROM email_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<EmailLog>>

    /**
     * Get logs for a specific date range
     * @param startTime Start of the day (Unix timestamp)
     * @param endTime End of the day (Unix timestamp)
     */
    @Query("SELECT * FROM email_logs WHERE timestamp >= :startTime AND timestamp < :endTime ORDER BY timestamp DESC")
    fun getLogsByDateRange(startTime: Long, endTime: Long): Flow<List<EmailLog>>

    /**
     * Get logs for today
     */
    @Query("SELECT * FROM email_logs WHERE timestamp >= :todayStart ORDER BY timestamp DESC")
    fun getTodayLogs(todayStart: Long): Flow<List<EmailLog>>

    /**
     * Get success count for today
     */
    @Query("SELECT COUNT(*) FROM email_logs WHERE timestamp >= :todayStart AND success = 1")
    suspend fun getTodaySuccessCount(todayStart: Long): Int

    /**
     * Get failure count for today
     */
    @Query("SELECT COUNT(*) FROM email_logs WHERE timestamp >= :todayStart AND success = 0")
    suspend fun getTodayFailureCount(todayStart: Long): Int

    /**
     * Delete all logs
     */
    @Query("DELETE FROM email_logs")
    suspend fun deleteAllLogs()

    /**
     * Delete logs older than specified timestamp
     * Used for 7-day retention policy
     */
    @Query("DELETE FROM email_logs WHERE timestamp < :timestamp")
    suspend fun deleteLogsOlderThan(timestamp: Long): Int

    /**
     * Get total log count
     */
    @Query("SELECT COUNT(*) FROM email_logs")
    suspend fun getTotalLogCount(): Int
}
