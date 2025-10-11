package com.alperen.spendcraft.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.achievements.AchievementManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Dashboard ViewModel - iOS DashboardView için gerekli data'yı sağlar
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val achievementManager: AchievementManager
) : ViewModel() {

    // Transactions flow
    val transactions: StateFlow<List<Transaction>> = transactionsRepository.observeTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Balance
    val currentBalance: StateFlow<Double> = transactions.map { list ->
        list.sumOf {
            if (it.type == TransactionType.INCOME) {
                it.amount.minorUnits / 100.0
            } else {
                -(it.amount.minorUnits / 100.0)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Total Income
    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.minorUnits / 100.0 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Total Expense
    val totalExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits / 100.0 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Streak data (placeholder - gerçek streak logic eklenecek)
    val currentStreak: StateFlow<Int> = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = MutableStateFlow(0)

    // Achievements data - gerçek veri ile çalışıyor (AchievementManager üzerinden)
    val achievements: StateFlow<List<com.alperen.spendcraft.data.db.entities.AchievementEntity>> = 
        achievementManager.allAchievements
            .map { list ->
                list.filterIsInstance<com.alperen.spendcraft.data.db.entities.AchievementEntity>()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    val achievementsCount: StateFlow<Int> = achievements.map { list ->
        list.count { it.isUnlocked }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    
    val totalPoints: StateFlow<Int> = achievements.map { list ->
        list.filter { it.isUnlocked }.sumOf { it.points.toInt() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        // Load initial data
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Initialize achievements
            achievementManager.initializeAchievements()
            // Check and update achievements
            achievementManager.checkAchievements()
            // TODO: Load streak data
        }
    }
}

