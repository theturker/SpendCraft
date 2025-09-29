package com.alperen.spendcraft.domain.achievements

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Achievement yönetimi için interface.
 * Implementation Android modülünde yapılacak.
 */
interface AchievementManager {
    val allAchievements: Flow<List<Any>> // AchievementEntity yerine Any kullanıyoruz JVM uyumluluğu için
    
    suspend fun initializeAchievements()
    suspend fun checkAchievements()
    suspend fun incrementAchievement(achievementName: String, progress: Int)
}
