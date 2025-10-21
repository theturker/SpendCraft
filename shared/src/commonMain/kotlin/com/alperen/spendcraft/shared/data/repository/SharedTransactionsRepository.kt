package com.alperen.spendcraft.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.alperen.spendcraft.shared.database.SpendCraftDatabase
import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Shared TransactionsRepository implementation using SQLDelight
 * 
 * Bu implementasyon hem iOS hem Android tarafından kullanılacak.
 * Core Data ve Room yerine SQLDelight type-safe queries kullanıyor.
 * 
 * iOS: TransactionsViewModel → SharedTransactionsRepository → SQLDelight
 * Android: TransactionsViewModel → SharedTransactionsRepository → SQLDelight
 */
class SharedTransactionsRepository(
    private val database: SpendCraftDatabase
) : TransactionsRepository {
    
    private val transactionQueries get() = database.transactionQueries
    private val categoryQueries get() = database.categoryQueries
    private val accountQueries get() = database.accountQueries
    
    // MARK: - Transactions
    
    override fun observeTransactions(): Flow<List<Transaction>> {
        return transactionQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> 
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        amount = Money(
                            minorUnits = entity.amountMinorUnits,
                            currencyCode = "TRY" // TODO: Get from account
                        ),
                        timestampUtcMillis = entity.timestampUtcMillis,
                        note = entity.note,
                        categoryId = entity.categoryId,
                        accountId = entity.accountId,
                        type = when (entity.type) {
                            "INCOME" -> com.alperen.spendcraft.shared.domain.model.TransactionType.INCOME
                            "EXPENSE" -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                            else -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                        }
                    )
                }
            }
    }
    
    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionQueries.selectByAccount(accountId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> 
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        amount = Money(
                            minorUnits = entity.amountMinorUnits,
                            currencyCode = "TRY"
                        ),
                        timestampUtcMillis = entity.timestampUtcMillis,
                        note = entity.note,
                        categoryId = entity.categoryId,
                        accountId = entity.accountId,
                        type = when (entity.type) {
                            "INCOME" -> com.alperen.spendcraft.shared.domain.model.TransactionType.INCOME
                            "EXPENSE" -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                            else -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                        }
                    )
                }
            }
    }
    
    override suspend fun upsert(transaction: Transaction) = withContext(Dispatchers.Default) {
        if (transaction.id == null || transaction.id == 0L) {
            // Insert new transaction
            transactionQueries.insert(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name,
                isRecurring = 0,
                recurringFrequency = null
            )
        } else {
            // Update existing transaction
            transactionQueries.update(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name,
                isRecurring = 0,
                recurringFrequency = null,
                id = transaction.id
            )
        }
    }
    
    override suspend fun delete(transactionId: Long) = withContext(Dispatchers.Default) {
        transactionQueries.delete(transactionId)
    }
    
    override suspend fun getAllAscending(): List<Transaction> = withContext(Dispatchers.Default) {
        transactionQueries.selectAllAscending().executeAsList().map { entity ->
            Transaction(
                id = entity.id,
                amount = Money(
                    minorUnits = entity.amountMinorUnits,
                    currencyCode = "TRY"
                ),
                timestampUtcMillis = entity.timestampUtcMillis,
                note = entity.note,
                categoryId = entity.categoryId,
                accountId = entity.accountId,
                type = when (entity.type) {
                    "INCOME" -> com.alperen.spendcraft.shared.domain.model.TransactionType.INCOME
                    "EXPENSE" -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                    else -> com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
                }
            )
        }
    }
    
    // MARK: - Categories
    
    override fun observeCategories(): Flow<List<Category>> {
        return categoryQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> 
                entities.map { entity ->
                    Category(
                        id = entity.id,
                        name = entity.name,
                        color = entity.color,
                        icon = entity.icon,
                        type = if (entity.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
                        isDefault = entity.isDefault == 1L
                    )
                }
            }
    }
    
    override suspend fun insertCategory(category: Category): Long = withContext(Dispatchers.Default) {
        categoryQueries.insert(
            name = category.name,
            color = category.color,
            icon = category.icon,
            type = when (category.type) {
                TransactionType.INCOME -> "INCOME"
                TransactionType.EXPENSE -> "EXPENSE"
            },
            isDefault = if (category.isDefault) 1 else 0
        )
        categoryQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun deleteCategory(categoryId: Long) = withContext(Dispatchers.Default) {
        categoryQueries.delete(categoryId)
    }
    
    // MARK: - Accounts
    
    override fun observeAccounts(): Flow<List<Account>> {
        return accountQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> 
                entities.map { entity ->
                    Account(
                        id = entity.id,
                        name = entity.name,
                        type = entity.type,
                        currency = entity.currency,
                        isDefault = entity.isDefault == 1L,
                        archived = entity.archived == 1L
                    )
                }
            }
    }
    
    override suspend fun insertAccount(account: Account): Long = withContext(Dispatchers.Default) {
        accountQueries.insert(
            name = account.name,
            type = account.type,
            currency = account.currency,
            isDefault = if (account.isDefault) 1 else 0,
            archived = if (account.archived) 1 else 0
        )
        accountQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateAccount(account: Account) = withContext(Dispatchers.Default) {
        accountQueries.update(
            name = account.name,
            type = account.type,
            currency = account.currency,
            isDefault = if (account.isDefault) 1 else 0,
            archived = if (account.archived) 1 else 0,
            id = account.id!!
        )
    }
    
    override suspend fun deleteAccount(accountId: Long) = withContext(Dispatchers.Default) {
        accountQueries.delete(accountId)
    }
    
    override suspend fun getDefaultAccount(): Account? = withContext(Dispatchers.Default) {
        accountQueries.selectDefault()
            .executeAsOneOrNull()
            ?.let { entity ->
                Account(
                    id = entity.id,
                    name = entity.name,
                    type = entity.type,
                    currency = entity.currency,
                    isDefault = entity.isDefault == 1L,
                    archived = entity.archived == 1L
                )
            }
    }
    
    // MARK: - Budget Calculations
    
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> = withContext(Dispatchers.Default) {
        transactionQueries.getSpentByCategory()
            .executeAsList()
            .associate { 
                (it.categoryId?.toString() ?: "0") to (it.totalAmount ?: 0L)
            }
    }
}

