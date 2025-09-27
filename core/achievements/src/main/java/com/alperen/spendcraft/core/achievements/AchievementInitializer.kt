package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory

object AchievementInitializer {
    fun getDefaultAchievements(): List<AchievementEntity> {
        return listOf(
            AchievementEntity(
                name = "İlk Adım",
                description = "İlk işlemini kaydet.",
                icon = "🎯",
                points = 10,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            AchievementEntity(
                name = "Hafta Savaşçısı",
                description = "Bir hafta boyunca her gün işlem kaydet.",
                icon = "⚔️",
                points = 50,
                category = AchievementCategory.STREAK,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 7
            ),
            AchievementEntity(
                name = "Bütçe Planlayıcısı",
                description = "İlk bütçeni oluştur.",
                icon = "📊",
                points = 25,
                category = AchievementCategory.BUDGET,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            AchievementEntity(
                name = "Tasarruf Ustası",
                description = "Toplam 1000 TL tasarruf et.",
                icon = "💰",
                points = 30,
                category = AchievementCategory.SAVING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1000
            ),
            AchievementEntity(
                name = "Kategori Uzmanı",
                description = "5 farklı kategoriye işlem kaydet.",
                icon = "🏷️",
                points = 40,
                category = AchievementCategory.CATEGORY,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 5
            ),
            AchievementEntity(
                name = "Günlük Kayıt",
                description = "Toplam 30 işlem kaydet.",
                icon = "📝",
                points = 20,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 30
            ),
            AchievementEntity(
                name = "Aylık Hedef",
                description = "Bir ayda 10 işlem kaydet.",
                icon = "📅",
                points = 35,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 10
            ),
            AchievementEntity(
                name = "Yıllık Planlayıcı",
                description = "Bir yıl içinde 100 işlem kaydet.",
                icon = "🗓️",
                points = 100,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 100
            ),
            AchievementEntity(
                name = "Süper Tasarrufçu",
                description = "Toplam 10.000 TL tasarruf et.",
                icon = "💎",
                points = 200,
                category = AchievementCategory.SAVING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 10000
            ),
            AchievementEntity(
                name = "Kategori Kralı",
                description = "10 farklı kategoriye işlem kaydet.",
                icon = "👑",
                points = 75,
                category = AchievementCategory.CATEGORY,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 10
            )
        )
    }
}