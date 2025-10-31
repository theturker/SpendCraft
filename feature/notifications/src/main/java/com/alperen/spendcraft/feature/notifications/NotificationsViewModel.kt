package com.alperen.spendcraft.feature.notifications

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.core.notifications.NotificationEventBus
import com.alperen.spendcraft.core.notifications.NotificationEvent
import com.alperen.spendcraft.core.notifications.NotificationType
import com.alperen.spendcraft.core.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * iOS NotificationsViewModel.swift - birebir Android karşılığı
 * SharedPreferences ile bildirim listesini saklar (iOS: UserDefaults)
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionsRepository,
    private val budgetRepository: BudgetRepository,
    private val notificationEventBus: NotificationEventBus,
    private val systemNotificationManager: com.alperen.spendcraft.core.notifications.NotificationManager
) : ViewModel() {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "saved_notifications", // iOS: notificationsKey = "saved_notifications"
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    // iOS NotificationsViewModel.swift:14 - @Published var unreadCount: Int = 0
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    init {
        loadNotifications() // iOS: NotificationsViewModel.swift:26
        listenToNotificationEvents()
    }
    
    // iOS NotificationsViewModel.swift:26-35 - loadNotifications()
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val json = prefs.getString("notifications", null)
                if (json != null) {
                    val type = object : TypeToken<List<NotificationItem>>() {}.type
                    val loaded: List<NotificationItem> = gson.fromJson(json, type)
                    _notifications.value = loaded.sortedByDescending { it.timestamp }
                } else {
                    // Yeni kullanıcılar için boş liste
                    _notifications.value = emptyList()
                }
                updateUnreadCount() // iOS'taki gibi
            } catch (e: Exception) {
                _notifications.value = emptyList()
                updateUnreadCount()
            }
        }
    }
    
    // iOS NotificationsViewModel.swift:37-42 - saveNotifications()
    private fun saveNotifications() {
        viewModelScope.launch {
            try {
                val json = gson.toJson(_notifications.value)
                prefs.edit().putString("notifications", json).apply()
                updateUnreadCount()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // iOS NotificationsViewModel.swift:44-46
    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }
    
    // iOS NotificationsViewModel.swift:69-85 - addNotification()
    private fun listenToNotificationEvents() {
        viewModelScope.launch {
            notificationEventBus.notificationEvents.collect { event ->
                addNotification(event.title, event.message, event.type)
            }
        }
    }
    
    // iOS NotificationsViewModel.swift:69-85 - addNotification(title:message:type:)
    fun addNotification(title: String, message: String, type: NotificationType) {
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
        saveNotifications() // iOS'taki gibi kaydet
        
        // iOS: sendLocalNotification(title: title, message: message)
        // System notification gönder
        sendSystemNotification(title, message, type)
    }
    
    // iOS NotificationsViewModel.swift:166-185 - sendLocalNotification()
    private fun sendSystemNotification(title: String, message: String, type: NotificationType) {
        when (type) {
            NotificationType.BUDGET_ALERT -> {
                // Bütçe aşımı bildirimi
                val categoryName = message.substringAfter("kategori: ").substringBefore(" ").ifEmpty { "Kategori" }
                systemNotificationManager.showBudgetAlert("100%", categoryName)
            }
            NotificationType.SPENDING_REMINDER -> {
                systemNotificationManager.showSpendingReminder()
            }
            NotificationType.ACHIEVEMENT -> {
                val achievementName = title.removePrefix("🏆 ")
                systemNotificationManager.showAchievementNotification(achievementName, message)
            }
            NotificationType.SYSTEM -> {
                systemNotificationManager.showGeneralNotification(title, message)
            }
        }
    }
    
    // iOS NotificationsViewModel.swift:50-54 - markAsRead(_:)
    fun markAsRead(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            _notifications.value = currentNotifications
            saveNotifications() // iOS'taki gibi kaydet
        }
    }
    
    // iOS NotificationsViewModel.swift:56-61 - markAllAsRead()
    fun markAllAsRead() {
        val currentNotifications = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentNotifications
        saveNotifications() // iOS'taki gibi kaydet
    }
    
    // iOS NotificationsViewModel.swift:64-67 - deleteNotification(_:)
    fun deleteNotification(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.removeAll { it.id == notificationId }
        _notifications.value = currentNotifications
        saveNotifications() // iOS'taki gibi kaydet
    }
    
    // Clear all (custom method)
    fun clearAllNotifications() {
        _notifications.value = emptyList()
        saveNotifications() // iOS'taki gibi kaydet
    }
    
    // iOS NotificationsViewModel.swift:146-152 - sendSpendingReminder()
    fun sendSpendingReminder() {
        addNotification(
            title = context.getString(R.string.spending_reminder_title),
            message = context.getString(R.string.spending_reminder_message),
            type = NotificationType.SPENDING_REMINDER
        )
    }
}
