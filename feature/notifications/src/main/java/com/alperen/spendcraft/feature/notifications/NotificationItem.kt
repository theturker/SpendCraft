package com.alperen.spendcraft.feature.notifications

import com.alperen.spendcraft.core.notifications.NotificationType

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean = false
)
