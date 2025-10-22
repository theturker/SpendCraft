package com.alperen.spendcraft.shared.domain.rules

import com.alperen.spendcraft.shared.domain.validation.BudgetValidator
import kotlinx.datetime.Clock

/**
 * Notification triggering rules
 * Platform-agnostic business logic for when to send notifications
 */
object NotificationRules {
    
    /**
     * Check if budget alert should be sent
     * iOS/Android pattern: Only for critical levels, max once per 24h
     */
    fun shouldSendBudgetAlert(
        breachLevel: BudgetValidator.BreachLevel,
        lastAlertSentMillis: Long
    ): Boolean {
        // Only send for critical and exceeded
        if (breachLevel !in listOf(
                BudgetValidator.BreachLevel.Critical,
                BudgetValidator.BreachLevel.Exceeded
            )) {
            return false
        }
        
        // Minimum 24 hours between alerts
        val now = Clock.System.now().toEpochMilliseconds()
        val hoursSinceLastAlert = (now - lastAlertSentMillis) / 3_600_000
        
        return hoursSinceLastAlert >= 24
    }
    
    /**
     * Check if daily spending reminder should be sent
     * iOS pattern: Send at 8 PM if no transaction today
     */
    fun shouldSendDailyReminder(
        lastTransactionMillis: Long,
        currentHour: Int
    ): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val hoursSinceLastTransaction = (now - lastTransactionMillis) / 3_600_000
        
        // Send reminder at 8 PM if no transaction in 24 hours
        return currentHour == 20 && hoursSinceLastTransaction >= 24
    }
    
    /**
     * Check if salary reminder should be sent
     * iOS pattern: First 5 days of month
     */
    fun shouldSendSalaryReminder(
        currentDayOfMonth: Int,
        hasIncomeThisMonth: Boolean
    ): Boolean {
        return currentDayOfMonth in 1..5 && !hasIncomeThisMonth
    }
    
    /**
     * Check if achievement notification should be sent
     */
    fun shouldSendAchievementNotification(
        achievementUnlocked: Boolean,
        lastNotificationMillis: Long
    ): Boolean {
        if (!achievementUnlocked) return false
        
        // Don't spam: minimum 1 hour between achievement notifications
        val now = Clock.System.now().toEpochMilliseconds()
        val hoursSinceLastNotification = (now - lastNotificationMillis) / 3_600_000
        
        return hoursSinceLastNotification >= 1
    }
    
    /**
     * Check if streak broken notification should be sent
     * iOS/Android pattern: Send when streak drops to 0 after being > 0
     */
    fun shouldSendStreakBrokenNotification(
        previousStreak: Int,
        currentStreak: Int
    ): Boolean {
        return previousStreak > 0 && currentStreak == 0
    }
    
    /**
     * Get notification priority
     * Used for iOS/Android notification channels
     */
    fun getNotificationPriority(breachLevel: BudgetValidator.BreachLevel): NotificationPriority {
        return when (breachLevel) {
            BudgetValidator.BreachLevel.Exceeded -> NotificationPriority.HIGH
            BudgetValidator.BreachLevel.Critical -> NotificationPriority.HIGH
            BudgetValidator.BreachLevel.Warning -> NotificationPriority.DEFAULT
            BudgetValidator.BreachLevel.Safe -> NotificationPriority.LOW
        }
    }
    
    enum class NotificationPriority {
        LOW, DEFAULT, HIGH
    }
}




