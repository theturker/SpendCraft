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
import com.alperen.spendcraft.domain.usecase.InsertCategoryUseCase
import com.alperen.spendcraft.domain.usecase.DeleteCategoryUseCase
import com.alperen.spendcraft.domain.usecase.ObserveAccountsUseCase
import com.alperen.spendcraft.domain.usecase.UpdateAccountUseCase
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsByAccountUseCase
import com.alperen.spendcraft.domain.usecase.MarkTodayLoggedUseCase
import com.alperen.spendcraft.domain.usecase.ObserveStreakUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.alperen.spendcraft.core.common.NotificationTester

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    observeTransactions: ObserveTransactionsUseCase,
    observeCategories: ObserveCategoriesUseCase,
    private val observeAccounts: ObserveAccountsUseCase,
    private val observeTransactionsByAccount: ObserveTransactionsByAccountUseCase,
    private val upsert: UpsertTransactionUseCase,
    private val delete: DeleteTransactionUseCase,
    private val insertCategory: InsertCategoryUseCase,
    private val deleteCategory: DeleteCategoryUseCase,
    private val updateAccount: UpdateAccountUseCase,
    private val markTodayLogged: MarkTodayLoggedUseCase,
    observeStreak: ObserveStreakUseCase,
    private val notificationTester: NotificationTester
) : ViewModel() {

    val items: StateFlow<List<Transaction>> = observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<com.alperen.spendcraft.core.model.Category>> = observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val accounts: StateFlow<List<com.alperen.spendcraft.core.model.Account>> = observeAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val streak: StateFlow<com.alperen.spendcraft.core.model.Streak> = observeStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), com.alperen.spendcraft.core.model.Streak(0, 0))

    // Quick add state
    private val _lastUsedCategoryId = mutableStateOf<Long?>(null)
    val lastUsedCategoryId: State<Long?> = _lastUsedCategoryId
    
    private val _lastNote = mutableStateOf("")
    val lastNote: State<String> = _lastNote

    fun saveTransaction(
        amountMinor: Long,
        note: String?,
        categoryId: Long?,
        accountId: Long?,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                id = null,
                amount = Money(amountMinor),
                timestampUtcMillis = System.currentTimeMillis(),
                note = note,
                categoryId = categoryId,
                accountId = accountId,
                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
            )
            upsert(tx)
            
            // Mark today as logged for streak
            markTodayLogged()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { delete(id) }
    }
    
    fun addCategory(name: String) {
        viewModelScope.launch { insertCategory(name) }
    }
    
    fun removeCategory(id: Long) {
        viewModelScope.launch { deleteCategory(id) }
    }
    
    fun updateAccountName(accountId: Long, newName: String) {
        viewModelScope.launch {
            val account = com.alperen.spendcraft.core.model.Account(
                id = accountId,
                name = newName
            )
            updateAccount(account)
        }
    }
    
    fun getTransactionsByAccount(accountId: Long): StateFlow<List<Transaction>> {
        return observeTransactionsByAccount(accountId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }
    
    fun addQuick(amountMinor: Long) {
        viewModelScope.launch {
            // Use last used category or find a default "Market" category
            val categoryId = _lastUsedCategoryId.value ?: run {
                val marketCategory = categories.value.find { it.name.contains("Market", ignoreCase = true) 
                    || it.name.contains("Shopping", ignoreCase = true) 
                    || it.name.contains("Food", ignoreCase = true) }
                marketCategory?.id ?: categories.value.firstOrNull()?.id
            }
            
            val defaultAccount = accounts.value.firstOrNull { account ->
                // Find default account or first account
                accounts.value.indexOf(account) == 0
            }
            
            val tx = Transaction(
                id = null,
                amount = Money(amountMinor),
                timestampUtcMillis = System.currentTimeMillis(),
                note = _lastNote.value.ifBlank { "Hızlı ekleme" },
                categoryId = categoryId,
                accountId = defaultAccount?.id,
                type = TransactionType.EXPENSE // Quick add is always expense
            )
            
            upsert(tx)
            
            // Mark today as logged for streak
            markTodayLogged()
            
            // Update last used values
            _lastUsedCategoryId.value = categoryId
        }
    }
    
    // TEST ONLY - Remove in production
    fun testNotificationNow() {
        notificationTester.testNotificationNow()
    }
}




