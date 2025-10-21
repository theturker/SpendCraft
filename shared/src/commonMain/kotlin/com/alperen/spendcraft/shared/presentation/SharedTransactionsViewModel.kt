package com.alperen.spendcraft.shared.presentation

import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Shared TransactionsViewModel - iOS ve Android'de kullanılır
 * 
 * iOS Usage:
 * ```swift
 * class TransactionsViewModel: ObservableObject {
 *     private let shared = SharedTransactionsViewModel(repository: koin.get())
 *     @Published var transactions: [Transaction] = []
 *     
 *     init() {
 *         shared.transactions.watch { [weak self] txs in
 *             self?.transactions = txs
 *         }
 *     }
 * }
 * ```
 * 
 * Android Usage:
 * ```kotlin
 * @HiltViewModel
 * class TransactionsViewModel @Inject constructor(
 *     private val shared: SharedTransactionsViewModel
 * ) : ViewModel() {
 *     val transactions = shared.transactions.asLiveData()
 * }
 * ```
 */
class SharedTransactionsViewModel(
    private val repository: TransactionsRepository
) {
    // CoroutineScope for shared ViewModel
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // MARK: - State Flows
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // MARK: - Computed Properties (iOS pattern)
    
    val currentBalance: StateFlow<Long> = transactions.map { txs ->
        txs.sumOf { tx ->
            when (tx.type) {
                TransactionType.INCOME -> tx.amount.minorUnits
                TransactionType.EXPENSE -> -tx.amount.minorUnits
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    
    val totalIncome: StateFlow<Long> = transactions.map { txs ->
        txs.filter { it.type == TransactionType.INCOME }
           .sumOf { it.amount.minorUnits }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    
    val totalExpense: StateFlow<Long> = transactions.map { txs ->
        txs.filter { it.type == TransactionType.EXPENSE }
           .sumOf { it.amount.minorUnits }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    
    // MARK: - Initialization
    
    init {
        loadData()
    }
    
    // MARK: - Public Methods
    
    fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load transactions
                repository.observeTransactions().collect { txs ->
                    _transactions.value = txs
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
        
        viewModelScope.launch {
            try {
                // Load categories
                repository.observeCategories().collect { cats ->
                    _categories.value = cats
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
        
        viewModelScope.launch {
            try {
                // Load accounts
                repository.observeAccounts().collect { accs ->
                    _accounts.value = accs
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun addTransaction(
        amountMinor: Long,
        note: String?,
        categoryId: Long?,
        accountId: Long?,
        timestampUtcMillis: Long,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val transaction = Transaction(
                    id = null,
                    amount = Money(minorUnits = amountMinor),
                    timestampUtcMillis = timestampUtcMillis,
                    note = note,
                    categoryId = categoryId,
                    accountId = accountId,
                    type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
                )
                
                repository.upsert(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.upsert(transaction)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.delete(transactionId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addCategory(
        name: String, 
        color: String, 
        icon: String?,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val category = Category(
                    id = null,
                    name = name,
                    color = color,
                    icon = icon,
                    isIncome = isIncome
                )
                
                repository.insertCategory(category)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteCategory(categoryId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addAccount(
        name: String,
        type: String,
        currency: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val account = Account(
                    id = null,
                    name = name,
                    type = type,
                    currency = currency,
                    isDefault = false,
                    archived = false
                )
                
                repository.insertAccount(account)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateAccount(account)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAccount(accountId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // MARK: - Helper Methods
    
    fun categoriesForType(isIncome: Boolean): List<Category> {
        return _categories.value.filter { it.isIncome == isIncome }
    }
    
    fun totalSpentForCategory(categoryId: Long): Long {
        return _transactions.value
            .filter { it.categoryId == categoryId && it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.minorUnits }
    }
    
    fun getDefaultAccount(): Account? {
        return _accounts.value.firstOrNull { it.isDefault }
    }
    
    // MARK: - Cleanup
    
    fun onCleared() {
        // iOS: deinit
        // Android: ViewModel.onCleared()
        // CoroutineScope'daki tüm job'ları iptal et
        viewModelScope.coroutineContext[kotlinx.coroutines.Job]?.cancel()
    }
}

