package com.alperen.spendcraft.shared.presentation

import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.calculator.StreakCalculator
import com.alperen.spendcraft.shared.domain.rules.AchievementRules
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Shared achievement business logic
 * iOS/Android pattern: Encapsulates achievement-related business rules
 */
class SharedAchievementBusinessLogic {
    
    /**
     * Check first transaction achievement
     */
    fun checkFirstTransactionAchievement(transactionCount: Int): Achievement {
        val isUnlocked = AchievementRules.checkFirstTransaction(transactionCount)
        return Achievement(
            id = "first_transaction",
            title = "İlk İşlem",
            description = "İlk işlemini kaydet",
            icon = "first_transaction_icon",
            points = 10,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check daily streak achievement
     */
    fun checkDailyStreakAchievement(lastLoggedDate: Instant, today: Instant): Achievement {
        val isUnlocked = AchievementRules.checkDailyStreak(lastLoggedDate, today)
        return Achievement(
            id = "daily_streak",
            title = "Günlük Seri",
            description = "Uygulamayı 3 gün üst üste kullan",
            icon = "daily_streak_icon",
            points = 15,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check budget master achievement
     */
    fun checkBudgetMasterAchievement(budgetCount: Int): Achievement {
        val isUnlocked = AchievementRules.checkBudgetMaster(budgetCount)
        return Achievement(
            id = "budget_master",
            title = "Bütçe Ustası",
            description = "5 bütçe oluştur",
            icon = "budget_master_icon",
            points = 20,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check category explorer achievement
     */
    fun checkCategoryExplorerAchievement(categoryCount: Int): Achievement {
        val isUnlocked = AchievementRules.checkCategoryExplorer(categoryCount)
        return Achievement(
            id = "category_explorer",
            title = "Kategori Kaşifi",
            description = "10 farklı kategori kullan",
            icon = "category_explorer_icon",
            points = 15,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check high roller achievement
     */
    fun checkHighRollerAchievement(totalSpent: Long): Achievement {
        val isUnlocked = AchievementRules.checkHighRoller(totalSpent)
        return Achievement(
            id = "high_roller",
            title = "Yüksek Harcama",
            description = "Toplam 10.000 TL harca",
            icon = "high_roller_icon",
            points = 25,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check savings champion achievement
     */
    fun checkSavingsChampionAchievement(totalSaved: Long): Achievement {
        val isUnlocked = AchievementRules.checkSavingsChampion(totalSaved)
        return Achievement(
            id = "savings_champion",
            title = "Tasarruf Şampiyonu",
            description = "Toplam 5.000 TL tasarruf et",
            icon = "savings_champion_icon",
            points = 30,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check no spend day achievement
     */
    fun checkNoSpendDayAchievement(noSpendDays: Int): Achievement {
        val isUnlocked = AchievementRules.checkNoSpendDay(noSpendDays)
        return Achievement(
            id = "no_spend_day",
            title = "Harcamasız Gün",
            description = "Bir ayda 5 harcamasız gün geçir",
            icon = "no_spend_day_icon",
            points = 20,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check early bird achievement
     */
    fun checkEarlyBirdAchievement(transaction: Transaction): Achievement {
        val isUnlocked = AchievementRules.checkEarlyBird(transaction)
        return Achievement(
            id = "early_bird",
            title = "Erken Kalkan",
            description = "Sabah 8'den önce ilk işlemini yap",
            icon = "early_bird_icon",
            points = 10,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check night owl achievement
     */
    fun checkNightOwlAchievement(transaction: Transaction): Achievement {
        val isUnlocked = AchievementRules.checkNightOwl(transaction)
        return Achievement(
            id = "night_owl",
            title = "Gece Kuşu",
            description = "Gece yarısından sonra işlem yap",
            icon = "night_owl_icon",
            points = 10,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Check referral master achievement
     */
    fun checkReferralMasterAchievement(referralCount: Int): Achievement {
        val isUnlocked = AchievementRules.checkReferralMaster(referralCount)
        return Achievement(
            id = "referral_master",
            title = "Referans Ustası",
            description = "3 arkadaşını davet et",
            icon = "referral_master_icon",
            points = 20,
            isUnlocked = isUnlocked
        )
    }
    
    /**
     * Calculate current streak
     */
    fun calculateCurrentStreak(dailyEntries: List<Instant>, today: Instant): Int {
        return StreakCalculator.calculateCurrentStreak(dailyEntries, today)
    }
    
    /**
     * Calculate best streak
     */
    fun calculateBestStreak(dailyEntries: List<Instant>): Int {
        return StreakCalculator.calculateBestStreak(dailyEntries)
    }
    
    /**
     * Get unlocked achievements count
     */
    fun getUnlockedAchievementsCount(achievements: List<Achievement>): Int {
        return achievements.count { it.isUnlocked }
    }
}