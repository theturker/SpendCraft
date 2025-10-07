package com.alperen.spendcraft.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.alperen.spendcraft.shared.data.mappers.toDomain
import com.alperen.spendcraft.shared.database.SpendCraftDatabase
import com.alperen.spendcraft.shared.domain.model.Account
import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.model.Money
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionsRepositoryImpl(
    private val database: SpendCraftDatabase
) : TransactionsRepository {
    
    private val transactionQueries = database.transactionEntityQueries
    private val categoryQueries = database.categoryEntityQueries
    private val accountQueries = database.accountEntityQueries
    
    override fun observeTransactions(): Flow<List<Transaction>> {
        return transactionQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        amount = Money(entity.amountMinorUnits),
                        timestampUtcMillis = entity.timestampUtcMillis,
                        note = entity.note,
                        categoryId = entity.categoryId,
                        accountId = entity.accountId,
                        type = TransactionType.valueOf(entity.type)
                    )
                }
            }
    }
    
    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionQueries.selectByAccount(accountId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        amount = Money(entity.amountMinorUnits),
                        timestampUtcMillis = entity.timestampUtcMillis,
                        note = entity.note,
                        categoryId = entity.categoryId,
                        accountId = entity.accountId,
                        type = TransactionType.valueOf(entity.type)
                    )
                }
            }
    }
    
    override fun observeCategories(): Flow<List<Category>> {
        return categoryQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
    
    override fun observeAccounts(): Flow<List<Account>> {
        return accountQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map {
                    Account(id = it.id, name = it.name)
                }
            }
    }
    
    override suspend fun upsert(transaction: Transaction) {
        if (transaction.id == null) {
            transactionQueries.insert(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name
            )
        } else {
            transactionQueries.update(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name,
                id = transaction.id
            )
        }
    }
    
    override suspend fun delete(transactionId: Long) {
        transactionQueries.delete(transactionId)
    }
    
    override suspend fun insertCategory(category: Category): Long {
        categoryQueries.insert(
            name = category.name,
            color = category.color,
            icon = category.icon
        )
        return categoryQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun deleteCategory(categoryId: Long) {
        categoryQueries.delete(categoryId)
    }
    
    override suspend fun insertAccount(account: Account): Long {
        accountQueries.insert(account.name)
        return accountQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateAccount(account: Account) {
        account.id?.let {
            accountQueries.update(name = account.name, id = it)
        }
    }
    
    override suspend fun deleteAccount(accountId: Long) {
        accountQueries.delete(accountId)
    }
    
    override suspend fun getDefaultAccount(): Account? {
        return accountQueries.selectAll().executeAsList().firstOrNull()?.let {
            Account(id = it.id, name = it.name)
        }
    }
    
    override suspend fun getAllAscending(): List<Transaction> {
        return transactionQueries.selectAllAscending().executeAsList().map { entity ->
            Transaction(
                id = entity.id,
                amount = Money(entity.amountMinorUnits),
                timestampUtcMillis = entity.timestampUtcMillis,
                note = entity.note,
                categoryId = entity.categoryId,
                accountId = entity.accountId,
                type = TransactionType.valueOf(entity.type)
            )
        }
    }
    
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> {
        return transactionQueries.getSpentByCategory().executeAsList()
            .associate { it.categoryId.toString() to (it.totalAmount ?: 0L) }
    }
}

