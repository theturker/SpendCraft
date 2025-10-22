package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import kotlinx.datetime.*

/**
 * Transaction analysis engine
 * Provides time-based analytics (daily, weekly, monthly)
 */
object TransactionAnalyzer {
    
    /**
     * Period data point (for charts)
     */
    data class PeriodData(
        val startMillis: Long,
        val endMillis: Long,
        val income: Long,
        val expense: Long,
        val balance: Long
    )
    
    /**
     * Calculate daily trend data
     * iOS pattern: Used for line charts
     */
    fun calculateDailyTrend(
        transactions: List<Transaction>,
        days: Int = 30
    ): List<PeriodData> {
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
     * Calculate weekly trend data
     * iOS pattern: Used for weekly charts
     */
    fun calculateWeeklyTrend(
        transactions: List<Transaction>,
        weeks: Int = 12
    ): List<PeriodData> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date
        val startDate = today.minus(weeks * 7, DateTimeUnit.DAY)
        
        return (0 until weeks).map { weekOffset ->
            val weekStart = startDate.plus(weekOffset * 7, DateTimeUnit.DAY)
            val weekEnd = weekStart.plus(7, DateTimeUnit.DAY)
            
            val weekStartMillis = weekStart.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val weekEndMillis = weekEnd.atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val weekTransactions = transactions.filter { 
                it.timestampUtcMillis in weekStartMillis..weekEndMillis
            }
            
            val income = weekTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            
            val expense = weekTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            PeriodData(
                startMillis = weekStartMillis,
                endMillis = weekEndMillis,
                income = income,
                expense = expense,
                balance = income - expense
            )
        }
    }
    
    /**
     * Calculate monthly trend data
     * iOS pattern: Used for monthly charts
     */
    fun calculateMonthlyTrend(
        transactions: List<Transaction>,
        months: Int = 12
    ): List<PeriodData> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = now.toLocalDateTime(timeZone).date
        
        return (0 until months).map { monthOffset ->
            val monthDate = currentDate.minus(months - monthOffset - 1, DateTimeUnit.MONTH)
            val monthStart = LocalDate(monthDate.year, monthDate.month, 1)
            val monthEnd = monthStart.plus(1, DateTimeUnit.MONTH)
            
            val monthStartMillis = monthStart.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val monthEndMillis = monthEnd.atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val monthTransactions = transactions.filter { 
                it.timestampUtcMillis in monthStartMillis..monthEndMillis
            }
            
            val income = monthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            
            val expense = monthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            PeriodData(
                startMillis = monthStartMillis,
                endMillis = monthEndMillis,
                income = income,
                expense = expense,
                balance = income - expense
            )
        }
    }
    
    /**
     * Calculate current month summary
     */
    fun calculateCurrentMonth(
        transactions: List<Transaction>,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): MonthSummary {
        val instant = Instant.fromEpochMilliseconds(nowMillis)
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = instant.toLocalDateTime(timeZone).date
        
        val monthStart = LocalDate(currentDate.year, currentDate.month, 1)
        val monthStartMillis = monthStart.atStartOfDayIn(timeZone).toEpochMilliseconds()
        
        val monthTransactions = transactions.filter { 
            it.timestampUtcMillis >= monthStartMillis
        }
        
        val income = monthTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.minorUnits }
        
        val expense = monthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits }
        
        return MonthSummary(
            income = income,
            expense = expense,
            balance = income - expense,
            transactionCount = monthTransactions.size,
            averageDaily = expense / currentDate.dayOfMonth
        )
    }
    
    data class MonthSummary(
        val income: Long,
        val expense: Long,
        val balance: Long,
        val transactionCount: Int,
        val averageDaily: Long
    )
    
    /**
     * Calculate total income (iOS compatibility)
     */
    fun calculateTotalIncome(transactions: List<Transaction>): Long {
        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.minorUnits }
    }
    
    /**
     * Calculate total expense (iOS compatibility)
     */
    fun calculateTotalExpense(transactions: List<Transaction>): Long {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits }
    }
    
    /**
     * Calculate current balance (iOS compatibility)
     */
    fun calculateCurrentBalance(transactions: List<Transaction>): Long {
        val income = calculateTotalIncome(transactions)
        val expense = calculateTotalExpense(transactions)
        return income - expense
    }
}




