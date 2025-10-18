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
    
    // iOS NotificationsViewModel.swift:14 - @Published var unreadCount: Int = 0
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    init {
        loadNotifications()
        listenToNotificationEvents()
    }
    
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val realNotifications = generateRealNotifications()
                _notifications.value = realNotifications
                updateUnreadCount() // iOS'taki gibi
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // iOS NotificationsViewModel.swift:44-46
    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }
    
    private suspend fun generateRealNotifications(): List<NotificationItem> {
        // iOS pattern: Bildirimler sadece gerçek olaylardan oluşur
        // NotificationsViewModel.swift:26-34 - loadNotifications() UserDefaults'tan okur
        // Yeni kullanıcılar için boş liste
        return emptyList()
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
                updateUnreadCount() // iOS'taki gibi
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
        updateUnreadCount() // iOS'taki gibi
    }
    
    fun markAsRead(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            _notifications.value = currentNotifications
            updateUnreadCount() // iOS'taki gibi
        }
    }
    
    fun markAllAsRead() {
        val currentNotifications = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentNotifications
        updateUnreadCount() // iOS'taki gibi
    }
    
    fun deleteNotification(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.removeAll { it.id == notificationId }
        _notifications.value = currentNotifications
        updateUnreadCount() // iOS'taki gibi
    }
    
    fun clearAllNotifications() {
        _notifications.value = emptyList()
        updateUnreadCount() // iOS'taki gibi
    }
}
