package com.alperen.spendcraft.reminder

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.*

object ReminderScheduler {
    
    private const val REMINDER_WORK_NAME = "daily_reminder"
    
    fun scheduleDaily(context: Context) {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            // TEST: 30 saniye sonra tetikle (normal: 21:00)
            timeInMillis = currentTime.timeInMillis + 30_000 // 30 saniye sonra
            
            /* Normal production code:
            set(Calendar.HOUR_OF_DAY, 21) // 21:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If 21:00 has passed today, schedule for tomorrow
            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            */
        }
        
        val delayInMillis = targetTime.timeInMillis - currentTime.timeInMillis
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()
        
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }
    
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME)
    }
}
