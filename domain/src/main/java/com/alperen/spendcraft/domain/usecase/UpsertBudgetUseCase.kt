package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.achievements.AchievementManager
import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.domain.repo.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpsertBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository,
    private val achievementManager: AchievementManager
) {
    suspend operator fun invoke(budget: Budget) {
        // Bütçeyi kaydet
        repository.upsertBudget(budget)
        
        // Achievement'ları kontrol et (background thread'de)
        withContext(Dispatchers.IO) {
            try {
                achievementManager.checkAchievements()
            } catch (e: Exception) {
                // Achievement kontrolünde hata olursa uygulama çökmesin
            }
        }
    }
}

