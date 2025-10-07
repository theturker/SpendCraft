package com.alperen.spendcraft.shared.domain.repository

import com.alperen.spendcraft.shared.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeBudgets(): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget)
    suspend fun deleteBudget(categoryId: String)
    suspend fun getBudget(categoryId: String): Budget?
    suspend fun checkBudgetBreaches(): List<String> // Returns list of breach messages
    suspend fun markAlertSent(categoryId: String, level: Int, monthKey: String)
}

