package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Budget
import com.alperen.spendcraft.shared.domain.validation.BudgetValidator
import kotlinx.datetime.*

/**
 * Budget calculation engine
 * Platform-agnostic business logic for budget analysis
 */
object BudgetCalculator {
    
    /**
     * Comprehensive budget analysis
     */
    data class BudgetAnalysis(
        val limit: Long,
        val spent: Long,
        val remaining: Long,
        val percentage: Double,
        val breachLevel: BudgetValidator.BreachLevel,
        val projectedEndOfMonth: Long,
        val dailyAllowance: Long,
        val daysRemaining: Int,
        val isOnTrack: Boolean
    )
    
    /**
     * Analyze budget for current month
     * Combines iOS and Android calculation logic
     */
    fun analyze(
        budget: Budget,
        spent: Long,
        timestampMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): BudgetAnalysis {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        val currentDayOfMonth = localDateTime.dayOfMonth
        val daysInMonth = getDaysInMonth(localDateTime.year, localDateTime.monthNumber)
        val daysRemaining = daysInMonth - currentDayOfMonth
        
        // Calculate remaining budget
        val remaining = budget.monthlyLimitMinor - spent
        
        // Calculate percentage
        val percentage = if (budget.monthlyLimitMinor > 0) {
            (spent.toDouble() / budget.monthlyLimitMinor * 100)
        } else 0.0
        
        // Calculate breach level
        val breachLevel = BudgetValidator.calculateBreachLevel(spent, budget.monthlyLimitMinor)
        
        // Project end of month spending (iOS pattern)
        val dailyAverage = if (currentDayOfMonth > 0) spent / currentDayOfMonth else 0
        val projectedEndOfMonth = dailyAverage * daysInMonth
        
        // Calculate daily allowance
        val dailyAllowance = if (daysRemaining > 0) remaining / daysRemaining.toLong() else 0
        
        // Check if on track (iOS/Android pattern)
        val isOnTrack = projectedEndOfMonth <= budget.monthlyLimitMinor
        
        return BudgetAnalysis(
            limit = budget.monthlyLimitMinor,
            spent = spent,
            remaining = remaining,
            percentage = percentage,
            breachLevel = breachLevel,
            projectedEndOfMonth = projectedEndOfMonth,
            dailyAllowance = dailyAllowance,
            daysRemaining = daysRemaining,
            isOnTrack = isOnTrack
        )
    }
    
    /**
     * Calculate recommended daily spending
     */
    fun calculateDailyRecommendation(
        budget: Budget,
        spent: Long,
        currentDayOfMonth: Int,
        daysInMonth: Int
    ): Long {
        val remaining = budget.monthlyLimitMinor - spent
        val daysRemaining = daysInMonth - currentDayOfMonth
        
        return if (daysRemaining > 0) {
            remaining / daysRemaining
        } else {
            0L
        }
    }
    
    /**
     * Calculate savings potential
     */
    fun calculateSavingsPotential(
        budgetLimit: Long,
        actualSpent: Long
    ): Long {
        return kotlin.math.max(0L, budgetLimit - actualSpent)
    }
    
    /**
     * Get days in month (helper function)
     */
    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }
    
    /**
     * Check if year is leap year
     */
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}

