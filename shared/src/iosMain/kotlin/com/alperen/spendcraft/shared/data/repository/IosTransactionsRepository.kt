package com.alperen.spendcraft.shared.data.repository

import com.alperen.spendcraft.shared.domain.model.Account
import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.model.Money
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS CoreData-based repository implementation.
 * Bu implementation iOS native CoreData ile çalışır.
 * Swift tarafında CoreDataManager ile entegre edilecek.
 */
class IosTransactionsRepository : TransactionsRepository {
    
    // StateFlows for reactive data
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    
    override fun observeTransactions(): Flow<List<Transaction>> = _transactions.asStateFlow()
    
    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return kotlinx.coroutines.flow.flow {
            _transactions.collect { transactions ->
                emit(transactions.filter { it.accountId == accountId })
            }
        }
    }
    
    override fun observeCategories(): Flow<List<Category>> = _categories.asStateFlow()
    
    override fun observeAccounts(): Flow<List<Account>> = _accounts.asStateFlow()
    
    override suspend fun upsert(transaction: Transaction) {
        // Swift tarafında implement edilecek
        // Bu fonksiyon Swift'ten çağrılacak ve CoreData'ya kaydedilecek
    }
    
    override suspend fun delete(transactionId: Long) {
        // Swift tarafında implement edilecek
    }
    
    override suspend fun insertCategory(category: Category): Long {
        // Swift tarafında implement edilecek
        return 0L
    }
    
    override suspend fun deleteCategory(categoryId: Long) {
        // Swift tarafında implement edilecek
    }
    
    override suspend fun insertAccount(account: Account): Long {
        // Swift tarafında implement edilecek
        return 0L
    }
    
    override suspend fun updateAccount(account: Account) {
        // Swift tarafında implement edilecek
    }
    
    override suspend fun deleteAccount(accountId: Long) {
        // Swift tarafında implement edilecek
    }
    
    override suspend fun getDefaultAccount(): Account? {
        return _accounts.value.firstOrNull()
    }
    
    override suspend fun getAllAscending(): List<Transaction> {
        return _transactions.value.sortedBy { it.timestampUtcMillis }
    }
    
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> {
        return _transactions.value
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId?.toString() ?: "0" }
            .mapValues { entry ->
                entry.value.sumOf { it.amount.minorUnits }
            }
    }
    
    // Helper functions for Swift to update flows
    fun updateTransactions(transactions: List<Transaction>) {
        _transactions.value = transactions
    }
    
    fun updateCategories(categories: List<Category>) {
        _categories.value = categories
    }
    
    fun updateAccounts(accounts: List<Account>) {
        _accounts.value = accounts
    }
}

