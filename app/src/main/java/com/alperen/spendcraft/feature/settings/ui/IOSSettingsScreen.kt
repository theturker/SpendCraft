package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS SettingsView'in birebir Android karşılığı
 * 
 * Sections:
 * 1. Finans (Hesaplar)
 * 2. Yapay Zeka (AI Önerileri, AI Ayarları)
 * 3. Özellikler (Tekrarlayan İşlemler, Başarımlar, Bildirimler, Bildirim Ayarları)
 * 4. Veri Yönetimi (Dışa/İçe Aktar)
 * 5. Hesap (Çıkış Yap)
 * 6. Uygulama (Versiyon, Toplam İşlem, Toplam Kategori)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSSettingsScreen(
    onNavigateToAccounts: () -> Unit,
    onNavigateToAISuggestions: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToNotificationSettings: () -> Unit,
    onNavigateToExport: () -> Unit,
    onSignOut: () -> Unit,
    totalTransactions: Int = 0,
    totalCategories: Int = 0,
    modifier: Modifier = Modifier
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Ayarlar",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
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
            // 1. Finans Section
            item {
                SettingsSectionHeader(title = "Finans")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_creditcard_fill,
                    iconColor = IOSColors.Blue,
                    title = "Hesaplar",
                    onClick = onNavigateToAccounts
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 2. Yapay Zeka Section
            item {
                SettingsSectionHeader(title = "🤖 Yapay Zeka")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_sparkles,
                    iconColor = IOSColors.Purple,
                    title = "AI Önerileri",
                    onClick = onNavigateToAISuggestions
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 3. Özellikler Section
            item {
                SettingsSectionHeader(title = "Özellikler")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_repeat_circle_fill,
                    iconColor = IOSColors.Orange,
                    title = "Tekrarlayan İşlemler",
                    onClick = onNavigateToRecurring
                )
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_trophy_fill,
                    iconColor = IOSColors.Yellow,
                    title = "Başarımlar",
                    onClick = onNavigateToAchievements
                )
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_bell_fill,
                    iconColor = IOSColors.Red,
                    title = "Bildirimler",
                    onClick = onNavigateToNotifications
                )
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_bell,
                    iconColor = IOSColors.Blue,
                    title = "Bildirim Ayarları",
                    onClick = onNavigateToNotificationSettings
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 4. Veri Yönetimi Section
            item {
                SettingsSectionHeader(title = "Veri Yönetimi")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_square_arrow_up,
                    iconColor = IOSColors.Green,
                    title = "Dışa/İçe Aktar",
                    onClick = onNavigateToExport
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 5. Hesap Section
            item {
                SettingsSectionHeader(title = "Hesap")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_key_fill,
                    iconColor = IOSColors.Red,
                    title = "Çıkış Yap",
                    titleColor = IOSColors.Red,
                    onClick = { showSignOutDialog = true }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 6. Uygulama Section
            item {
                SettingsSectionHeader(title = "Uygulama")
            }
            
            item {
                SettingsInfoRow(
                    title = "Versiyon",
                    value = "1.0.0"
                )
            }
            
            item {
                SettingsInfoRow(
                    title = "Toplam İşlem",
                    value = "$totalTransactions"
                )
            }
            
            item {
                SettingsInfoRow(
                    title = "Toplam Kategori",
                    value = "$totalCategories"
                )
            }
        }
    }
    
    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Çıkış yapılsın mı?") },
            text = { Text("Hesabınızdan çıkış yapacaksınız.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = IOSColors.Red
                    )
                ) {
                    Text("Çıkış Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

/**
 * Settings Section Header - iOS style
 */
@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * Settings List Item - iOS style
 */
@Composable
private fun SettingsListItem(
    icon: Int,
    iconColor: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Title and Subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = titleColor ?: MaterialTheme.colorScheme.onSurface
                )
                
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Chevron
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Settings Info Row (non-clickable) - iOS style
 */
@Composable
private fun SettingsInfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

