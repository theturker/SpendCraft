package com.alperen.spendcraft.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
// import com.alperen.spendcraft.MainActivity
// import com.alperen.spendcraft.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    // Sadece sistem bildirimleri gönderir
    
    companion object {
        const val CHANNEL_ID_BUDGET = "budget_alerts"
        const val CHANNEL_ID_REMINDERS = "spending_reminders"
        const val CHANNEL_ID_ACHIEVEMENTS = "achievements"
        const val CHANNEL_ID_GENERAL = "general"
        const val CHANNEL_ID_REPORTS = "reports"
        
        const val NOTIFICATION_ID_BUDGET = 1001
        const val NOTIFICATION_ID_REMINDER = 1002
        const val NOTIFICATION_ID_ACHIEVEMENT = 1003
        const val NOTIFICATION_ID_GENERAL = 1004
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Budget Alerts Channel
            val budgetChannel = NotificationChannel(
                CHANNEL_ID_BUDGET,
                "Bütçe Uyarıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Bütçe aşımı ve finansal uyarılar"
                enableVibration(true)
            }
            
            // Spending Reminders Channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Harcama Hatırlatmaları",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Günlük harcama kayıt hatırlatmaları"
                enableVibration(false)
            }
            
            // Achievements Channel
            val achievementChannel = NotificationChannel(
                CHANNEL_ID_ACHIEVEMENTS,
                "Başarımlar",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Başarım ve rozet bildirimleri"
                enableVibration(true)
            }
            
            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Genel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Genel uygulama bildirimleri"
                enableVibration(false)
            }
            
            val reportsChannel = NotificationChannel(
                CHANNEL_ID_REPORTS,
                "Raporlar",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Rapor oluşturma bildirimi"
                enableVibration(false)
            }
            notificationManager.createNotificationChannels(
                listOf(budgetChannel, reminderChannel, achievementChannel, generalChannel, reportsChannel)
            )
        }
    }

    fun showFileNotification(title: String, message: String, uri: android.net.Uri, mimeType: String = "application/pdf") {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1010,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(2001, notification)
    }
    
    fun showBudgetAlert(amount: String, budgetName: String) {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Bütçe Uyarısı")
            .setContentText("$budgetName bütçenizi $amount aştınız!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BUDGET, notification)
    }
    
    fun showBudgetWarning(percentage: Int, budgetName: String, spentAmount: String, limitAmount: String) {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Bütçe Uyarısı")
            .setContentText("$budgetName bütçenizin %$percentage'ini kullandınız! (₺$spentAmount / ₺$limitAmount)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BUDGET + 1, notification)
    }
    
    fun showSpendingReminder() {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle("📝 Günlük Hatırlatma")
            .setContentText("Bugünkü harcamalarınızı kaydetmeyi unutmayın!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }
    
    fun showAchievementNotification(achievementName: String, description: String) {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ACHIEVEMENTS)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentTitle("🏆 Yeni Başarım!")
            .setContentText("$achievementName: $description")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
    }
    
    fun showGeneralNotification(title: String, message: String) {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_GENERAL, notification)
    }
    
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
