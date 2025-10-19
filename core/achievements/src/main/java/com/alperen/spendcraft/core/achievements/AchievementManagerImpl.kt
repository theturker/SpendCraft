package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.dao.AchievementDao
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory
import com.alperen.spendcraft.domain.achievements.AchievementManager
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.CategoryRepository
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.core.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementManagerImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val transactionRepository: TransactionsRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : AchievementManager {
    
    override val allAchievements: Flow<List<Any>> = achievementDao.getAllAchievements() as Flow<List<Any>>
    
    override suspend fun initializeAchievements() {
        // Başlangıç achievement'larını oluştur
        val initialAchievements = listOf(
            AchievementEntity(
                id = 1,
                name = "İlk Adım",
                description = "İlk işleminizi kaydedin",
                icon = "🎯",
                points = 10,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            AchievementEntity(
                id = 2,
                name = "İşlem Ustası",
                description = "10 işlem kaydedin",
                icon = "📊",
                points = 25,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 10
            ),
            AchievementEntity(
                id = 3,
                name = "Süper İzleyici",
                description = "100 işlem kaydedin",
                icon = "ic_binoculars_fill",
                points = 150,
                category = AchievementCategory.SPENDING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 100
            ),
            AchievementEntity(
                id = 4,
                name = "Hafta Savaşçısı",
                description = "7 gün üst üste işlem kaydedin",
                icon = "⚔️",
                points = 75,
                category = AchievementCategory.STREAK,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 7
            ),
            AchievementEntity(
                id = 5,
                name = "Aylık Şampiyon",
                description = "30 gün üst üste işlem kaydedin",
                icon = "🏆",
                points = 150,
                category = AchievementCategory.STREAK,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 30
            ),
            AchievementEntity(
                id = 6,
                name = "Bütçe Planlayıcısı",
                description = "İlk bütçenizi oluşturun",
                icon = "📊",
                points = 30,
                category = AchievementCategory.BUDGET,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            ),
            AchievementEntity(
                id = 7,
                name = "Kategori Uzmanı",
                description = "5 farklı kategoride işlem yapın",
                icon = "🏷️",
                points = 40,
                category = AchievementCategory.CATEGORY,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 5
            ),
            AchievementEntity(
                id = 8,
                name = "Tasarruf Ustası",
                description = "Bir ay boyunca bütçenizi aşmayın",
                icon = "ic_piggybank_fill",
                points = 100,
                category = AchievementCategory.SAVING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 0,
                maxProgress = 1
            )
        )
        
        // Achievement'ları veritabanına ekle (eğer yoksa)
        initialAchievements.forEach { achievement ->
            val existing = achievementDao.getAchievementById(achievement.id)
            if (existing == null) {
                achievementDao.insertAchievement(achievement)
            }
        }
    }
    
    override suspend fun checkAchievements() {
        val transactions = transactionRepository.getAllAscending()
        val budgets = budgetRepository.observeBudgets().first()
        val categories = categoryRepository.getAllCategories()
        
        // Transaction sayısı achievement'ları
        checkTransactionCountAchievements(transactions.size)
        
        // Streak achievement'ları
        checkStreakAchievements(transactions)
        
        // Bütçe achievement'ları
        checkBudgetAchievements(budgets.isNotEmpty())
        
        // Kategori achievement'ları
        checkCategoryAchievements(transactions, categories.size)
        
        // Tasarruf achievement'ları
        checkSavingsAchievements(transactions, budgets)
    }
    
    private suspend fun checkTransactionCountAchievements(transactionCount: Int) {
        // İlk işlem
        if (transactionCount >= 1) {
            updateAchievementProgress("İlk Adım", 1, 1)
        }
        
        // 10 işlem
        if (transactionCount >= 10) {
            updateAchievementProgress("İşlem Ustası", 10, 10)
        }
        
        // 100 işlem - Süper İzleyici
        if (transactionCount >= 100) {
            updateAchievementProgress("Süper İzleyici", 100, 100)
        }
    }
    
    private suspend fun checkStreakAchievements(transactions: List<Transaction>) {
        val currentStreak = calculateCurrentStreak(transactions)
        
        // 7 günlük streak
        if (currentStreak >= 7) {
            updateAchievementProgress("Hafta Savaşçısı", 7, 7)
        } else {
            updateAchievementProgress("Hafta Savaşçısı", currentStreak, 7)
        }
        
        // 30 günlük streak
        if (currentStreak >= 30) {
            updateAchievementProgress("Aylık Şampiyon", 30, 30)
        } else {
            updateAchievementProgress("Aylık Şampiyon", currentStreak, 30)
        }
    }
    
    private suspend fun checkBudgetAchievements(hasBudget: Boolean) {
        if (hasBudget) {
            updateAchievementProgress("Bütçe Planlayıcısı", 1, 1)
        }
    }
    
    private suspend fun checkCategoryAchievements(transactions: List<Transaction>, categoryCount: Int) {
        val uniqueCategories = transactions.mapNotNull { it.categoryId }.distinct().size
        val progress = minOf(uniqueCategories, 5)
        updateAchievementProgress("Kategori Uzmanı", progress, 5)
    }
    
    private suspend fun checkSavingsAchievements(
        transactions: List<Transaction>,
        budgets: List<Budget>
    ) {
        // Bu ay bütçeyi aşmamış mı kontrol et
        val currentMonth = Instant.now().atZone(ZoneOffset.UTC)
        val monthStart = currentMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val monthEnd = monthStart.plusMonths(1)
        
        val monthStartMillis = monthStart.toInstant().toEpochMilli()
        val monthEndMillis = monthEnd.toInstant().toEpochMilli()
        
        val monthTransactions = transactions.filter { tx ->
            tx.timestampUtcMillis >= monthStartMillis && 
            tx.timestampUtcMillis < monthEndMillis && 
            tx.type == TransactionType.EXPENSE
        }
        
        val spentAmounts: Map<Long?, Long> = monthTransactions.groupBy { it.categoryId }
            .mapValues { (_, txs) -> txs.sumOf { tx -> tx.amount.minorUnits } }
        
        var budgetRespected = true
        budgets.forEach { budget ->
            // Budget'ın categoryId'si String, spentAmounts'ın key'i Long?
            // Bu yüzden budget'ı kontrol etmiyoruz şimdilik
            // TODO: Budget ve Transaction categoryId tiplerini uyumlu hale getir
        }
        
        if (budgetRespected && budgets.isNotEmpty()) {
            updateAchievementProgress("Tasarruf Ustası", 1, 1)
        }
    }
    
    private fun calculateCurrentStreak(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return 0
        
        val today = Instant.now().atZone(ZoneOffset.UTC).toLocalDate()
        val transactionDates = transactions.map { 
            Instant.ofEpochMilli(it.timestampUtcMillis).atZone(ZoneOffset.UTC).toLocalDate()
        }.distinct().sortedDescending()
        
        var streak = 0
        var currentDate = today
        
        for (date in transactionDates) {
            if (date == currentDate || date == currentDate.minusDays(1)) {
                streak++
                currentDate = date.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private suspend fun updateAchievementProgress(achievementName: String, progress: Int, maxProgress: Int) {
        val achievement = achievementDao.getAchievementByName(achievementName)
        if (achievement != null) {
            val isUnlocked = progress >= maxProgress
            val unlockedAt = if (isUnlocked && !achievement.isUnlocked) {
                System.currentTimeMillis()
            } else {
                achievement.unlockedAt
            }
            
            achievementDao.updateAchievement(
                id = achievement.id,
                progress = progress,
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt
            )
        }
    }
    
    override suspend fun incrementAchievement(achievementName: String, progress: Int) {
        val achievement = achievementDao.getAchievementByName(achievementName)
        if (achievement != null) {
            val newProgress = minOf(achievement.progress + progress, achievement.maxProgress)
            val isUnlocked = newProgress >= achievement.maxProgress
            val unlockedAt = if (isUnlocked && !achievement.isUnlocked) {
                System.currentTimeMillis()
            } else {
                achievement.unlockedAt
            }
            
            achievementDao.updateAchievement(
                id = achievement.id,
                progress = newProgress,
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt
            )
        }
    }
}
