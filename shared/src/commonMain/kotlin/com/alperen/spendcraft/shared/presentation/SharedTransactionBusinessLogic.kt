package com.alperen.spendcraft.shared.presentation

import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.calculator.TransactionAnalyzer
import com.alperen.spendcraft.shared.domain.calculator.CategoryAnalyzer
import com.alperen.spendcraft.shared.domain.calculator.StreakCalculator
import com.alperen.spendcraft.shared.domain.calculator.BudgetCalculator
import com.alperen.spendcraft.shared.domain.validation.TransactionValidator
import com.alperen.spendcraft.shared.domain.validation.ValidationResult
import kotlinx.datetime.*

/**
 * Shared transaction business logic
 * iOS/Android pattern: Encapsulates transaction-related business rules
 */
class SharedTransactionBusinessLogic {
    
    /**
     * Validate transaction
     */
    fun validateTransaction(transaction: Transaction): ValidationResult {
        return TransactionValidator.validate(transaction)
    }
    
    /**
     * Get daily income/expense trend
     */
    fun getDailyIncomeExpenseTrend(transactions: List<Transaction>, days: Int = 30): List<Triple<LocalDate, Long, Long>> {
        return TransactionAnalyzer.getDailyIncomeExpenseTrend(transactions, days)
    }
    
    /**
     * Get monthly income/expense trend
     */
    fun getMonthlyIncomeExpenseTrend(transactions: List<Transaction>, months: Int = 12): List<Triple<Int, Long, Long>> {
        return TransactionAnalyzer.getMonthlyIncomeExpenseTrend(transactions, months)
    }
    
    /**
     * Get top spending categories
     */
    fun getTopCategories(transactions: List<Transaction>, categories: List<Category>, limit: Int = 5): List<Pair<Category, Long>> {
        return CategoryAnalyzer.getTopCategories(transactions, limit)
    }
    
    /**
     * Get category spending trend
     */
    fun getCategorySpendingTrend(transactions: List<Transaction>, categoryId: Long, days: Int = 30): Map<LocalDate, Long> {
        return CategoryAnalyzer.getCategorySpendingTrend(transactions, categoryId, days)
    }
    
    /**
     * Calculate current balance
     */
    fun calculateCurrentBalance(transactions: List<Transaction>): Long {
        return TransactionAnalyzer.calculateCurrentBalance(transactions)
    }
    
    /**
     * Calculate total income
     */
    fun calculateTotalIncome(transactions: List<Transaction>): Long {
        return TransactionAnalyzer.calculateTotalIncome(transactions)
    }
    
    /**
     * Calculate total expense
     */
    fun calculateTotalExpense(transactions: List<Transaction>): Long {
        return TransactionAnalyzer.calculateTotalExpense(transactions)
    }
    
    /**
     * Calculate current streak
     */
    fun calculateCurrentStreak(transactionTimestamps: List<Long>, nowMillis: Long = Clock.System.now().toEpochMilliseconds()): Streak {
        return StreakCalculator.calculateFromTransactions(transactionTimestamps, nowMillis)
    }
    
    /**
     * Calculate best streak
     */
    fun calculateBestStreak(transactionTimestamps: List<Long>): Streak {
        return StreakCalculator.calculateFromTransactions(transactionTimestamps).copy(current = 0) // Only best streak
    }
    
    /**
     * Calculate daily allowance
     */
    fun calculateDailyAllowance(budgetLimit: Long, spent: Long, daysRemaining: Int): Long {
        return BudgetCalculator.calculateDailyAllowance(budgetLimit, spent, daysRemaining)
    }
}