package com.alperen.spendcraft.domain.repo

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeCategories(): Flow<List<Category>>
    suspend fun upsert(transaction: Transaction)
    suspend fun delete(transactionId: Long)
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(categoryId: Long)
}




