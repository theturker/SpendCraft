package com.alperen.spendcraft.core.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationEventBus @Inject constructor() {
    
    private val _notificationEvents = MutableSharedFlow<NotificationEvent>()
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()
    
    suspend fun sendNotificationEvent(event: NotificationEvent) {
        _notificationEvents.emit(event)
    }
}

data class NotificationEvent(
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    BUDGET_ALERT,
    SPENDING_REMINDER,
    ACHIEVEMENT,
    SYSTEM
}

