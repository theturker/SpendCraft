package com.alperen.spendcraft.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alperen.spendcraft.data.db.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TxDao {
    @Query("SELECT * FROM transactions ORDER BY timestampUtcMillis DESC")
    fun observeAll(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY timestampUtcMillis DESC")
    fun observeByAccount(accountId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransactionEntity): Long

    @Update
    suspend fun update(entity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}




