package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.ai.AIKeyManager
import com.alperen.spendcraft.core.ai.GroqClient
import com.alperen.spendcraft.core.ai.GroqMessage
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import com.alperen.spendcraft.data.db.dao.AIUsageDao
import com.alperen.spendcraft.data.db.entities.AIUsageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val groqClient: GroqClient,
    private val aiUsageDao: AIUsageDao,
    private val billingRepository: BillingRepository,
    private val premiumStateDataStore: PremiumStateDataStore,
    private val aiKeyManager: AIKeyManager
) {
    data class AIUsageInfo(
        val weeklyQuota: Int,
        val usedThisWeek: Int
    )
    
    suspend fun canUseAI(): Boolean {
        val isPremium = premiumStateDataStore.isPremium.first()
        if (isPremium) return true
        
        // Ücretsiz kullanıcılar haftada 2 hakka sahiptir; satın alma gerektirmez
        val usage = aiUsageDao.getUsageByUserId("local")
        if (usage == null) return true // First time user (initial quota will be created on first record)
        
        val now = System.currentTimeMillis()
        val weekInMillis = 7 * 24 * 60 * 60 * 1000L
        val lastUsedWeek = usage.lastUsedEpoch / weekInMillis
        val currentWeek = now / weekInMillis
        
        return currentWeek > lastUsedWeek || usage.usedThisWeek < usage.weeklyQuota
    }
    
    suspend fun generateAdvice(
        prompt: String
    ): Result<String> {
        if (!canUseAI()) {
            return Result.failure(Exception("AI kullanım hakkınız yok. Premium'a geçin veya haftalık AI paketi satın alın."))
        }
        
        val apiKey = aiKeyManager.getGroqApiKey()
            ?: return Result.failure(Exception("AI API anahtarı bulunamadı. Lütfen ayarlardan API anahtarını girin."))
        
        val messages = listOf(
            GroqMessage(role = "system", content = "Sen bir kişisel finans uzmanısın. Türkçe olarak kısa, net ve uygulanabilir finansal öneriler ver."),
            GroqMessage(role = "user", content = prompt)
        )
        val result = groqClient.generateAdvice(apiKey, messages)
        
        if (result.isSuccess) {
            // Record usage
            recordAIUsage()
        }
        
        return result
    }
    
    suspend fun generateSpendingAdvice(
        categoryBreakdown: Map<String, Long>,
        totalExpense: Long
    ): Result<String> {
        if (!canUseAI()) {
            return Result.failure(Exception("AI kullanım hakkınız yok. Premium'a geçin veya haftalık AI paketi satın alın."))
        }
        
        val apiKey = aiKeyManager.getGroqApiKey()
            ?: return Result.failure(Exception("AI API anahtarı bulunamadı. Lütfen ayarlardan API anahtarını girin."))
        
        val result = groqClient.generateSpendingAdvice(categoryBreakdown, totalExpense, apiKey)
        
        if (result.isSuccess) {
            recordAIUsage()
        }
        
        return result
    }
    
    suspend fun generateBudgetAdvice(
        income: Long,
        expenses: Long,
        savings: Long
    ): Result<String> {
        if (!canUseAI()) {
            return Result.failure(Exception("AI kullanım hakkınız yok. Premium'a geçin veya haftalık AI paketi satın alın."))
        }
        
        val apiKey = aiKeyManager.getGroqApiKey()
            ?: return Result.failure(Exception("AI API anahtarı bulunamadı. Lütfen ayarlardan API anahtarını girin."))
        
        val result = groqClient.generateBudgetAdvice(income, expenses, savings, apiKey)
        
        if (result.isSuccess) {
            recordAIUsage()
        }
        
        return result
    }
    
    private suspend fun recordAIUsage() {
        val now = System.currentTimeMillis()
        val weekInMillis = 7 * 24 * 60 * 60 * 1000L
        val currentWeek = now / weekInMillis
        
        val usage = aiUsageDao.getUsageByUserId("local")
        if (usage == null) {
            // First time usage
            aiUsageDao.insertUsage(
                AIUsageEntity(
                    userId = "local",
                    lastUsedEpoch = now,
                    weeklyQuota = 2,
                    usedThisWeek = 1
                )
            )
        } else {
            val lastUsedWeek = usage.lastUsedEpoch / weekInMillis
            if (currentWeek > lastUsedWeek) {
                // New week, reset counter
                aiUsageDao.updateUsage(
                    usage.copy(
                        lastUsedEpoch = now,
                        usedThisWeek = 1
                    )
                )
            } else {
                // Same week, increment counter
                aiUsageDao.recordUsage("local", now)
            }
        }
    }
    
    suspend fun getAIUsage(): Flow<AIUsageEntity?> {
        return aiUsageDao.getUsageByUserIdFlow("local")
    }

    fun getUsageInfo(): Flow<AIUsageInfo> {
        return aiUsageDao.getUsageByUserIdFlow("local").map { usage ->
            AIUsageInfo(
                weeklyQuota = usage?.weeklyQuota ?: 2,
                usedThisWeek = usage?.usedThisWeek ?: 0
            )
        }
    }
    
    suspend fun resetWeeklyUsage() {
        aiUsageDao.resetWeeklyUsage("local")
    }
    
    suspend fun updateWeeklyQuota(quota: Int) {
        aiUsageDao.updateWeeklyQuota("local", quota)
    }
}
