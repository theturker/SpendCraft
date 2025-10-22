package com.alperen.spendcraft.shared.presentation

import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.calculator.BudgetCalculator
import com.alperen.spendcraft.shared.domain.rules.NotificationRules
import com.alperen.spendcraft.shared.domain.validation.BudgetValidator
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit

/**
 * Shared notification business logic
 * iOS/Android pattern: Encapsulates notification-related business rules
 */
class SharedNotificationBusinessLogic {
    
    /**
     * Should send budget alert
     */
    fun shouldSendBudgetAlert(analysis: BudgetCalculator.BudgetAnalysis, lastAlertSentMillis: Long): Boolean {
        return NotificationRules.shouldSendBudgetAlert(analysis.breachLevel, lastAlertSentMillis)
    }
    
    /**
     * Should send daily reminder
     */
    fun shouldSendDailyReminder(lastTransactionMillis: Long, currentHour: Int): Boolean {
        return NotificationRules.shouldSendDailyReminder(lastTransactionMillis, currentHour)
    }
    
    /**
     * Should send salary reminder
     */
    fun shouldSendSalaryReminder(currentDayOfMonth: Int, hasIncomeThisMonth: Boolean): Boolean {
        return NotificationRules.shouldSendSalaryReminder(currentDayOfMonth, hasIncomeThisMonth)
    }
    
    /**
     * Should send achievement notification
     */
    fun shouldSendAchievementNotification(achievementUnlocked: Boolean, lastNotificationMillis: Long): Boolean {
        return NotificationRules.shouldSendAchievementNotification(achievementUnlocked, lastNotificationMillis)
    }
    
    /**
     * Should send streak broken notification
     */
    fun shouldSendStreakBrokenNotification(previousStreak: Int, currentStreak: Int): Boolean {
        return NotificationRules.shouldSendStreakBrokenNotification(previousStreak, currentStreak)
    }
    
    /**
     * Get notification priority
     */
    fun getNotificationPriority(breachLevel: BudgetValidator.BreachLevel): NotificationRules.NotificationPriority {
        return NotificationRules.getNotificationPriority(breachLevel)
    }
    
    /**
     * Create budget alert notification
     */
    fun createBudgetAlertNotification(budget: Budget, analysis: BudgetCalculator.BudgetAnalysis): NotificationEvent {
        val title = "Bütçe Uyarısı: ${budget.categoryId}"
        val message = when (analysis.breachLevel) {
            BudgetValidator.BreachLevel.Critical -> "${budget.categoryId} bütçenizin %${analysis.percentage.toInt()}'sini aştınız!"
            BudgetValidator.BreachLevel.Exceeded -> "${budget.categoryId} bütçenizi aştınız!"
            else -> "Bütçenizde dikkat çeken bir durum var."
        }
        return NotificationEvent(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            type = NotificationType.BUDGET_ALERT,
            title = title,
            message = message,
            timestampUtcMillis = Clock.System.now().toEpochMilliseconds(),
            isRead = false,
            data = mapOf("categoryId" to budget.categoryId, "breachLevel" to analysis.breachLevel.name)
        )
    }
    
    /**
     * Create achievement unlocked notification
     */
    fun createAchievementUnlockedNotification(achievement: Achievement): NotificationEvent {
        return NotificationEvent(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            type = NotificationType.ACHIEVEMENT,
            title = "Başarım Kazanıldı: ${achievement.title}",
            message = achievement.description,
            timestampUtcMillis = Clock.System.now().toEpochMilliseconds(),
            isRead = false,
            data = mapOf("achievementId" to achievement.id)
        )
    }
    
    /**
     * Create streak notification
     */
    fun createStreakNotification(currentStreak: Int, bestStreak: Int): NotificationEvent {
        val title = "Seri Güncellemesi!"
        val message = if (currentStreak > 0) {
            "Harika! ${currentStreak} günlük serin devam ediyor. En iyi serin: ${bestStreak} gün."
        } else {
            "Serin bozuldu. Yeni bir seriye başlama zamanı!"
        }
        return NotificationEvent(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            type = NotificationType.STREAK_ALERT,
            title = title,
            message = message,
            timestampUtcMillis = Clock.System.now().toEpochMilliseconds(),
            isRead = false,
            data = mapOf("currentStreak" to currentStreak.toString(), "bestStreak" to bestStreak.toString())
        )
    }
    
    /**
     * Get next notification time
     */
    fun getNextNotificationTime(notificationType: NotificationType, lastSent: Long, intervalHours: Int): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        val intervalMillis = intervalHours * 3_600_000L
        
        return when (notificationType) {
            NotificationType.BUDGET_ALERT -> lastSent + intervalMillis
            NotificationType.STREAK_ALERT -> lastSent + intervalMillis
            NotificationType.ACHIEVEMENT_ALERT -> lastSent + (1 * 3_600_000L) // 1 hour
            NotificationType.REMINDER_ALERT -> lastSent + intervalMillis
            NotificationType.WEEKLY_SUMMARY -> lastSent + (7 * 24 * 3_600_000L) // 7 days
            NotificationType.MONTHLY_SUMMARY -> lastSent + (30 * 24 * 3_600_000L) // 30 days
            else -> lastSent + intervalMillis
        }
    }
    
    /**
     * Should send notification now
     */
    fun shouldSendNotificationNow(notificationType: NotificationType, lastSentMillis: Long, intervalHours: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val nextSendTime = getNextNotificationTime(notificationType, lastSentMillis, intervalHours)
        return now >= nextSendTime
    }
    
    /**
     * Filter notifications by time
     */
    fun filterNotificationsByTime(notifications: List<NotificationEvent>, days: Int): List<NotificationEvent> {
        val now = Clock.System.now()
        val cutoffMillis = now.toEpochMilliseconds() - (days * 24 * 60 * 60 * 1000L)
        return notifications.filter { it.timestampUtcMillis >= cutoffMillis }
    }
    
    /**
     * Filter notifications by type
     */
    fun filterNotificationsByType(notifications: List<NotificationEvent>, type: NotificationType): List<NotificationEvent> {
        return notifications.filter { it.type == type }
    }
    
    /**
     * Get unread notification count
     */
    fun getUnreadNotificationCount(notifications: List<NotificationEvent>): Int {
        return notifications.count { !it.isRead }
    }
}