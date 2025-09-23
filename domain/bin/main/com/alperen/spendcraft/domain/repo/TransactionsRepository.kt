package com.alperen.spendcraft.domain.repo

import com.alperen.spendcraft.core.model.Account
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>>
    fun observeCategories(): Flow<List<Category>>
    fun observeAccounts(): Flow<List<Account>>
    suspend fun upsert(transaction: Transaction)
    suspend fun delete(transactionId: Long)
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(categoryId: Long)
    suspend fun insertAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(accountId: Long)
    suspend fun getDefaultAccount(): Account?
    
    // CSV Import/Export
    suspend fun getAllAscending(): List<Transaction>
    
    // Budget calculations
    suspend fun getSpentAmountsByCategory(): Map<String, Long>
}




