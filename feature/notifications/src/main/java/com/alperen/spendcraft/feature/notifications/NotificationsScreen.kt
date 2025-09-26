package com.alperen.spendcraft.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import java.text.SimpleDateFormat
import java.util.*

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

@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {}
) {
    // Mock data - gerçek uygulamada repository'den gelecek
    val notifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "Bütçe Uyarısı",
                message = "Market kategorisinde aylık bütçenizin %85'ini harcadınız",
                type = NotificationType.BUDGET_ALERT,
                timestamp = System.currentTimeMillis() - 3600000, // 1 saat önce
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "AI Önerisi",
                message = "Bu ay kahve harcamalarınız %30 arttı. Alternatif öneriler için AI sayfasını ziyaret edin.",
                type = NotificationType.AI_INSIGHT,
                timestamp = System.currentTimeMillis() - 7200000, // 2 saat önce
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "Başarım Kazandınız!",
                message = "🎉 7 gün üst üste işlem kaydettiniz! Streak Badge kazandınız.",
                type = NotificationType.ACHIEVEMENT,
                timestamp = System.currentTimeMillis() - 86400000, // 1 gün önce
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "Ödeme Hatırlatması",
                message = "Elektrik faturası ödemesi yarın son gün",
                type = NotificationType.PAYMENT_DUE,
                timestamp = System.currentTimeMillis() - 172800000, // 2 gün önce
                isRead = true
            ),
            NotificationItem(
                id = "5",
                title = "Harcama Hatırlatması",
                message = "Bugün henüz bir işlem kaydetmediniz",
                type = NotificationType.SPENDING_REMINDER,
                timestamp = System.currentTimeMillis() - 259200000, // 3 gün önce
                isRead = true
            ),
            NotificationItem(
                id = "6",
                title = "Sistem Güncellemesi",
                message = "SpendCraft v2.1.0 güncellemesi mevcut. Yeni AI özelliklerini keşfedin!",
                type = NotificationType.SYSTEM,
                timestamp = System.currentTimeMillis() - 604800000, // 1 hafta önce
                isRead = true
            )
        )
    }

    AppScaffold(
        title = "Bildirimler",
        onBack = onBack,
        actions = {
            IconButton(onClick = { /* Mark all as read */ }) {
                Icon(
                    painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_done_all_vector),
                    contentDescription = "Tümünü okundu işaretle"
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bildirim Yok",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Henüz hiç bildiriminiz yok",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Unread notifications first
                val unreadNotifications = notifications.filter { !it.isRead }
                val readNotifications = notifications.filter { it.isRead }

                if (unreadNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Okunmamış (${unreadNotifications.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    items(unreadNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { /* Handle mark as read */ },
                            onDelete = { /* Handle delete */ }
                        )
                    }
                }

                if (readNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Okunmuş",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                    }

                    items(readNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { /* Handle mark as read */ },
                            onDelete = { /* Handle delete */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notification icon and unread indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getNotificationIcon(notification.type),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = getNotificationColor(notification.type)
                )
                
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = 8.dp, y = (-8).dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                    color = if (notification.isRead) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = dateFormatter.format(Date(notification.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!notification.isRead) {
                    IconButton(
                        onClick = onMarkAsRead,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "Okundu işaretle",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Sil",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.BUDGET_ALERT -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_warning_vector)
    NotificationType.SPENDING_REMINDER -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_schedule_vector)
    NotificationType.ACHIEVEMENT -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_emoji_events_vector)
    NotificationType.PAYMENT_DUE -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_payment_vector)
    NotificationType.AI_INSIGHT -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector)
    NotificationType.SYSTEM -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_system_update_vector)
}

@Composable
private fun getNotificationColor(type: NotificationType) = when (type) {
    NotificationType.BUDGET_ALERT -> MaterialTheme.colorScheme.error
    NotificationType.SPENDING_REMINDER -> MaterialTheme.colorScheme.primary
    NotificationType.ACHIEVEMENT -> Color(0xFFFFD700) // Gold
    NotificationType.PAYMENT_DUE -> MaterialTheme.colorScheme.error
    NotificationType.AI_INSIGHT -> MaterialTheme.colorScheme.secondary
    NotificationType.SYSTEM -> MaterialTheme.colorScheme.onSurfaceVariant
}
