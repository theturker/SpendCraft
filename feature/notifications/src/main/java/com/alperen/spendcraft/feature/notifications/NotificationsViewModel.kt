package com.alperen.spendcraft.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val transactionRepository: TransactionsRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val realNotifications = generateRealNotifications()
                _notifications.value = realNotifications
            } catch (e: Exception) {
                // Hata durumunda mock data kullan
                loadMockNotifications()
            }
        }
    }
    
    private suspend fun generateRealNotifications(): List<NotificationItem> {
        val notifications = mutableListOf<NotificationItem>()
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        // Gerçek verileri al
        val transactions = transactionRepository.getAllAscending()
        val budgets = budgetRepository.observeBudgets().first()
        
        // Debug log'ları
        println("🔍 DEBUG: Transactions count: ${transactions.size}")
        println("🔍 DEBUG: Budgets count: ${budgets.size}")
        
        if (transactions.isNotEmpty()) {
            println("🔍 DEBUG: First transaction: ${transactions.first()}")
        }
        if (budgets.isNotEmpty()) {
            println("🔍 DEBUG: First budget: ${budgets.first()}")
        }
        
        // 1. Hoş geldin bildirimi (eğer hiç işlem yoksa)
        if (transactions.isEmpty()) {
            notifications.add(
                NotificationItem(
                    id = "welcome_${currentTime}",
                    title = "Hoş Geldin!",
                    message = "SpendCraft'a hoş geldin! İlk işlemini ekleyerek başla.",
                    type = NotificationType.SYSTEM,
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }
        
        // 2. Bütçe uyarıları
        for (budget in budgets) {
            val categoryTransactions = transactions.filter { 
                it.categoryId?.toString() == budget.categoryId 
            }
            val monthlySpent = categoryTransactions.sumOf { it.amount.minorUnits }
            val budgetLimit = budget.monthlyLimitMinor
            val percentage = if (budgetLimit > 0) (monthlySpent * 100) / budgetLimit else 0
            
            println("🔍 DEBUG: Budget ${budget.categoryId} - Spent: $monthlySpent, Limit: $budgetLimit, Percentage: $percentage%")
            
            if (percentage >= 80) {
                println("🔍 DEBUG: Creating budget alert for category ${budget.categoryId} - $percentage%")
                notifications.add(
                    NotificationItem(
                        id = "budget_alert_${budget.categoryId}_${currentTime}",
                        title = "Bütçe Uyarısı",
                        message = "Kategori ${budget.categoryId} kategorisinde aylık bütçenizin %${percentage.toInt()}'ini harcadınız",
                        type = NotificationType.BUDGET_ALERT,
                        timestamp = currentTime,
                        isRead = false
                    )
                )
            }
        }
        
        // 3. Günlük harcama hatırlatmaları (sabah, öğlen, akşam)
        val todayStart = calendar.apply { 
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val todayTransactions = transactions.filter { it.timestampUtcMillis >= todayStart }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        
        // Sabah hatırlatması (08:00-10:00 arası)
        if (currentHour in 8..10 && todayTransactions.isEmpty()) {
            notifications.add(
                NotificationItem(
                    id = "morning_reminder_${currentTime}",
                    title = "Sabah Hatırlatması",
                    message = "Günlük harcamalarınızı kaydetmeyi unutmayın!",
                    type = NotificationType.SPENDING_REMINDER,
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }
        
        // Öğlen hatırlatması (12:00-14:00 arası)
        if (currentHour in 12..14 && todayTransactions.isEmpty()) {
            notifications.add(
                NotificationItem(
                    id = "afternoon_reminder_${currentTime}",
                    title = "Öğlen Hatırlatması",
                    message = "Günlük harcamalarınızı kaydetmeyi unutmayın!",
                    type = NotificationType.SPENDING_REMINDER,
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }
        
        // Akşam hatırlatması (18:00-20:00 arası)
        if (currentHour in 18..20 && todayTransactions.isEmpty()) {
            notifications.add(
                NotificationItem(
                    id = "evening_reminder_${currentTime}",
                    title = "Akşam Hatırlatması",
                    message = "Günlük harcamalarınızı kaydetmeyi unutmayın!",
                    type = NotificationType.SPENDING_REMINDER,
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }
        
        // 4. Aylık gelir hatırlatması (ayın 1-5'i arası)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        if (dayOfMonth in 1..5) {
            val thisMonthStart = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val thisMonthTransactions = transactions.filter { it.timestampUtcMillis >= thisMonthStart }
            val hasIncome = thisMonthTransactions.any { it.type == com.alperen.spendcraft.core.model.TransactionType.INCOME && it.amount.minorUnits > 0 }
            
            if (!hasIncome) {
                notifications.add(
                    NotificationItem(
                        id = "income_reminder_${currentTime}",
                        title = "Aylık Gelir Hatırlatması",
                        message = "Bu ay henüz gelir eklemediniz. Gelirinizi eklemeyi unutmayın!",
                        type = NotificationType.SPENDING_REMINDER,
                        timestamp = currentTime,
                        isRead = false
                    )
                )
            }
        }
        
        // 5. Haftalık düzenli harcama hatırlatması (Pazartesi günleri)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.MONDAY) {
            notifications.add(
                NotificationItem(
                    id = "weekly_reminder_${currentTime}",
                    title = "Haftalık Hatırlatma",
                    message = "Bu hafta düzenli harcamalarınızı eklemeyi unutmayın!",
                    type = NotificationType.SPENDING_REMINDER,
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }
        
        return notifications
    }
    
    private fun loadMockNotifications() {
        val mockNotifications = listOf(
            NotificationItem(
                id = "mock_1",
                title = "Hoş Geldin!",
                message = "SpendCraft'a hoş geldin! İlk işlemini ekleyerek başla.",
                type = NotificationType.BUDGET_ALERT,
                timestamp = System.currentTimeMillis(),
                isRead = false
            ),
            NotificationItem(
                id = "mock_2",
                title = "Bütçe Uyarısı",
                message = "Bu ay bütçenin %80'ini aştın. Dikkatli ol!",
                type = NotificationType.BUDGET_ALERT,
                timestamp = System.currentTimeMillis() - 86400000,
                isRead = false
            ),
            NotificationItem(
                id = "mock_3",
                title = "Günlük Hatırlatma",
                message = "Bugün henüz hiç işlem eklemedin. Harcamalarını takip et!",
                type = NotificationType.SPENDING_REMINDER,
                timestamp = System.currentTimeMillis() - 172800000,
                isRead = true
            )
        )
        _notifications.value = mockNotifications
    }
    
    fun markNotificationAsRead(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        val notificationIndex = currentNotifications.indexOfFirst { it.id == notificationId }
        if (notificationIndex != -1) {
            currentNotifications[notificationIndex] = currentNotifications[notificationIndex].copy(isRead = true)
            _notifications.value = currentNotifications
        }
    }
    
    fun deleteNotification(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.removeAll { it.id == notificationId }
        _notifications.value = currentNotifications
    }
    
    fun sendNotification(title: String, message: String, type: NotificationType) {
        val newNotification = NotificationItem(
            id = "custom_${System.currentTimeMillis()}",
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.add(0, newNotification) // En üste ekle
        _notifications.value = currentNotifications
    }
    
    fun refreshNotifications() {
        loadNotifications()
    }
}