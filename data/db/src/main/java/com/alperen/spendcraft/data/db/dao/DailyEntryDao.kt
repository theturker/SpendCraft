package com.alperen.spendcraft.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alperen.spendcraft.data.db.entities.DailyEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {
    
    @Query("SELECT * FROM daily_entry ORDER BY dateEpochDay DESC")
    fun observeAll(): Flow<List<DailyEntryEntity>>
    
    @Query("SELECT * FROM daily_entry ORDER BY dateEpochDay ASC")
    suspend fun getAllAscending(): List<DailyEntryEntity>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: DailyEntryEntity)
    
    @Query("SELECT COUNT(*) FROM daily_entry WHERE dateEpochDay = :dateEpochDay")
    suspend fun existsForDay(dateEpochDay: Int): Int
}

