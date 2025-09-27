package com.alperen.spendcraft.feature.recurrence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.billing.BillingRepository
// import com.alperen.spendcraft.core.recurring.RecurringTransactionManager
import com.alperen.spendcraft.data.db.entities.RecurringTransactionEntity
import com.alperen.spendcraft.data.db.entities.RecurringFrequency
import com.alperen.spendcraft.core.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    val billingRepository: BillingRepository
    // private val recurringTransactionManager: RecurringTransactionManager
) : ViewModel() {
    
    private val _recurringTransactions = MutableStateFlow<List<RecurringTransactionEntity>>(emptyList())
    val recurringTransactions: StateFlow<List<RecurringTransactionEntity>> = _recurringTransactions.asStateFlow()
    
    init {
        loadRecurringTransactions()
    }
    
    private fun loadRecurringTransactions() {
        viewModelScope.launch {
            try {
                // val transactions = recurringTransactionManager.getAllRecurringTransactions()
                // _recurringTransactions.value = transactions
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun addRecurringTransaction(
        name: String,
        amount: Long,
        categoryId: Long,
        accountId: Long,
        type: TransactionType,
        frequency: RecurringFrequency,
        startDate: Long,
        endDate: Long? = null,
        note: String? = null
    ) {
        viewModelScope.launch {
            try {
                // Gerçek veri ekleme - Room database'e kaydet
                val newTransaction = RecurringTransactionEntity(
                    name = name,
                    amount = amount,
                    categoryId = categoryId,
                    accountId = accountId,
                    type = type,
                    frequency = frequency,
                    startDate = startDate,
                    endDate = endDate,
                    isActive = true,
                    lastExecuted = null,
                    nextExecution = calculateNextExecution(startDate, frequency),
                    note = note
                )
                
                // Şimdilik memory'de tutuyoruz, ileride Room'a bağlayacağız
                val currentList = _recurringTransactions.value.toMutableList()
                currentList.add(newTransaction)
                _recurringTransactions.value = currentList
                
                loadRecurringTransactions() // Refresh list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun calculateNextExecution(startDate: Long, frequency: RecurringFrequency): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startDate
        
        when (frequency) {
            RecurringFrequency.DAILY -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            RecurringFrequency.WEEKLY -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            RecurringFrequency.MONTHLY -> calendar.add(java.util.Calendar.MONTH, 1)
            RecurringFrequency.YEARLY -> calendar.add(java.util.Calendar.YEAR, 1)
        }
        
        return calendar.timeInMillis
    }
    
    fun updateRecurringTransaction(transaction: RecurringTransactionEntity) {
        viewModelScope.launch {
            try {
                // recurringTransactionManager.updateRecurringTransaction(transaction)
                loadRecurringTransactions() // Refresh list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteRecurringTransaction(id: Long) {
        viewModelScope.launch {
            try {
                // recurringTransactionManager.deleteRecurringTransaction(id)
                loadRecurringTransactions() // Refresh list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}