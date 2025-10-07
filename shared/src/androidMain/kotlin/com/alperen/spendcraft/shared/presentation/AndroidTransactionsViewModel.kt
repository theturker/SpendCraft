package com.alperen.spendcraft.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.usecase.DeleteTransactionUseCase
import com.alperen.spendcraft.shared.domain.usecase.ObserveTransactionsUseCase
import com.alperen.spendcraft.shared.domain.usecase.UpsertTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Android-specific ViewModel wrapper
 * Extends AndroidX ViewModel for lifecycle awareness
 */
class AndroidTransactionsViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            observeTransactionsUseCase()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { transactionList ->
                    _transactions.value = transactionList
                    _isLoading.value = false
                }
        }
    }
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                upsertTransactionUseCase(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                upsertTransactionUseCase(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transactionId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

