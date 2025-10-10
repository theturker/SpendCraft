package com.alperen.spendcraft.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Dashboard ViewModel - iOS DashboardView için gerekli data'yı sağlar
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository
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

    // Achievements data (placeholder - gerçek achievements logic eklenecek)
    val achievementsCount: StateFlow<Int> = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = MutableStateFlow(0)

    init {
        // Load initial data
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // TODO: Load streak data
            // TODO: Load achievements data
        }
    }
}

