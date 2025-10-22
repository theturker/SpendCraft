package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType

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
                
                CategoryInsight(
                    category = category,
                    totalSpent = categorySpent,
                    percentage = percentage,
                    averageTransaction = categorySpent / categoryTransactions.size,
                    transactionCount = categoryTransactions.size,
                    trend = calculateTrend(categoryTransactions),
                    lastTransaction = categoryTransactions.maxOfOrNull { it.timestampUtcMillis }
                )
            }
        }
        .sortedByDescending { it.totalSpent }
    }
    
    /**
     * Get top N categories by spending
     */
    fun getTopCategories(
        transactions: List<Transaction>,
        categories: List<Category>,
        limit: Int = 5
    ): List<CategoryInsight> {
        return analyzeCategories(transactions, categories).take(limit)
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
}




