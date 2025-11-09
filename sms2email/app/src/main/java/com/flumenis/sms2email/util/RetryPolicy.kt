package com.flumenis.sms2email.util

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

/**
 * Retry policy for network operations
 */
class RetryPolicy(
    private val maxAttempts: Int = 3,
    private val initialDelayMs: Long = 1000,
    private val maxDelayMs: Long = 10000,
    private val factor: Double = 2.0,
    private val jitter: Boolean = true
) {

    companion object {
        private const val TAG = "RetryPolicy"

        /**
         * Default retry policy for network operations
         */
        val DEFAULT = RetryPolicy(
            maxAttempts = 3,
            initialDelayMs = 1000,
            maxDelayMs = 10000,
            factor = 2.0
        )

        /**
         * Aggressive retry policy for critical operations
         */
        val AGGRESSIVE = RetryPolicy(
            maxAttempts = 5,
            initialDelayMs = 500,
            maxDelayMs = 5000,
            factor = 1.5
        )

        /**
         * Conservative retry policy for background operations
         */
        val CONSERVATIVE = RetryPolicy(
            maxAttempts = 2,
            initialDelayMs = 2000,
            maxDelayMs = 15000,
            factor = 3.0
        )
    }

    /**
     * Execute operation with retry logic
     */
    suspend fun <T> execute(
        operation: suspend (attempt: Int) -> Result<T>
    ): Result<T> {
        var lastException: Throwable? = null
        var currentDelay = initialDelayMs

        repeat(maxAttempts) { attempt ->
            try {
                val result = operation(attempt + 1)

                if (result.isSuccess) {
                    if (attempt > 0) {
                        Log.d(TAG, "Operation succeeded after ${attempt + 1} attempts")
                    }
                    return result
                }

                // If result is failure, extract exception
                result.exceptionOrNull()?.let { exception ->
                    lastException = exception
                    Log.w(TAG, "Attempt ${attempt + 1}/$maxAttempts failed: ${exception.message}")
                }

            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Attempt ${attempt + 1}/$maxAttempts failed with exception: ${e.message}", e)
            }

            // Don't delay after last attempt
            if (attempt < maxAttempts - 1) {
                val delayTime = calculateDelay(currentDelay, attempt)
                Log.d(TAG, "Retrying in ${delayTime}ms...")
                delay(delayTime)
                currentDelay = min((currentDelay * factor).toLong(), maxDelayMs)
            }
        }

        // All attempts failed
        Log.e(TAG, "All $maxAttempts attempts failed")
        return Result.failure(
            lastException ?: Exception("Operation failed after $maxAttempts attempts")
        )
    }

    /**
     * Calculate delay with exponential backoff and optional jitter
     */
    private fun calculateDelay(baseDelay: Long, attempt: Int): Long {
        val exponentialDelay = min(
            (baseDelay * factor.pow(attempt)).toLong(),
            maxDelayMs
        )

        return if (jitter) {
            // Add random jitter (0-25% of delay)
            val jitterAmount = (exponentialDelay * 0.25 * Math.random()).toLong()
            exponentialDelay + jitterAmount
        } else {
            exponentialDelay
        }
    }

    /**
     * Check if exception is retryable
     */
    fun isRetryable(exception: Throwable): Boolean {
        return when {
            // Network errors are retryable
            exception is java.net.UnknownHostException -> true
            exception is java.net.SocketTimeoutException -> true
            exception is java.net.ConnectException -> true
            exception is java.io.IOException -> true

            // HTTP 5xx errors are retryable
            exception.message?.contains("500") == true -> true
            exception.message?.contains("502") == true -> true
            exception.message?.contains("503") == true -> true
            exception.message?.contains("504") == true -> true

            // Rate limiting (429) is retryable with backoff
            exception.message?.contains("429") == true -> true

            // Authentication errors are NOT retryable
            exception.message?.contains("401") == true -> false
            exception.message?.contains("403") == true -> false

            // Client errors are NOT retryable
            exception.message?.contains("400") == true -> false
            exception.message?.contains("404") == true -> false

            else -> false
        }
    }
}

/**
 * Extension function for easy retry
 */
suspend fun <T> retryWithPolicy(
    policy: RetryPolicy = RetryPolicy.DEFAULT,
    operation: suspend (attempt: Int) -> Result<T>
): Result<T> {
    return policy.execute(operation)
}

/**
 * Extension function for simple retry (without Result wrapper)
 */
suspend fun <T> retry(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 1000,
    operation: suspend (attempt: Int) -> T
): T {
    var lastException: Throwable? = null
    var currentDelay = initialDelayMs

    repeat(maxAttempts) { attempt ->
        try {
            return operation(attempt + 1)
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxAttempts - 1) {
                delay(currentDelay)
                currentDelay *= 2 // Simple exponential backoff
            }
        }
    }

    throw lastException ?: Exception("Operation failed after $maxAttempts attempts")
}
