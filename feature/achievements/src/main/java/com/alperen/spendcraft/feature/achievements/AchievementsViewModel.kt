package com.alperen.spendcraft.feature.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.domain.achievements.AchievementManager
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementManager: AchievementManager,
    private val transactionRepository: TransactionsRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val premiumStateDataStore: com.alperen.spendcraft.core.premium.PremiumStateDataStore
) : ViewModel() {
    
    private val _achievements = MutableStateFlow<List<AchievementEntity>>(emptyList())
    val achievements: StateFlow<List<AchievementEntity>> = _achievements.asStateFlow()

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _unlockedCount = MutableStateFlow(0)
    val unlockedCount: StateFlow<Int> = _unlockedCount.asStateFlow()

    private val _rewardProgressPercent = MutableStateFlow(0)
    val rewardProgressPercent: StateFlow<Int> = _rewardProgressPercent.asStateFlow()

    private val _rewardXpCurrent = MutableStateFlow(0)
    val rewardXpCurrent: StateFlow<Int> = _rewardXpCurrent.asStateFlow()

    private val _rewardXpTarget = MutableStateFlow(0)
    val rewardXpTarget: StateFlow<Int> = _rewardXpTarget.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            // İlk başta achievement'ları initialize et
            achievementManager.initializeAchievements()
            
            // Başarımları kontrol et ve güncelle
            achievementManager.checkAchievements()
            
            // AchievementManager'dan güncel başarımları al
            achievementManager.allAchievements.collect { achievements ->
                val achievementEntities = achievements.filterIsInstance<AchievementEntity>()
                _achievements.value = achievementEntities
                val unlockedPoints = achievementEntities.filter { it.isUnlocked }.sumOf { it.points }
                val totalPts = achievementEntities.sumOf { it.points }
                _totalPoints.value = unlockedPoints
                _unlockedCount.value = achievementEntities.count { it.isUnlocked }

                // Ödül ilerlemesi (% ve XP): tüm başarımlar tamamlanınca ödül
                _rewardXpCurrent.value = unlockedPoints
                _rewardXpTarget.value = if (totalPts > 0) totalPts else 100
                _rewardProgressPercent.value = if (totalPts > 0) ((unlockedPoints * 100) / totalPts).coerceIn(0, 100) else 0

                // Ödül: Premium değilken tüm başarımlar %100 tamamlandıysa 30 gün premium ver
                val total = achievementEntities.size
                val unlocked = achievementEntities.count { it.isUnlocked }
                if (total > 0 && unlocked == total) {
                    // Kullanıcı premium değilse ver
                    viewModelScope.launch {
                        val isPremiumNow = premiumStateDataStore.isPremium.first()
                        if (!isPremiumNow) {
                            premiumStateDataStore.grantPremiumForDays(30)
                        }
                    }
                }
            }
        }
    }
    
    
    private fun loadMockAchievements() {
        val mockAchievements = listOf(
            AchievementEntity(
                id = 1,
                name = "İlk Adım",
                description = "İlk işleminizi kaydedin",
                icon = "🎯",
                points = 10,
                category = com.alperen.spendcraft.data.db.entities.AchievementCategory.SPENDING,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 86400000,
                progress = 1,
                maxProgress = 1
            ),
            AchievementEntity(
                id = 2,
                name = "Hafta Savaşçısı",
                description = "7 gün üst üste işlem kaydedin",
                icon = "⚔️",
                points = 50,
                category = com.alperen.spendcraft.data.db.entities.AchievementCategory.STREAK,
                isUnlocked = false,
                unlockedAt = null,
                progress = 3,
                maxProgress = 7
            ),
            AchievementEntity(
                id = 3,
                name = "Bütçe Planlayıcısı",
                description = "İlk bütçenizi oluşturun",
                icon = "📊",
                points = 25,
                category = com.alperen.spendcraft.data.db.entities.AchievementCategory.BUDGET,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 172800000,
                progress = 1,
                maxProgress = 1
            ),
            AchievementEntity(
                id = 4,
                name = "Tasarruf Ustası",
                description = "Gelirinizden fazla harcamayın",
                icon = "💰",
                points = 30,
                category = com.alperen.spendcraft.data.db.entities.AchievementCategory.SAVING,
                isUnlocked = false,
                unlockedAt = null,
                progress = 500,
                maxProgress = 1000
            ),
            AchievementEntity(
                id = 5,
                name = "Kategori Uzmanı",
                description = "5 farklı kategoride işlem yapın",
                icon = "🏷️",
                points = 40,
                category = com.alperen.spendcraft.data.db.entities.AchievementCategory.CATEGORY,
                isUnlocked = false,
                unlockedAt = null,
                progress = 3,
                maxProgress = 5
            )
        )
        
        _achievements.value = mockAchievements
        _totalPoints.value = mockAchievements.filter { it.isUnlocked }.sumOf { it.points }
        _unlockedCount.value = mockAchievements.count { it.isUnlocked }
    }

    fun incrementAchievement(name: String, incrementBy: Int = 1) {
        viewModelScope.launch {
            try {
                achievementManager.incrementAchievement(name, incrementBy)
                loadAchievements() // Refresh after increment
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun checkAchievements() {
        viewModelScope.launch {
            try {
                achievementManager.checkAchievements()
                loadAchievements() // Refresh after checking
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}