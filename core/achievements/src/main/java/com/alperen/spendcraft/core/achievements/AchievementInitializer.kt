package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory

object AchievementInitializer {
    /**
     * iOS AchievementsViewModel.swift:36-63 ile birebir aynı achievement'lar
     * SF Symbol icon'lar kullanılıyor (drawable reference olarak)
     */
    fun getDefaultAchievements(): List<AchievementEntity> {
        return listOf(
            // iOS: "İlk Adım" - checkmark.circle.fill
            AchievementEntity(
                name = "İlk Adım",
                description = "İlk işleminizi kaydedin",
                icon = "ic_checkmark_circle_fill",
                points = 10,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            // iOS: "Başlangıç" - flame.fill (5 işlem)
            AchievementEntity(
                name = "Başlangıç",
                description = "5 işlem kaydedin",
                icon = "ic_flame_fill",
                points = 25,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 5
            ),
            // iOS: "Düzenli" - star.fill (10 işlem)
            AchievementEntity(
                name = "Düzenli",
                description = "10 işlem kaydedin",
                icon = "ic_star_fill",
                points = 50,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 10
            ),
            // iOS: "Uzman" - crown.fill (50 işlem)
            AchievementEntity(
                name = "Uzman",
                description = "50 işlem kaydedin",
                icon = "ic_crown_fill",
                points = 100,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 50
            ),
            // iOS: "Kategori Ustası" - folder.badge.plus
            AchievementEntity(
                name = "Kategori Ustası",
                description = "5 farklı kategori kullanın",
                icon = "ic_folder_badge_plus",
                points = 30,
                category = AchievementCategory.CATEGORY,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 5
            ),
            // iOS: "Bütçe Bilinci" - chart.bar.fill
            AchievementEntity(
                name = "Bütçe Bilinci",
                description = "İlk bütçenizi oluşturun",
                icon = "ic_chart_bar_fill",
                points = 20,
                category = AchievementCategory.BUDGET,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            // iOS: "Tutumlu" - shield.fill
            AchievementEntity(
                name = "Tutumlu",
                description = "Aylık bütçenize uyun",
                icon = "ic_shield_fill",
                points = 75,
                category = AchievementCategory.BUDGET,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            // iOS: "Yatırımcı" - banknote.fill
            AchievementEntity(
                name = "Yatırımcı",
                description = "İlk gelirinizi kaydedin",
                icon = "ic_banknote",
                points = 15,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            // BONUS: Android-specific extras (iOS'ta yok, ama güzel olabilir)
            AchievementEntity(
                name = "Tasarruf Ustası",
                description = "Toplam 1000 TL tasarruf edin",
                icon = "ic_piggybank_fill",
                points = 50,
                category = AchievementCategory.SAVING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1000
            ),
            AchievementEntity(
                name = "Süper İzleyici",
                description = "100 işlem kaydedin",
                icon = "ic_binoculars_fill",
                points = 150,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 100
            )
        )
    }
}