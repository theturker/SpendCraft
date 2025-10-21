package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Budget
import com.alperen.spendcraft.shared.domain.repository.BudgetRepository
import com.alperen.spendcraft.shared.domain.util.AchievementManager

class UpsertBudgetUseCase(
    private val repository: BudgetRepository,
    private val achievementManager: AchievementManager? = null // Optional for platforms without achievements
) {
    suspend operator fun invoke(budget: Budget) {
        // Bütçeyi kaydet
        repository.upsertBudget(budget)
        
        // Achievement'ları kontrol et (optional, platform-specific)
        try {
            achievementManager?.checkAchievements()
        } catch (e: Exception) {
            // Achievement kontrolünde hata olursa uygulama çökmesin
        }
    }
}


