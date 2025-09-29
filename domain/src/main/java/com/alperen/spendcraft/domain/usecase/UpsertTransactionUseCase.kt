package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.achievements.AchievementManager
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repo: TransactionsRepository,
    private val achievementManager: AchievementManager
) {
    suspend operator fun invoke(tx: Transaction) {
        // Transaction'ı kaydet
        repo.upsert(tx)
        
        // Achievement'ları kontrol et (background thread'de)
        withContext(Dispatchers.IO) {
            try {
                achievementManager.checkAchievements()
            } catch (e: Exception) {
                // Achievement kontrolünde hata olursa uygulama çökmesin
                // Log e.printStackTrace()
            }
        }
    }
}




