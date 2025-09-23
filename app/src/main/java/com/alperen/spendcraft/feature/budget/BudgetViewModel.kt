package com.alperen.spendcraft.feature.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.domain.usecase.ObserveBudgetsUseCase
import com.alperen.spendcraft.domain.usecase.UpsertBudgetUseCase
import com.alperen.spendcraft.domain.usecase.DeleteBudgetUseCase
import com.alperen.spendcraft.domain.usecase.ObserveCategoriesUseCase
import com.alperen.spendcraft.domain.usecase.CheckBudgetBreachesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val observeBudgetsUseCase: ObserveBudgetsUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val checkBudgetBreachesUseCase: CheckBudgetBreachesUseCase
) : ViewModel() {

    val budgets: StateFlow<List<Budget>> = observeBudgetsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<Category>> = observeCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _budgetBreaches = MutableStateFlow<List<String>>(emptyList())
    val budgetBreaches: StateFlow<List<String>> = _budgetBreaches.asStateFlow()

    private val _spentAmounts = MutableStateFlow<Map<String, Long>>(emptyMap())
    val spentAmounts: StateFlow<Map<String, Long>> = _spentAmounts.asStateFlow()

    init {
        // Check for budget breaches periodically
        viewModelScope.launch {
            checkBudgetBreaches()
        }
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                upsertBudgetUseCase(budget)
                checkBudgetBreaches()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                upsertBudgetUseCase(budget)
                checkBudgetBreaches()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteBudget(categoryId: String) {
        viewModelScope.launch {
            try {
                deleteBudgetUseCase(categoryId)
                checkBudgetBreaches()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun checkBudgetBreaches() {
        try {
            val breaches = checkBudgetBreachesUseCase()
            _budgetBreaches.value = breaches
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun refreshBudgetBreaches() {
        viewModelScope.launch {
            checkBudgetBreaches()
        }
    }

    fun calculateSpentAmounts() {
        viewModelScope.launch {
            try {
                // TODO: Implement spent amounts calculation
                // This should calculate spent amounts for each budget category for current month
                _spentAmounts.value = emptyMap()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
