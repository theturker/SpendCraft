package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {
    
    @Query("SELECT * FROM recurring_rules WHERE isActive = 1 ORDER BY nextRunEpoch ASC")
    fun getAllActiveRules(): Flow<List<RecurringRuleEntity>>
    
    @Query("SELECT * FROM recurring_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): RecurringRuleEntity?
    
    @Query("SELECT * FROM recurring_rules WHERE nextRunEpoch <= :currentTime AND isActive = 1")
    suspend fun getDueRules(currentTime: Long): List<RecurringRuleEntity>
    
    @Insert
    suspend fun insertRule(rule: RecurringRuleEntity): Long
    
    @Update
    suspend fun updateRule(rule: RecurringRuleEntity)
    
    @Query("UPDATE recurring_rules SET nextRunEpoch = :nextRun, isActive = :isActive WHERE id = :id")
    suspend fun updateNextRun(id: Long, nextRun: Long, isActive: Boolean = true)
    
    @Query("UPDATE recurring_rules SET isActive = 0 WHERE id = :id")
    suspend fun deactivateRule(id: Long)
    
    @Delete
    suspend fun deleteRule(rule: RecurringRuleEntity)
}
