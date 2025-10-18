package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS NotificationSettingsView'in Android karşılığı
 * 
 * iOS Pattern:
 * - Toggle switches for notification preferences
 * - Saved to @AppStorage / UserDefaults
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    
    // iOS @AppStorage pattern
    var budgetNotifications by remember { mutableStateOf(prefs.getBoolean("budgetNotifications", true)) }
    var achievementNotifications by remember { mutableStateOf(prefs.getBoolean("achievementNotifications", true)) }
    var recurringNotifications by remember { mutableStateOf(prefs.getBoolean("recurringNotifications", true)) }
    var dailySummary by remember { mutableStateOf(prefs.getBoolean("dailySummary", false)) }
    
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
            // Section Header
            item {
                Text(
                    text = "Bildirim Tercihleri",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Budget Notifications
            item {
                NotificationToggleItem(
                    title = "Bütçe Bildirimleri",
                    description = "Bütçe limitine yaklaştığınızda bildirim al",
                    isChecked = budgetNotifications,
                    onCheckedChange = { 
                        budgetNotifications = it
                        prefs.edit().putBoolean("budgetNotifications", it).apply()
                    }
                )
            }
            
            // Achievement Notifications
            item {
                NotificationToggleItem(
                    title = "Başarım Bildirimleri",
                    description = "Yeni başarımlar kazandığınızda bildirim al",
                    isChecked = achievementNotifications,
                    onCheckedChange = { 
                        achievementNotifications = it
                        prefs.edit().putBoolean("achievementNotifications", it).apply()
                    }
                )
            }
            
            // Recurring Transaction Notifications
            item {
                NotificationToggleItem(
                    title = "Tekrarlayan İşlem Bildirimleri",
                    description = "Tekrarlayan işlemler eklendiğinde bildirim al",
                    isChecked = recurringNotifications,
                    onCheckedChange = { 
                        recurringNotifications = it
                        prefs.edit().putBoolean("recurringNotifications", it).apply()
                    }
                )
            }
            
            // Daily Summary
            item {
                NotificationToggleItem(
                    title = "Günlük Özet",
                    description = "Her gün sonunda harcama özeti al",
                    isChecked = dailySummary,
                    onCheckedChange = { 
                        dailySummary = it
                        prefs.edit().putBoolean("dailySummary", it).apply()
                    }
                )
            }
            
            // Footer
            item {
                Text(
                    text = "Bildirimleri dilediğiniz zaman açıp kapatabilirsiniz. Sistem bildirim izinlerini kontrol etmeyi unutmayın.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

