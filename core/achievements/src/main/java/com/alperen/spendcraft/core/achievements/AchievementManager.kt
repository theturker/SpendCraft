package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.dao.AchievementDao
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementManager @Inject constructor(
    private val achievementDao: AchievementDao
) {
    
    val allAchievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    
    suspend fun initializeAchievements() {
        // TODO: Başlangıç achievement'larını yükle
    }
    
    suspend fun checkAchievements() {
        // TODO: Gerçek veri entegrasyonu için mock data kullanıyoruz
        // Gelecekte transactionRepository, budgetRepository, categoryRepository kullanılacak
    }
    
    suspend fun incrementAchievement(achievementName: String, progress: Int) {
        // TODO: Achievement progress'ini güncelle
    }
}