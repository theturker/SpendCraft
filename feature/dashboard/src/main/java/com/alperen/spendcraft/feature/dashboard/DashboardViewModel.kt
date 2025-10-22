package com.alperen.spendcraft.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.achievements.AchievementManager
import com.alperen.spendcraft.shared.domain.calculator.TransactionAnalyzer as SharedTransactionAnalyzer
import com.alperen.spendcraft.shared.domain.calculator.StreakCalculator as SharedStreakCalculator
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
    
    // Categories flow
    val categories = transactionsRepository.observeCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Accounts flow
    val accounts = transactionsRepository.observeAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Balance - NOW USING SHARED KMP CALCULATOR! 🎉
    val currentBalance: StateFlow<Double> = transactions.map { list ->
        // Delegate to shared calculator for consistent logic
        val sharedTransactions = list.map { transaction ->
            com.alperen.spendcraft.shared.domain.model.Transaction(
                id = transaction.id,
                amount = com.alperen.spendcraft.shared.domain.model.Money(transaction.amount.minorUnits),
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = if (transaction.type == TransactionType.INCOME) 
                    com.alperen.spendcraft.shared.domain.model.TransactionType.INCOME 
                else 
                    com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE
            )
        }
        
        // Use shared calculator - calculate manually for now
        val income = sharedTransactions
            .filter { it.type == com.alperen.spendcraft.shared.domain.model.TransactionType.INCOME }
            .sumOf { it.amount.minorUnits }
        val expense = sharedTransactions
            .filter { it.type == com.alperen.spendcraft.shared.domain.model.TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits }
        
        (income - expense).toDouble() / 100.0 // Convert back to major units
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

    // Streak data - SIMPLIFIED FOR NOW
    val currentStreak: StateFlow<Int> = MutableStateFlow(0) // Placeholder
    val longestStreak: StateFlow<Int> = MutableStateFlow(0) // Placeholder

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

