package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.BudgetAlertEntity

@Dao
interface BudgetAlertDao {
    
    @Query("SELECT COUNT(*) FROM budget_alert WHERE categoryId = :categoryId AND level = :level AND monthKey = :monthKey")
    suspend fun alertExists(categoryId: String, level: Int, monthKey: String): Int
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alert: BudgetAlertEntity)
    
    @Query("DELETE FROM budget_alert WHERE monthKey < :oldestMonthKey")
    suspend fun deleteOldAlerts(oldestMonthKey: String)
}

