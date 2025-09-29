package com.alperen.spendcraft.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.core.notifications.NotificationEventBus
import com.alperen.spendcraft.core.notifications.NotificationEvent
import com.alperen.spendcraft.core.notifications.NotificationType
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
    private val budgetRepository: BudgetRepository,
    private val notificationEventBus: NotificationEventBus
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    init {
        loadNotifications()
        listenToNotificationEvents()
    }
    
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val realNotifications = generateRealNotifications()
                _notifications.value = realNotifications
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private suspend fun generateRealNotifications(): List<NotificationItem> {
        val notifications = mutableListOf<NotificationItem>()
        
        // Örnek bildirimler - gerçek veri yerine
        notifications.add(
            NotificationItem(
                id = "budget_1",
                title = "⚠️ Bütçe Uyarısı",
                message = "Yemek bütçenizi aştınız!",
                timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000L), // 2 saat önce
                type = NotificationType.BUDGET_ALERT,
                isRead = false
            )
        )
        
        // Günlük hatırlatma bildirimleri
        val today = Calendar.getInstance()
        for (i in 1..7) {
            val day = Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis - (i * 24 * 60 * 60 * 1000L)
            }
            notifications.add(
                NotificationItem(
                    id = "reminder_$i",
                    title = "📝 Günlük Hatırlatma",
                    message = "Bugünkü harcamalarınızı kaydetmeyi unutmayın!",
                    timestamp = day.timeInMillis,
                    type = NotificationType.SPENDING_REMINDER,
                    isRead = false
                )
            )
        }
        
        // Başarım bildirimleri
        notifications.add(
            NotificationItem(
                id = "achievement_1",
                title = "🏆 Yeni Başarım!",
                message = "İlk işleminizi kaydettiniz!",
                timestamp = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L),
                type = NotificationType.ACHIEVEMENT,
                isRead = false
            )
        )
        
        // Sistem bildirimleri
        notifications.add(
            NotificationItem(
                id = "system_1",
                title = "🎉 Hoş Geldiniz!",
                message = "SpendCraft'a hoş geldiniz!",
                timestamp = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L),
                type = NotificationType.SYSTEM,
                isRead = true
            )
        )
        
        return notifications.sortedByDescending { it.timestamp }
    }
    
    private fun listenToNotificationEvents() {
        viewModelScope.launch {
            notificationEventBus.notificationEvents.collect { event ->
                val newNotification = NotificationItem(
                    id = System.currentTimeMillis().toString(),
                    title = event.title,
                    message = event.message,
                    timestamp = event.timestamp,
                    type = event.type,
                    isRead = false
                )
                _notifications.value = listOf(newNotification) + _notifications.value
            }
        }
    }
    
    fun sendNotification(title: String, message: String, type: NotificationType) {
        val notification = NotificationItem(
            id = "notification_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            isRead = false
        )
        
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.add(0, notification) // En üste ekle
        _notifications.value = currentNotifications
    }
    
    fun markAsRead(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            _notifications.value = currentNotifications
        }
    }
    
    fun markAllAsRead() {
        val currentNotifications = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentNotifications
    }
    
    fun deleteNotification(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.removeAll { it.id == notificationId }
        _notifications.value = currentNotifications
    }
    
    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }
}
