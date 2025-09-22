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
}


