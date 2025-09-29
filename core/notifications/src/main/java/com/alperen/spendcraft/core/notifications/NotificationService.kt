package com.alperen.spendcraft.core.notifications

import android.content.Context
import androidx.work.*
// import com.alperen.spendcraft.data.repository.TransactionRepository
// import com.alperen.spendcraft.data.repository.BudgetRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    
    fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(calculateDelayUntilEvening(), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    private fun calculateDelayUntilEvening(): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 20) // 20:00
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        
        return calendar.timeInMillis - now
    }
    
    suspend fun checkBudgetAlerts() = withContext(Dispatchers.IO) {
        try {
            // Bütçe kontrolü - şimdilik boş implementasyon
            // Repository'ler inject edilmediği için bu fonksiyon şimdilik çalışmıyor
        } catch (e: Exception) {
            // Log error
        }
    }
    
    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun cancelAllScheduledNotifications() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Burada bildirim gönderme işlemi yapılacak
            // NotificationManager inject edilecek
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
