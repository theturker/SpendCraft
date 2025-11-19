package com.alperen.spendcraft.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.notifications.NotificationType
// import com.alperen.spendcraft.ui.iosTheme.*  // Note: IOSTheme in app module
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    viewModel: NotificationsViewModel? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Gerçek veri - ViewModel'den gelecek
    val notifications by (viewModel?.notifications ?: remember { 
        kotlinx.coroutines.flow.MutableStateFlow(emptyList<NotificationItem>()) 
    }).collectAsState(initial = emptyList())
    
    // Gerçek veri kullan
    val displayNotifications = notifications
    
    // iOS scroll behavior
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = lerp(start = 34.sp, stop = 17.sp, fraction = collapsedFraction)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                LargeTopAppBar(
                    title = { Spacer(modifier = Modifier) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                                contentDescription = context.getString(com.alperen.spendcraft.feature.notifications.R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            displayNotifications.forEach { notification ->
                                if (!notification.isRead) {
                                    viewModel?.markAsRead(notification.id)
                                }
                            }
                        }) {
                            Icon(
                                painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_done_all_vector),
                                contentDescription = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_mark_all_read)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (collapsedFraction > 0.5f) 64.dp else 152.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = if (collapsedFraction > 0.5f) {
                        Alignment.Center
                    } else {
                        Alignment.BottomStart
                    }
                ) {
                    Text(
                        text = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_title),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = if (collapsedFraction > 0.5f) {
                            Modifier
                        } else {
                            Modifier.padding(start = 16.dp, bottom = 8.dp)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (displayNotifications.isEmpty()) {
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
                                text = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_no_notifications),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_no_notifications_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Unread notifications first
                val unreadNotifications = displayNotifications.filter { !it.isRead }
                val readNotifications = displayNotifications.filter { it.isRead }

                // iOS: Section("Okunmamış") - NotificationsView.swift:90-119
                if (unreadNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_unread, unreadNotifications.size),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    items(unreadNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { viewModel?.markAsRead(notification.id) },
                            onDelete = { viewModel?.deleteNotification(notification.id) }
                        )
                    }
                }

                if (readNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_read),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                    }

                    items(readNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { viewModel?.markAsRead(notification.id) },
                            onDelete = { viewModel?.deleteNotification(notification.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    // iOS swipe: .swipeActions(edge: .trailing, allowsFullSwipe: true)
    // NotificationsView.swift:190-205
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                else -> false
            }
        }
    )
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // iOS: Button(role: .destructive) - Red background
            val color = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.EndToStart -> IOSColors.Red
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_delete_description),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
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
                    text = com.alperen.spendcraft.core.ui.NotificationLocalization.localizeTitle(context, notification.title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                    color = if (notification.isRead) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = com.alperen.spendcraft.core.ui.NotificationLocalization.localizeMessage(context, notification.message),
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
                            contentDescription = context.getString(com.alperen.spendcraft.feature.notifications.R.string.notifications_mark_read),
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
                        contentDescription = context.getString(com.alperen.spendcraft.feature.notifications.R.string.delete),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    } // SwipeToDismissBox end
}

@Composable
private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.BUDGET_ALERT -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_warning_vector)
    NotificationType.SPENDING_REMINDER -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_schedule_vector)
    NotificationType.ACHIEVEMENT -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_emoji_events_vector)
    NotificationType.SYSTEM -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_system_update_vector)
}

@Composable
private fun getNotificationColor(type: NotificationType) = when (type) {
    NotificationType.BUDGET_ALERT -> MaterialTheme.colorScheme.error
    NotificationType.SPENDING_REMINDER -> MaterialTheme.colorScheme.primary
    NotificationType.ACHIEVEMENT -> Color(0xFFFFD700) // Gold
    NotificationType.SYSTEM -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(name = "Notifications - Light")
@Composable
private fun NotificationsScreenPreview() {
    NotificationsScreen(onBack = {})
}
