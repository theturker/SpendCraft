package com.alperen.spendcraft.shared.domain.util

import kotlinx.coroutines.flow.Flow

/**
 * Achievement yönetimi için interface.
 * Platform-specific implementations:
 * - Android: Hilt ile DI
 * - iOS: Swift ile implement edilecek
 */
interface AchievementManager {
    val allAchievements: Flow<List<Any>> // Platform-specific achievement entities
    
    suspend fun initializeAchievements()
    suspend fun checkAchievements()
    suspend fun incrementAchievement(achievementName: String, progress: Int)
}


