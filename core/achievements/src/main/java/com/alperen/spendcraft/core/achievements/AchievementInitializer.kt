package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.dao.AchievementDao
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementInitializer @Inject constructor(
    private val achievementDao: AchievementDao
) {
    
    suspend fun initializeDefaultAchievements() {
        val existingAchievements = achievementDao.getAllAchievements()
        if (existingAchievements.first().isEmpty()) {
            val defaultAchievements = listOf(
                AchievementEntity(
                    name = "İlk Adım",
                    description = "İlk işleminizi kaydedin",
                    icon = "🎯",
                    points = 10,
                    category = AchievementCategory.SPENDING,
                    maxProgress = 1
                ),
                AchievementEntity(
                    name = "Hafta Savaşçısı",
                    description = "7 gün üst üste işlem kaydedin",
                    icon = "⚔️",
                    points = 50,
                    category = AchievementCategory.STREAK,
                    maxProgress = 7
                ),
                AchievementEntity(
                    name = "Bütçe Planlayıcısı",
                    description = "İlk bütçenizi oluşturun",
                    icon = "📊",
                    points = 25,
                    category = AchievementCategory.BUDGET,
                    maxProgress = 1
                ),
                AchievementEntity(
                    name = "Tasarruf Ustası",
                    description = "Gelirinizden fazla harcamayın",
                    icon = "💰",
                    points = 30,
                    category = AchievementCategory.SAVING,
                    maxProgress = 1
                ),
                AchievementEntity(
                    name = "Kategori Uzmanı",
                    description = "5 farklı kategoride işlem yapın",
                    icon = "🏷️",
                    points = 40,
                    category = AchievementCategory.CATEGORY,
                    maxProgress = 5
                ),
                AchievementEntity(
                    name = "Günlük Kayıt",
                    description = "Bugün işlem kaydedin",
                    icon = "📝",
                    points = 5,
                    category = AchievementCategory.SPENDING,
                    maxProgress = 1
                ),
                AchievementEntity(
                    name = "Aylık Hedef",
                    description = "Bir ayda 30 işlem kaydedin",
                    icon = "📅",
                    points = 75,
                    category = AchievementCategory.SPENDING,
                    maxProgress = 30
                ),
                AchievementEntity(
                    name = "Yıllık Planlayıcı",
                    description = "Bir yılda 100 işlem kaydedin",
                    icon = "🗓️",
                    points = 100,
                    category = AchievementCategory.SPENDING,
                    maxProgress = 100
                ),
                AchievementEntity(
                    name = "Süper Tasarrufçu",
                    description = "10.000 TL tasarruf edin",
                    icon = "💎",
                    points = 200,
                    category = AchievementCategory.SAVING,
                    maxProgress = 10000
                ),
                AchievementEntity(
                    name = "Kategori Kralı",
                    description = "10 farklı kategoride işlem yapın",
                    icon = "👑",
                    points = 150,
                    category = AchievementCategory.CATEGORY,
                    maxProgress = 10
                )
            )
            
            defaultAchievements.forEach { achievement ->
                achievementDao.insertAchievement(achievement)
            }
        }
    }
}
