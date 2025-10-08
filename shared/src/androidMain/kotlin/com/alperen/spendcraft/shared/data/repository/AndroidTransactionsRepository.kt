package com.alperen.spendcraft.shared.data.repository

import com.alperen.spendcraft.shared.domain.model.Account
import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Android Room-based repository implementation.
 * Bu implementation Android'deki mevcut Room database ile çalışır.
 * Android app module'ünde Room DAO'ları ile entegre edilecek.
 */
class AndroidTransactionsRepository : TransactionsRepository {
    
    // Android tarafında Room ile implement edilecek
    // Şimdilik placeholder implementation
    
    override fun observeTransactions(): Flow<List<Transaction>> {
        return flowOf(emptyList())
    }
    
    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return flowOf(emptyList())
    }
    
    override fun observeCategories(): Flow<List<Category>> {
        return flowOf(emptyList())
    }
    
    override fun observeAccounts(): Flow<List<Account>> {
        return flowOf(emptyList())
    }
    
    override suspend fun upsert(transaction: Transaction) {
        // Room DAO ile implement edilecek
    }
    
    override suspend fun delete(transactionId: Long) {
        // Room DAO ile implement edilecek
    }
    
    override suspend fun insertCategory(category: Category): Long {
        return 0L
    }
    
    override suspend fun deleteCategory(categoryId: Long) {
        // Room DAO ile implement edilecek
    }
    
    override suspend fun insertAccount(account: Account): Long {
        return 0L
    }
    
    override suspend fun updateAccount(account: Account) {
        // Room DAO ile implement edilecek
    }
    
    override suspend fun deleteAccount(accountId: Long) {
        // Room DAO ile implement edilecek
    }
    
    override suspend fun getDefaultAccount(): Account? {
        return null
    }
    
    override suspend fun getAllAscending(): List<Transaction> {
        return emptyList()
    }
    
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> {
        return emptyMap()
    }
}

