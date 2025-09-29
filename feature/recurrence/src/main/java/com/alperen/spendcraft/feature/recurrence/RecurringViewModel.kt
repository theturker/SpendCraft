package com.alperen.spendcraft.feature.recurrence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.data.db.dao.RecurringTransactionDao
import com.alperen.spendcraft.data.db.entities.RecurringTransactionEntity
import com.alperen.spendcraft.data.db.entities.RecurringFrequency
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    val billingRepository: BillingRepository,
    private val recurringTransactionDao: RecurringTransactionDao,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {
    
    private val _recurringTransactions = MutableStateFlow<List<RecurringTransactionEntity>>(emptyList())
    val recurringTransactions: StateFlow<List<RecurringTransactionEntity>> = _recurringTransactions.asStateFlow()
    
    init {
        loadRecurringTransactions()
    }
    
    private fun loadRecurringTransactions() {
        viewModelScope.launch {
            try {
                println("🔍 DEBUG: RecurringViewModel - loadRecurringTransactions başlıyor")
                // collect kullan - tüm değişiklikleri dinle
                recurringTransactionDao.getAllRecurringTransactions().collect { transactions ->
                    println("🔍 DEBUG: RecurringViewModel - ${transactions.size} adet tekrarlayan işlem yüklendi")
                    transactions.forEachIndexed { index, transaction ->
                        println("🔍 DEBUG: [$index] ${transaction.name} (${transaction.amount} kuruş, ${transaction.frequency}, ID: ${transaction.id})")
                    }
                    println("🔍 DEBUG: _recurringTransactions.value güncelleniyor: ${transactions.size} item")
                    _recurringTransactions.value = transactions
                    println("🔍 DEBUG: _recurringTransactions.value güncellendi: ${_recurringTransactions.value.size} item")
                }
            } catch (e: Exception) {
                // Handle error
                println("Error loading recurring transactions: ${e.message}")
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
                println("🔍 DEBUG: RecurringViewModel - addRecurringTransaction başlıyor")
                println("🔍 DEBUG: - name: $name, amount: $amount, categoryId: $categoryId")
                
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

                println("🔍 DEBUG: RecurringTransactionEntity oluşturuldu: $newTransaction")

                // Room database'e kaydet
                val insertedId = recurringTransactionDao.insertRecurringTransaction(newTransaction)
                println("🔍 DEBUG: Recurring transaction saved with ID: $insertedId")
                println("🔍 DEBUG: Kaydedilen transaction: $newTransaction")
                
                // Eğer başlangıç tarihi bugün ise, otomatik olarak normal işlem de oluştur
                val currentTime = System.currentTimeMillis()
                val todayStart = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val todayEnd = todayStart + (24 * 60 * 60 * 1000) - 1 // Günün sonu
                
                if (startDate >= todayStart && startDate <= todayEnd) {
                    println("🔍 DEBUG: Başlangıç tarihi bugün, normal işlem oluşturuluyor")
                    createTransactionFromRecurring(newTransaction, startDate)
                }
                
                // collect otomatik olarak güncellenecek, manuel yenileme gerekmez
            } catch (e: Exception) {
                println("Error saving recurring transaction: ${e.message}")
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
    
    private suspend fun createTransactionFromRecurring(recurringTransaction: RecurringTransactionEntity, executionDate: Long) {
        try {
            println("🔍 DEBUG: createTransactionFromRecurring çağrıldı")
            println("🔍 DEBUG: - name: ${recurringTransaction.name}, amount: ${recurringTransaction.amount}")
            
            // Normal işlem oluştur
            val transaction = com.alperen.spendcraft.core.model.Transaction(
                id = null, // Yeni işlem
                amount = com.alperen.spendcraft.core.model.Money(recurringTransaction.amount),
                note = recurringTransaction.note ?: recurringTransaction.name,
                categoryId = recurringTransaction.categoryId,
                accountId = recurringTransaction.accountId,
                type = recurringTransaction.type,
                timestampUtcMillis = executionDate
            )
            
            // TransactionsRepository'e kaydet
            transactionsRepository.upsert(transaction)
            println("🔍 DEBUG: Normal işlem oluşturuldu ve kaydedildi")
            
        } catch (e: Exception) {
            println("Error creating transaction from recurring: ${e.message}")
        }
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