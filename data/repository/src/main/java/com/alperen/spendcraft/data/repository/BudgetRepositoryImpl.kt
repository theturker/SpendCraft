package com.alperen.spendcraft.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao
import com.alperen.spendcraft.data.db.dao.BudgetDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.data.db.entities.BudgetAlertEntity
import com.alperen.spendcraft.data.db.entities.BudgetEntity
import com.alperen.spendcraft.domain.repo.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budgetAlertDao: BudgetAlertDao,
    private val txDao: TxDao,
    private val categoryDao: CategoryDao
) : BudgetRepository {

    override fun observeBudgets(): Flow<List<Budget>> {
        return budgetDao.observeAll().map { entities ->
            entities.map { entity ->
                Budget(
                    categoryId = entity.categoryId,
                    monthlyLimitMinor = entity.monthlyLimitMinor
                )
            }
        }
    }

    override suspend fun upsertBudget(budget: Budget) {
        val entity = BudgetEntity(
            categoryId = budget.categoryId,
            monthlyLimitMinor = budget.monthlyLimitMinor
        )
        budgetDao.upsert(entity)
    }

    override suspend fun deleteBudget(categoryId: String) {
        budgetDao.deleteByCategory(categoryId)
    }

    override suspend fun getBudget(categoryId: String): Budget? {
        return budgetDao.getByCategory(categoryId)?.let { entity ->
            Budget(
                categoryId = entity.categoryId,
                monthlyLimitMinor = entity.monthlyLimitMinor
            )
        }
    }

    override suspend fun checkBudgetBreaches(): List<String> {
        val currentMonth = getCurrentMonthKey()
        val (monthStart, monthEnd) = getMonthBoundaries()
        val breachMessages = mutableListOf<String>()

        // Get all data using suspend functions
        val budgets = budgetDao.getAllAscending()
        val transactions = txDao.getAllAscending()
        val categories = categoryDao.getAllAscending()

        // Filter transactions for current month (expenses only)
        val monthTransactions = transactions.filter { tx ->
            tx.timestampUtcMillis >= monthStart && tx.timestampUtcMillis < monthEnd && !tx.isIncome
        }

        budgets.forEach { budgetEntity ->
            val categoryName = budgetEntity.categoryId
            val limit = budgetEntity.monthlyLimitMinor

            // Calculate spent amount for this category
            val categoryTransactions = monthTransactions.filter { tx ->
                val category = categories.find { it.id == tx.categoryId }
                category?.name == categoryName
            }
            val spentAmount = categoryTransactions.sumOf { it.amountMinor }

            // Check for breaches
            val percentage = if (limit > 0) (spentAmount * 100) / limit else 0

            if (percentage >= 100) {
                // 100% breach
                val alertExists = budgetAlertDao.alertExists(categoryName, 100, currentMonth) > 0
                if (!alertExists) {
                    breachMessages.add("Budget exceeded for $categoryName! Spent: ${formatAmount(spentAmount)}, Budget: ${formatAmount(limit)}")
                    budgetAlertDao.insert(BudgetAlertEntity(categoryName, 100, currentMonth))
                }
            } else if (percentage >= 80) {
                // 80% breach
                val alertExists = budgetAlertDao.alertExists(categoryName, 80, currentMonth) > 0
                if (!alertExists) {
                    breachMessages.add("Budget warning for $categoryName! Spent: ${formatAmount(spentAmount)}, Budget: ${formatAmount(limit)} (${percentage}%)")
                    budgetAlertDao.insert(BudgetAlertEntity(categoryName, 80, currentMonth))
                }
            }
        }

        return breachMessages
    }

    override suspend fun markAlertSent(categoryId: String, level: Int, monthKey: String) {
        budgetAlertDao.insert(BudgetAlertEntity(categoryId, level, monthKey))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentMonthKey(): String {
        return Instant.now()
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthBoundaries(): Pair<Long, Long> {
        val now = Instant.now().atZone(ZoneOffset.UTC)
        val monthStart = now.with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        val monthEnd = now.with(TemporalAdjusters.firstDayOfNextMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        
        return Pair(monthStart.toInstant().toEpochMilli(), monthEnd.toInstant().toEpochMilli())
    }

    private fun formatAmount(amountMinor: Long): String {
        return "₺${amountMinor / 100}.${(amountMinor % 100).toString().padStart(2, '0')}"
    }
}
