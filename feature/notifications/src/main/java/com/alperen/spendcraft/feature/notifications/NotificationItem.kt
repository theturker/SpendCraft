package com.alperen.spendcraft.feature.notifications

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean = false
)

enum class NotificationType {
    BUDGET_ALERT,
    SPENDING_REMINDER,
    ACHIEVEMENT,
    PAYMENT_DUE,
    AI_INSIGHT,
    SYSTEM
}
