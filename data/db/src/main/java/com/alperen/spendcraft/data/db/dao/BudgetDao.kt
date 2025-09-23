package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budget")
    fun observeAll(): Flow<List<BudgetEntity>>
    
    @Query("SELECT * FROM budget")
    suspend fun getAllAscending(): List<BudgetEntity>
    
    @Query("SELECT * FROM budget WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: String): BudgetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: BudgetEntity)
    
    @Delete
    suspend fun delete(budget: BudgetEntity)
    
    @Query("DELETE FROM budget WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: String)
}
