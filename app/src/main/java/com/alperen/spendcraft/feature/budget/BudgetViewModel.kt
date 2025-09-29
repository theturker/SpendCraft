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
import com.alperen.spendcraft.domain.usecase.GetSpentAmountsUseCase
import com.alperen.spendcraft.core.notifications.NotificationManager
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
    private val checkBudgetBreachesUseCase: CheckBudgetBreachesUseCase,
    private val getSpentAmountsUseCase: GetSpentAmountsUseCase,
    private val notificationManager: NotificationManager
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
            
            // Bütçe aşımı tespit edildiğinde bildirim gönder
            if (breaches.isNotEmpty()) {
                for (breach in breaches) {
                    if (breach.contains("Budget exceeded")) {
                        // 100% aşım bildirimi
                        val categoryName = breach.substringAfter("Budget exceeded for ").substringBefore("!")
                        notificationManager.showBudgetAlert("100%", categoryName)
                    } else if (breach.contains("Budget warning")) {
                        // 80% uyarı bildirimi
                        val categoryName = breach.substringAfter("Budget warning for ").substringBefore("!")
                        val percentage = breach.substringAfter("(").substringBefore("%)").toIntOrNull() ?: 80
                        val spentAmount = breach.substringAfter("Spent: ").substringBefore(",")
                        val limitAmount = breach.substringAfter("Budget: ").substringBefore(" (")
                        
                        notificationManager.showBudgetWarning(percentage, categoryName, spentAmount, limitAmount)
                    }
                }
            }
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
                val spentAmounts = getSpentAmountsUseCase()
                _spentAmounts.value = spentAmounts
            } catch (e: Exception) {
                // Handle error
                _spentAmounts.value = emptyMap()
            }
        }
    }
}
