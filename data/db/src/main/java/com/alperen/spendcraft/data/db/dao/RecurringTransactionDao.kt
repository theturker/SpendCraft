package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {
    
    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY nextExecution ASC")
    fun getActiveRecurringTransactions(): Flow<List<RecurringTransactionEntity>>
    
    @Query("SELECT * FROM recurring_transactions ORDER BY name ASC")
    fun getAllRecurringTransactions(): Flow<List<RecurringTransactionEntity>>
    
    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getRecurringTransactionById(id: Long): RecurringTransactionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringTransaction(recurringTransaction: RecurringTransactionEntity): Long
    
    @Update
    suspend fun updateRecurringTransaction(recurringTransaction: RecurringTransactionEntity)
    
    @Query("UPDATE recurring_transactions SET isActive = 0 WHERE id = :id")
    suspend fun deactivateRecurringTransaction(id: Long)
    
    @Query("UPDATE recurring_transactions SET lastExecuted = :timestamp, nextExecution = :nextExecution WHERE id = :id")
    suspend fun updateExecutionTime(id: Long, timestamp: Long, nextExecution: Long)
    
    @Query("SELECT * FROM recurring_transactions WHERE nextExecution <= :currentTime AND isActive = 1")
    suspend fun getDueRecurringTransactions(currentTime: Long): List<RecurringTransactionEntity>
    
    @Delete
    suspend fun deleteRecurringTransaction(recurringTransaction: RecurringTransactionEntity)
}
