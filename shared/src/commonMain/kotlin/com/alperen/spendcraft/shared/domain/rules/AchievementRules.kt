package com.alperen.spendcraft.shared.domain.rules

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.Budget
import kotlinx.datetime.*

/**
 * Achievement unlocking rules
 * Platform-agnostic business logic
 */
object AchievementRules {
    
    /**
     * Check if "First Transaction" achievement should be unlocked
     */
    fun checkFirstTransaction(transactionCount: Int): Boolean {
        return transactionCount >= 1
    }
    
    /**
     * Check if "Consecutive Days" streak achievement should be unlocked
     * Levels: 3, 7, 14, 30, 60, 90, 180, 365 days
     */
    fun checkConsecutiveDays(currentStreak: Int): List<AchievementLevel> {
        val levels = listOf(3, 7, 14, 30, 60, 90, 180, 365)
        
        return levels
            .filter { currentStreak >= it }
            .map { AchievementLevel.StreakDays(it) }
    }
    
    /**
     * Check if "Budget Master" achievement should be unlocked
     */
    fun checkBudgetMaster(budgetCount: Int): Boolean {
        return budgetCount >= 1
    }
    
    /**
     * Check if "Category Expert" achievement should be unlocked
     * Unlocks when user has categories in multiple types
     */
    fun checkCategoryExpert(categoryCount: Int, incomeCategoryCount: Int, expenseCategoryCount: Int): Boolean {
        return categoryCount >= 5 && incomeCategoryCount >= 1 && expenseCategoryCount >= 3
    }
    
    /**
     * Check if "Savings Champion" achievement should be unlocked
     */
    fun checkSavingsChampion(totalSavings: Long, savingsGoal: Long): Boolean {
        return totalSavings >= savingsGoal
    }
    
    /**
     * Check if "Transaction Master" achievement should be unlocked
     * Levels: 10, 50, 100, 500, 1000 transactions
     */
    fun checkTransactionMaster(transactionCount: Int): List<AchievementLevel> {
        val levels = listOf(10, 50, 100, 500, 1000)
        
        return levels
            .filter { transactionCount >= it }
            .map { AchievementLevel.TransactionCount(it) }
    }
    
    /**
     * Check if "Budget Keeper" achievement should be unlocked
     * Unlocked when user stays under budget for N months
     */
    fun checkBudgetKeeper(monthsUnderBudget: Int): List<AchievementLevel> {
        val levels = listOf(1, 3, 6, 12)
        
        return levels
            .filter { monthsUnderBudget >= it }
            .map { AchievementLevel.BudgetMonths(it) }
    }
    
    /**
     * Check if "Early Bird" achievement should be unlocked
     * Logs transaction before 9 AM
     */
    fun checkEarlyBird(transaction: Transaction): Boolean {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(transaction.timestampUtcMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        return localDateTime.hour < 9
    }
    
    /**
     * Check if "Night Owl" achievement should be unlocked
     * Logs transaction after 10 PM
     */
    fun checkNightOwl(transaction: Transaction): Boolean {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(transaction.timestampUtcMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        return localDateTime.hour >= 22
    }
    
    sealed class AchievementLevel {
        data class StreakDays(val days: Int) : AchievementLevel()
        data class TransactionCount(val count: Int) : AchievementLevel()
        data class BudgetMonths(val months: Int) : AchievementLevel()
    }
}

