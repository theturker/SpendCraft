package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.model.Account
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionsRepositoryImpl(
    private val txDao: TxDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) : TransactionsRepository {

    override fun observeTransactions(): Flow<List<Transaction>> =
        txDao.observeAll().map { it.map(TransactionMapper::fromEntity) }

    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> =
        txDao.observeByAccount(accountId).map { it.map(TransactionMapper::fromEntity) }

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { it.map(CategoryMapper::fromEntity) }

    override fun observeAccounts(): Flow<List<Account>> =
        accountDao.observeAll().map { it.map(AccountMapper::fromEntity) }

    override suspend fun upsert(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        val id: Long = transaction.id ?: 0L
        if (id == 0L) {
            txDao.insert(entity)
        } else {
            txDao.update(entity.copy(id = id))
        }
    }

    override suspend fun delete(transactionId: Long) {
        txDao.deleteById(transactionId)
    }
    
    override suspend fun insertCategory(category: Category): Long {
        val entity = CategoryMapper.toEntity(category)
        return categoryDao.insert(entity)
    }
    
    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteById(categoryId)
    }
    
    override suspend fun insertAccount(account: Account): Long {
        val entity = AccountMapper.toEntity(account)
        return accountDao.insert(entity)
    }
    
    override suspend fun updateAccount(account: Account) {
        val entity = AccountMapper.toEntity(account)
        accountDao.update(entity)
    }
    
    override suspend fun deleteAccount(accountId: Long) {
        accountDao.deleteById(accountId)
    }
    
    override suspend fun getDefaultAccount(): Account? {
        return accountDao.getDefaultAccount()?.let { AccountMapper.fromEntity(it) }
    }
    
    
    override suspend fun getAllAscending(): List<Transaction> {
        return txDao.getAllAscending().map { entity ->
            TransactionMapper.fromEntity(entity)
        }
    }
    
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> {
        val transactions = txDao.getAllAscending()
        val categories = categoryDao.getAllAscending()
        
        // Get current month boundaries
        val now = java.time.Instant.now().atZone(java.time.ZoneOffset.UTC)
        val monthStart = now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        val monthEnd = now.with(java.time.temporal.TemporalAdjusters.firstDayOfNextMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        
        val monthStartMillis = monthStart.toInstant().toEpochMilli()
        val monthEndMillis = monthEnd.toInstant().toEpochMilli()
        
        // Filter transactions for current month (expenses only)
        val monthTransactions = transactions.filter { tx ->
            tx.timestampUtcMillis >= monthStartMillis && 
            tx.timestampUtcMillis < monthEndMillis && 
            !tx.isIncome
        }
        
        // Group by category and sum amounts
        val spentAmounts = mutableMapOf<String, Long>()
        
        monthTransactions.forEach { tx ->
            val category = categories.find { it.id == tx.categoryId }
            val categoryName = category?.name ?: "Bilinmeyen"
            spentAmounts[categoryName] = (spentAmounts[categoryName] ?: 0L) + tx.amountMinor
        }
        
        return spentAmounts
    }
}


