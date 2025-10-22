package com.alperen.spendcraft.shared.domain.model

import kotlinx.serialization.Serializable

/**
 * Notification event model for shared business logic
 */
@Serializable
data class NotificationEvent(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestampUtcMillis: Long,
    val isRead: Boolean = false,
    val data: Map<String, String> = emptyMap()
)
