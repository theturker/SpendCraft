package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.AIUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIUsageDao {
    
    @Query("SELECT * FROM ai_usage WHERE userId = :userId")
    suspend fun getUsageByUserId(userId: String): AIUsageEntity?
    
    @Query("SELECT * FROM ai_usage WHERE userId = :userId")
    fun getUsageByUserIdFlow(userId: String): Flow<AIUsageEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: AIUsageEntity): Long
    
    @Update
    suspend fun updateUsage(usage: AIUsageEntity)
    
    @Query("UPDATE ai_usage SET lastUsedEpoch = :timestamp, usedThisWeek = usedThisWeek + 1 WHERE userId = :userId")
    suspend fun recordUsage(userId: String, timestamp: Long)
    
    @Query("UPDATE ai_usage SET usedThisWeek = 0 WHERE userId = :userId")
    suspend fun resetWeeklyUsage(userId: String)
    
    @Query("UPDATE ai_usage SET weeklyQuota = :quota WHERE userId = :userId")
    suspend fun updateWeeklyQuota(userId: String, quota: Int)
}
