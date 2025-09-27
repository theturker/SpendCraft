package com.alperen.spendcraft.core.recurring

import com.alperen.spendcraft.data.db.dao.RecurringTransactionDao
import com.alperen.spendcraft.data.db.dao.TransactionDao
import com.alperen.spendcraft.data.db.entities.RecurringTransactionEntity
import com.alperen.spendcraft.data.db.entities.RecurringFrequency
import com.alperen.spendcraft.data.db.entities.TransactionEntity
import com.alperen.spendcraft.data.db.entities.TransactionType
import com.alperen.spendcraft.core.model.Money
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringTransactionManager @Inject constructor(
    private val recurringTransactionDao: RecurringTransactionDao,
    private val transactionDao: TransactionDao
) {
    
    suspend fun processDueRecurringTransactions() {
        val currentTime = System.currentTimeMillis()
        val dueTransactions = recurringTransactionDao.getDueRecurringTransactions(currentTime)
        
        dueTransactions.forEach { recurringTransaction ->
            createTransactionFromRecurring(recurringTransaction)
            updateNextExecution(recurringTransaction)
        }
    }
    
    private suspend fun createTransactionFromRecurring(recurringTransaction: RecurringTransactionEntity) {
        val transaction = TransactionEntity(
            id = 0, // Auto-generated
            amount = Money(recurringTransaction.amount),
            categoryId = recurringTransaction.categoryId,
            accountId = recurringTransaction.accountId,
            type = recurringTransaction.type,
            timestamp = System.currentTimeMillis(),
            note = recurringTransaction.note ?: "Tekrarlayan işlem: ${recurringTransaction.name}",
            isRecurring = true,
            recurringTransactionId = recurringTransaction.id
        )
        
        transactionDao.insertTransaction(transaction)
    }
    
    private suspend fun updateNextExecution(recurringTransaction: RecurringTransactionEntity) {
        val nextExecution = calculateNextExecution(
            recurringTransaction.frequency,
            recurringTransaction.nextExecution
        )
        
        recurringTransactionDao.updateExecutionTime(
            recurringTransaction.id,
            System.currentTimeMillis(),
            nextExecution
        )
    }
    
    private fun calculateNextExecution(frequency: RecurringFrequency, currentNextExecution: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentNextExecution
        
        return when (frequency) {
            RecurringFrequency.DAILY -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }
            RecurringFrequency.WEEKLY -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.timeInMillis
            }
            RecurringFrequency.MONTHLY -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.timeInMillis
            }
            RecurringFrequency.YEARLY -> {
                calendar.add(Calendar.YEAR, 1)
                calendar.timeInMillis
            }
        }
    }
    
    suspend fun createRecurringTransaction(
        name: String,
        amount: Long,
        categoryId: Long,
        accountId: Long,
        type: TransactionType,
        frequency: RecurringFrequency,
        startDate: Long,
        endDate: Long? = null,
        note: String? = null
    ): Long {
        val nextExecution = calculateNextExecution(frequency, startDate)
        
        val recurringTransaction = RecurringTransactionEntity(
            name = name,
            amount = amount,
            categoryId = categoryId,
            accountId = accountId,
            type = type,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            nextExecution = nextExecution,
            note = note
        )
        
        return recurringTransactionDao.insertRecurringTransaction(recurringTransaction)
    }
    
    suspend fun getActiveRecurringTransactions() = 
        recurringTransactionDao.getActiveRecurringTransactions().first()
    
    suspend fun getAllRecurringTransactions() = 
        recurringTransactionDao.getAllRecurringTransactions().first()
    
    suspend fun deactivateRecurringTransaction(id: Long) {
        recurringTransactionDao.deactivateRecurringTransaction(id)
    }
    
    suspend fun deleteRecurringTransaction(id: Long) {
        val recurringTransaction = recurringTransactionDao.getRecurringTransactionById(id)
        recurringTransaction?.let {
            recurringTransactionDao.deleteRecurringTransaction(it)
        }
    }
}
