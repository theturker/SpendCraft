package com.alperen.spendcraft.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.model.Money
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.usecase.DeleteTransactionUseCase
import com.alperen.spendcraft.domain.usecase.ObserveCategoriesUseCase
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase
import com.alperen.spendcraft.domain.usecase.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    observeTransactions: ObserveTransactionsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val upsert: UpsertTransactionUseCase,
    private val delete: DeleteTransactionUseCase
) : ViewModel() {

    val items: StateFlow<List<Transaction>> = observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<com.alperen.spendcraft.core.model.Category>> = observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveTransaction(
        amountMinor: Long,
        note: String?,
        categoryId: Long?,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                id = null,
                amount = Money(amountMinor),
                timestampUtcMillis = System.currentTimeMillis(),
                note = note,
                categoryId = categoryId,
                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
            )
            upsert(tx)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { delete(id) }
    }
}




