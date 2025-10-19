package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.feature.notifications.CustomNotification
import com.alperen.spendcraft.feature.notifications.NotificationManager
import com.alperen.spendcraft.feature.notifications.NotificationTemplate
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS NotificationSettingsView'in birebir Android karşılığı
 * 
 * iOS Source: iosApp/SpendCraftiOS/NotificationSettingsView.swift:1-739
 * 
 * Features:
 * - Authorization status
 * - Template notifications (20+ templates)
 * - Custom notifications
 * - Day/time picker
 * - Category grouping
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationManager = remember { NotificationManager.getInstance(context) }
    
    val templates by notificationManager.templates.collectAsState()
    val customNotifications by notificationManager.customNotifications.collectAsState()
    val isAuthorized by notificationManager.isAuthorized.collectAsState()
    
    var selectedTemplate by remember { mutableStateOf<NotificationTemplate?>(null) }
    var selectedCustom by remember { mutableStateOf<CustomNotification?>(null) }
    var showAddCustom by remember { mutableStateOf(false) }
    
    // Group templates by category - iOS: groupedTemplates()
    val groupedTemplates = remember(templates) {
        templates.groupBy { it.category }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bildirim Ayarları") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = CoreR.drawable.ic_chevron_left),
                            contentDescription = "Geri"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Authorization Status - iOS: NotificationSettingsView.swift:20-57
            item {
                AuthorizationStatusCard(isAuthorized = isAuthorized)
            }
            
            // Template Notifications Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hazır Bildirimler (${templates.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Template Categories - iOS: ForEach(groupedTemplates().keys.sorted())
            groupedTemplates.keys.sorted().forEach { category ->
                val categoryTemplates = groupedTemplates[category] ?: emptyList()
                val enabledCount = categoryTemplates.count { it.isEnabled }
                
                item {
                    TemplateCategorySection(
                        category = category,
                        templates = categoryTemplates,
                        enabledCount = enabledCount,
                        onTemplateClick = { selectedTemplate = it }
                    )
                }
            }
            
            // Footer for templates
            item {
                Text(
                    text = "Hazır bildirim senaryolarını aktifleştirip saatlerini düzenleyebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Custom Notifications Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Özel Bildirimlerim (${customNotifications.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Custom Notifications List
            items(customNotifications) { notification ->
                CustomNotificationRow(
                    notification = notification,
                    onClick = { selectedCustom = notification },
                    onDelete = { notificationManager.deleteCustomNotification(notification) }
                )
            }
            
            // Add Custom Button
            item {
                AddCustomNotificationButton(onClick = { showAddCustom = true })
            }
            
            // Footer for custom
            item {
                Text(
                    text = "Kendi özel hatırlatmalarınızı oluşturun.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
    
    // Edit Template Sheet - iOS: EditTemplateView
    selectedTemplate?.let { template ->
        EditTemplateSheet(
            template = template,
            onDismiss = { selectedTemplate = null },
            onSave = { updated ->
                notificationManager.updateTemplate(updated)
                selectedTemplate = null
            }
        )
    }
    
    // Edit Custom Notification Sheet
    selectedCustom?.let { notification ->
        EditCustomNotificationSheet(
            notification = notification,
            onDismiss = { selectedCustom = null },
            onSave = { updated ->
                notificationManager.updateCustomNotification(updated)
                selectedCustom = null
            }
        )
    }
    
    // Add Custom Notification Sheet
    if (showAddCustom) {
        AddCustomNotificationSheet(
            onDismiss = { showAddCustom = false },
            onAdd = { notification ->
                notificationManager.addCustomNotification(notification)
                showAddCustom = false
            }
        )
    }
}

// MARK: - Authorization Status Card

@Composable
private fun AuthorizationStatusCard(
    isAuthorized: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bildirim Durumu",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isAuthorized) "Aktif" else "Kapalı",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                painter = painterResource(
                    id = if (isAuthorized) CoreR.drawable.ic_checkmark_circle_fill 
                    else CoreR.drawable.ic_xmark_circle_fill
                ),
                contentDescription = null,
                tint = if (isAuthorized) IOSColors.Green else IOSColors.Red,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// MARK: - Template Category Section

@Composable
private fun TemplateCategorySection(
    category: String,
    templates: List<NotificationTemplate>,
    enabledCount: Int,
    onTemplateClick: (NotificationTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Category Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = categoryIcon(category)),
                        contentDescription = null,
                        tint = categoryColor(category),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$enabledCount/${templates.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Template List
        if (expanded) {
            templates.forEach { template ->
                NotificationTemplateRow(
                    template = template,
                    onClick = { onTemplateClick(template) }
                )
            }
        }
    }
}

@Composable
private fun NotificationTemplateRow(
    template: NotificationTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (template.isEnabled) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else 
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%02d:%02d", template.hour, template.minute),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = IOSColors.Blue
                    )
                    template.daysOfWeek?.let { days ->
                        Text(
                            text = "• ${daysText(days)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    template.daysOfMonth?.let { days ->
                        Text(
                            text = "• Ayın ${monthDaysText(days)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Icon(
                painter = painterResource(
                    id = if (template.isEnabled) CoreR.drawable.ic_checkmark_circle_fill 
                    else CoreR.drawable.ic_circle
                ),
                contentDescription = null,
                tint = if (template.isEnabled) IOSColors.Green else MaterialTheme.colorScheme.outline
            )
        }
    }
}

// MARK: - Custom Notification Row

@Composable
private fun CustomNotificationRow(
    notification: CustomNotification,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_bell_badge_fill),
                contentDescription = null,
                tint = IOSColors.Purple,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = String.format("%02d:%02d", notification.hour, notification.minute),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = IOSColors.Purple
                    )
                    notification.daysOfWeek?.let { days ->
                        Text(
                            text = "• ${daysText(days)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_trash_fill),
                    contentDescription = "Sil",
                    tint = IOSColors.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Bildirimi Sil") },
            text = { Text("Bu özel bildirimi silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Sil", color = IOSColors.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun AddCustomNotificationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_plus_circle_fill),
                contentDescription = null,
                tint = IOSColors.Blue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Özel Bildirim Ekle",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = IOSColors.Blue
            )
        }
    }
}

// MARK: - Helper Functions

private fun categoryIcon(category: String): Int = when (category) {
    "Sabah" -> CoreR.drawable.ic_sun_max_fill // SF Symbol: sun.max.fill
    "Öğlen" -> CoreR.drawable.ic_sun_min_fill // SF Symbol: sun.min.fill
    "Akşam" -> CoreR.drawable.ic_moon_stars_fill // SF Symbol: moon.stars.fill
    "Haftalık" -> CoreR.drawable.ic_calendar // SF Symbol: calendar
    "Aylık" -> CoreR.drawable.ic_calendar_badge_clock // SF Symbol: calendar.badge.clock
    "Motivasyon" -> CoreR.drawable.ic_star_fill // SF Symbol: star.fill
    "Özel" -> CoreR.drawable.ic_gift_fill // SF Symbol: gift.fill
    else -> CoreR.drawable.ic_bell_fill // SF Symbol: bell.fill
}

private fun categoryColor(category: String): Color = when (category) {
    "Sabah" -> IOSColors.Orange
    "Öğlen" -> IOSColors.Yellow
    "Akşam" -> Color(0xFF5E5CE6) // Indigo
    "Haftalık" -> IOSColors.Blue
    "Aylık" -> IOSColors.Green
    "Motivasyon" -> IOSColors.Purple
    "Özel" -> Color(0xFFFF2D55) // Pink
    else -> Color.Gray
}

private fun daysText(days: List<Int>): String {
    val dayNames = listOf("Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt")
    return days.map { dayNames.getOrNull(it - 1) ?: "" }.joinToString(", ")
}

private fun monthDaysText(days: List<Int>): String {
    return if (days.size > 4) {
        "${days.first()}-${days.last()}. günleri"
    } else {
        days.joinToString(", ") { "$it." } + " günleri"
    }
}

