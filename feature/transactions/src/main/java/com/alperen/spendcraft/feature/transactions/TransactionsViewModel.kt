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
import com.alperen.spendcraft.domain.usecase.CheckBudgetBreachesUseCase
import com.alperen.spendcraft.core.notifications.NotificationManager
import com.alperen.spendcraft.core.notifications.NotificationEventBus
import com.alperen.spendcraft.core.notifications.NotificationEvent
import com.alperen.spendcraft.core.notifications.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.alperen.spendcraft.core.common.NotificationTester
// import com.alperen.spendcraft.core.billing.BillingRepository

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
    private val notificationTester: NotificationTester,
    private val checkBudgetBreachesUseCase: CheckBudgetBreachesUseCase,
    private val notificationManager: NotificationManager,
    private val notificationEventBus: NotificationEventBus,
    // private val billingRepository: BillingRepository
) : ViewModel() {

    val items: StateFlow<List<Transaction>> = observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<com.alperen.spendcraft.core.model.Category>> = observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val accounts: StateFlow<List<com.alperen.spendcraft.core.model.Account>> = observeAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val streak: StateFlow<com.alperen.spendcraft.core.model.Streak> = observeStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), com.alperen.spendcraft.core.model.Streak(0, 0))
    
    // val isPremium: StateFlow<Boolean> = billingRepository.isPremium
    //     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

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
            
            // Bütçe kontrolü yap ve bildirim gönder
            checkBudgetBreaches()
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
                    || it.name.contains("Alışveriş", ignoreCase = true) 
                    || it.name.contains("Yemek", ignoreCase = true) }
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
            
            // Bütçe kontrolü yap ve bildirim gönder
            checkBudgetBreaches()
        }
    }
    
    fun updateTransactionCategory(transactionId: Long, categoryId: Long) {
        viewModelScope.launch {
            updateTransactionCategory(transactionId, categoryId)
        }
    }
    
    // TEST ONLY - Remove in production
    fun testNotificationNow() {
        notificationTester.testNotificationNow()
    }
    
    private suspend fun checkBudgetBreaches() {
        try {
            val breaches = checkBudgetBreachesUseCase()
            
            // Bütçe aşımı tespit edildiğinde bildirim gönder
            if (breaches.isNotEmpty()) {
                for (breach in breaches) {
                    // Breach mesajından kategori adını çıkar
                    val categoryName = breach.substringAfter("Budget exceeded for ").substringBefore("!")
                    notificationManager.showBudgetAlert("100%", categoryName)
                    
                    // Uygulama içi bildirim ekle
                    notificationEventBus.sendNotificationEvent(
                        NotificationEvent(
                            title = "⚠️ Bütçe Uyarısı",
                            message = "$categoryName bütçenizi 100% aştınız!",
                            type = NotificationType.BUDGET_ALERT
                        )
                    )
                }
            }
            
            // Genel bütçe kontrolü - toplam gelir vs gider
            checkGeneralBudgetBreach()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    private suspend fun checkGeneralBudgetBreach() {
        try {
            val currentMonth = getCurrentMonth()
            val (monthStart, monthEnd) = getMonthBoundaries(currentMonth)
            
            // Bu ayın tüm işlemlerini al
            val allTransactions = items.value.filter { tx ->
                tx.timestampUtcMillis >= monthStart && tx.timestampUtcMillis < monthEnd
            }
            
            val totalIncome = allTransactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.minorUnits }
            val totalExpense = allTransactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.minorUnits }
            
            val netAmount = totalIncome - totalExpense
            
            // Eğer net negatif ise (gider > gelir) bildirim gönder
            if (netAmount < 0) {
                val deficit = -netAmount
                val deficitFormatted = String.format("%.2f", deficit / 100.0)
                notificationManager.showBudgetAlert("Açık", "Toplam açığınız: $deficitFormatted TL")
                
                // Uygulama içi bildirim ekle
                notificationEventBus.sendNotificationEvent(
                    NotificationEvent(
                        title = "⚠️ Genel Bütçe Uyarısı",
                        message = "Toplam açığınız: $deficitFormatted TL",
                        type = NotificationType.BUDGET_ALERT
                    )
                )
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    private fun getCurrentMonth(): String {
        val calendar = java.util.Calendar.getInstance()
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}"
    }
    
    private fun getMonthBoundaries(monthKey: String): Pair<Long, Long> {
        val (year, month) = monthKey.split("-").map { it.toInt() }
        val calendar = java.util.Calendar.getInstance()
        
        // Ayın başlangıcı
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis
        
        // Ayın sonu
        calendar.add(java.util.Calendar.MONTH, 1)
        val monthEnd = calendar.timeInMillis
        
        return Pair(monthStart, monthEnd)
    }
}




