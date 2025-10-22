package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import kotlinx.datetime.*

/**
 * Transaction analysis engine
 * iOS/Android pattern: Provides spending insights and trends
 */
object TransactionAnalyzer {
    
    /**
     * Period data for analysis
     */
    data class PeriodData(
        val startMillis: Long,
        val endMillis: Long,
        val income: Long,
        val expense: Long,
        val balance: Long
    )
    
    /**
     * Get daily income/expense trend
     * Returns list of (date, income, expense) tuples
     */
    fun getDailyIncomeExpenseTrend(
        transactions: List<Transaction>,
        days: Int = 30
    ): List<Triple<LocalDate, Long, Long>> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date
        val startDate = today.minus(days, DateTimeUnit.DAY)
        
        return (0 until days).map { dayOffset ->
            val date = startDate.plus(dayOffset, DateTimeUnit.DAY)
            val dayStart = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val dayEnd = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val dayTransactions = transactions.filter { 
                it.timestampUtcMillis in dayStart..dayEnd
            }
            
            val income = dayTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            
            val expense = dayTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            Triple(date, income, expense)
        }
    }
    
    /**
     * Get monthly income/expense trend (simplified)
     * Returns list of (monthIndex, income, expense) tuples
     */
    fun getMonthlyIncomeExpenseTrend(
        transactions: List<Transaction>,
        months: Int = 12
    ): List<Triple<Int, Long, Long>> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = now.toLocalDateTime(timeZone).date
        
        return (0 until months).map { monthOffset ->
            val targetDate = currentDate.minus(monthOffset, DateTimeUnit.MONTH)
            val monthStart = targetDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val monthEnd = targetDate.plus(1, DateTimeUnit.MONTH).atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val monthTransactions = transactions.filter { 
                it.timestampUtcMillis in monthStart..monthEnd
            }
            
            val income = monthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            
            val expense = monthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            Triple(monthOffset, income, expense)
        }
    }
    
    /**
     * Calculate daily trend
     */
    fun calculateDailyTrend(
        transactions: List<Transaction>,
        days: Int = 30
    ): List<PeriodData> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date
        
        return (0 until days).map { dayOffset ->
            val date = today.minus(dayOffset, DateTimeUnit.DAY)
            val dayStart = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val dayEnd = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val dayTransactions = transactions.filter { 
                it.timestampUtcMillis in dayStart..dayEnd
            }
            
            val income = dayTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            
            val expense = dayTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            PeriodData(
                startMillis = dayStart,
                endMillis = dayEnd,
                income = income,
                expense = expense,
                balance = income - expense
            )
        }
    }
    
    /**
     * Calculate total income
     */
    fun calculateTotalIncome(transactions: List<Transaction>): Long {
        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.minorUnits }
    }
    
    /**
     * Calculate total expense
     */
    fun calculateTotalExpense(transactions: List<Transaction>): Long {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits }
    }
    
    /**
     * Calculate current balance
     */
    fun calculateCurrentBalance(transactions: List<Transaction>): Long {
        val income = calculateTotalIncome(transactions)
        val expense = calculateTotalExpense(transactions)
        return income - expense
    }
}