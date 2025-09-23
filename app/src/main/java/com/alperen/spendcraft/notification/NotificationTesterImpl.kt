package com.alperen.spendcraft.notification

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alperen.spendcraft.core.common.NotificationTester
import com.alperen.spendcraft.reminder.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTesterImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationTester {
    
    override fun testNotificationNow() {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

