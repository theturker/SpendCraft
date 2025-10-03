package com.alperen.spendcraft.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alperen.spendcraft.data.db.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY isDefault DESC, id ASC")
    fun observeAll(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAccount(): AccountEntity?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: AccountEntity): Long
    
    @Update
    suspend fun update(account: AccountEntity)
    
    @Query("DELETE FROM accounts WHERE id = :id AND isDefault = 0")
    suspend fun deleteById(id: Long)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Query("UPDATE accounts SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE accounts SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)

    @Query("UPDATE accounts SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)
}
