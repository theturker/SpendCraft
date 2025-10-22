package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import kotlinx.datetime.*

/**
 * Category analysis engine
 * iOS/Android pattern: Provides spending insights by category
 */
object CategoryAnalyzer {
    
    /**
     * Category spending insight
     */
    data class CategoryInsight(
        val category: Category,
        val totalSpent: Long,
        val percentage: Double,
        val averageTransaction: Long,
        val transactionCount: Int,
        val trend: Trend,
        val lastTransaction: Long? // timestamp
    )
    
    enum class Trend {
        UP,      // Spending increasing
        DOWN,    // Spending decreasing
        STABLE   // No significant change
    }
    
    /**
     * Analyze all categories
     * Returns insights sorted by spending (highest first)
     */
    fun analyzeCategories(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<CategoryInsight> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val totalSpent = expenseTransactions.sumOf { it.amount.minorUnits }
        
        return categories.mapNotNull { category ->
            val categoryTransactions = expenseTransactions.filter { it.categoryId == category.id }
            
            if (categoryTransactions.isEmpty()) {
                null
            } else {
                val categorySpent = categoryTransactions.sumOf { it.amount.minorUnits }
                val percentage = if (totalSpent > 0) {
                    (categorySpent.toDouble() / totalSpent * 100)
                } else 0.0
                
                val averageTransaction = categorySpent / categoryTransactions.size
                val trend = calculateTrend(categoryTransactions)
                val lastTransaction = categoryTransactions.maxOfOrNull { it.timestampUtcMillis }
                
                CategoryInsight(
                    category = category,
                    totalSpent = categorySpent,
                    percentage = percentage,
                    averageTransaction = averageTransaction,
                    transactionCount = categoryTransactions.size,
                    trend = trend,
                    lastTransaction = lastTransaction
                )
            }
        }.sortedByDescending { it.totalSpent }
    }
    
    /**
     * Calculate spending trend
     * Compares recent vs older transactions
     */
    private fun calculateTrend(transactions: List<Transaction>): Trend {
        if (transactions.size < 4) return Trend.STABLE
        
        val sorted = transactions.sortedByDescending { it.timestampUtcMillis }
        val midpoint = transactions.size / 2
        
        val recentTransactions = sorted.take(midpoint)
        val olderTransactions = sorted.drop(midpoint)
        
        val recentAvg = recentTransactions.map { it.amount.minorUnits }.average()
        val olderAvg = olderTransactions.map { it.amount.minorUnits }.average()
        
        return when {
            recentAvg > olderAvg * 1.15 -> Trend.UP      // 15% increase
            recentAvg < olderAvg * 0.85 -> Trend.DOWN    // 15% decrease
            else -> Trend.STABLE
        }
    }
    
    /**
     * Calculate category distribution for pie charts
     */
    fun calculateDistribution(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<Pair<Category, Double>> {
        val insights = analyzeCategories(transactions, categories)
        
        return insights.map { insight ->
            insight.category to insight.percentage
        }
    }
    
    /**
     * Get top spending categories
     */
    fun getTopCategories(transactions: List<Transaction>, limit: Int = 5): List<Pair<Category, Long>> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val categorySpending = expenseTransactions
            .groupBy { it.categoryId }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount.minorUnits } }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
        
        // Convert category IDs to Category objects (simplified)
        return categorySpending.map { (categoryId, amount) ->
            Category(id = categoryId, name = "Category $categoryId", color = "#007AFF") to amount
        }
    }
    
    /**
     * Get category spending trend
     */
    fun getCategorySpendingTrend(
        transactions: List<Transaction>, 
        categoryId: Long, 
        days: Int = 30
    ): Map<LocalDate, Long> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date
        val startDate = today.minus(days, DateTimeUnit.DAY)
        
        val categoryTransactions = transactions.filter { 
            it.categoryId == categoryId && it.type == TransactionType.EXPENSE
        }
        
        return (0 until days).associate { dayOffset ->
            val date = startDate.plus(dayOffset, DateTimeUnit.DAY)
            val dayStart = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
            val dayEnd = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
            
            val dayTransactions = categoryTransactions.filter { 
                it.timestampUtcMillis in dayStart..dayEnd
            }
            
            val daySpent = dayTransactions.sumOf { it.amount.minorUnits }
            date to daySpent
        }
    }
}