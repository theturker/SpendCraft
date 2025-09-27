package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements ORDER BY points DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE category = :category")
    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: Long): AchievementEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity): Long
    
    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)
    
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: Long, timestamp: Long)
    
    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int
    
    @Query("SELECT SUM(points) FROM achievements WHERE isUnlocked = 1")
    suspend fun getTotalPoints(): Int
    
    @Query("SELECT * FROM achievements WHERE name = :name")
    suspend fun getAchievementByName(name: String): AchievementEntity?
    
    @Query("UPDATE achievements SET progress = :progress, isUnlocked = :isUnlocked, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun updateAchievement(id: Long, progress: Int, isUnlocked: Boolean, unlockedAt: Long?)
}
