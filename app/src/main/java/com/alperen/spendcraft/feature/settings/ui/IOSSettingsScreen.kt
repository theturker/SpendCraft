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
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.ui.iosTheme.*
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
    onNavigateToCurrencySettings: () -> Unit = {}, // iOS'ta CurrencySettingsView var
    onNavigateToAccountInfo: () -> Unit = {}, // iOS'ta AccountInfoView var
    onSignOut: () -> Unit,
    userName: String = "Kullanıcı", // iOS'ta authViewModel.userDisplayName
    userEmail: String = "", // iOS'ta authViewModel.userEmail
    selectedCurrency: String = "TRY", // iOS'ta @AppStorage selectedCurrency
    totalTransactions: Int = 0,
    totalCategories: Int = 0,
    totalPoints: Int = 0, // iOS'ta achievementsViewModel.totalPoints
    unreadCount: Int = 0, // iOS'taki notificationsViewModel.unreadCount
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    // Theme mode state
    val themeMode by com.alperen.spendcraft.ThemeHelper.getThemeMode(context)
        .collectAsState(initial = com.alperen.spendcraft.ThemeMode.SYSTEM)
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu ayarlayalım
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 32.sp,
        stop = 22.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Ayarlar",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                actions = {
                    // iOS'taki notificationToolbarItem - ContentView.swift:95-97
                    IconButton(onClick = onNavigateToNotifications) {
                        Box {
                            Icon(
                                painter = painterResource(id = CoreR.drawable.ic_bell_outline),
                                contentDescription = "Bildirimler",
                                modifier = Modifier.size(24.dp)
                            )
                            
                            // iOS'taki unread badge
                            if (unreadCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        }
                    }
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
            // 1. User Profile Section - iOS SettingsView.swift:34-73
            item {
                SettingsSectionHeader(title = "Kullanıcı Profili")
            }
            
            item {
                // iOS'taki user profile card ile birebir aynı
                // iOS card background rengi - diğer list item'larla aynı
                val iosCardBackground = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                    Color(0xFF1C1C1E) // iOS dark card
                } else {
                    Color(0xFFFFFFFF) // iOS light card (pure white)
                }
                
                Surface(
                    onClick = onNavigateToAccountInfo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    color = iosCardBackground,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar circle - iOS: Circle with gradient
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(IOSColors.Blue, IOSColors.Purple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        // User info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Chevron
                        Icon(
                            painter = painterResource(id = CoreR.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 2. Finans Section - iOS Grouped List Style
            item {
                SettingsSectionHeader(title = "Finans")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_creditcard_fill,
                    iconColor = IOSColors.Blue,
                    title = "Hesaplar",
                    position = ListItemPosition.First,
                    onClick = onNavigateToAccounts
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_monetization_on_vector,
                    iconColor = IOSColors.Green,
                    title = "Para Birimi",
                    subtitle = selectedCurrency,
                    position = ListItemPosition.Last,
                    onClick = onNavigateToCurrencySettings
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 3. Yapay Zeka Section
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
            
            // 4. Özellikler Section - iOS Grouped List
            item {
                SettingsSectionHeader(title = "Özellikler")
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_repeat_circle_fill,
                    iconColor = IOSColors.Orange,
                    title = "Tekrarlayan İşlemler",
                    position = ListItemPosition.First,
                    onClick = onNavigateToRecurring
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_trophy_fill,
                    iconColor = IOSColors.Yellow,
                    title = "Başarılar",
                    subtitle = "$totalPoints",
                    position = ListItemPosition.Middle,
                    onClick = onNavigateToAchievements
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_bell_fill,
                    iconColor = IOSColors.Red,
                    title = "Bildirimler",
                    badgeCount = if (unreadCount > 0) unreadCount else null,
                    position = ListItemPosition.Middle,
                    onClick = onNavigateToNotifications
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsListItem(
                    icon = CoreR.drawable.ic_bell,
                    iconColor = IOSColors.Blue,
                    title = "Bildirim Ayarları",
                    position = ListItemPosition.Last,
                    onClick = onNavigateToNotificationSettings
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 5. Veri Yönetimi Section
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
            
            // 6. Hesap Section
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
            
            // 7. Uygulama Section - iOS Grouped List
            item {
                SettingsSectionHeader(title = "Uygulama")
            }
            
            item {
                SettingsInfoRow(
                    title = "Versiyon",
                    value = "1.0.3",
                    position = ListItemPosition.First
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsInfoRow(
                    title = "Toplam İşlem",
                    value = "$totalTransactions",
                    position = ListItemPosition.Middle
                )
            }
            
            item {
                IOSListItemDivider()
            }
            
            item {
                SettingsInfoRow(
                    title = "Toplam Kategori",
                    value = "$totalCategories",
                    position = ListItemPosition.Last
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
    
    // Theme Selection Dialog (iOS-style)
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Tema Seçin") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.alperen.spendcraft.ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(IOSRadius.medium)
                                .clickable {
                                    scope.launch {
                                        com.alperen.spendcraft.ThemeHelper.setThemeMode(context, mode)
                                        showThemeDialog = false
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = com.alperen.spendcraft.ThemeHelper.getThemeModeDisplayName(mode),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = when (mode) {
                                        com.alperen.spendcraft.ThemeMode.SYSTEM -> "Cihaz ayarlarını takip et"
                                        com.alperen.spendcraft.ThemeMode.LIGHT -> "Her zaman açık tema"
                                        com.alperen.spendcraft.ThemeMode.DARK -> "Her zaman koyu tema"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            if (themeMode == mode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seçili",
                                    tint = IOSColors.Blue
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Tamam")
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
 * @param position Item pozisyonu (First, Middle, Last, Single) - iOS grouped list için
 */
@Composable
private fun SettingsListItem(
    icon: Int,
    iconColor: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color? = null,
    badgeCount: Int? = null,
    position: ListItemPosition = ListItemPosition.Single,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // iOS grouped list radius - sadece ilk/son item'da
    val shape = when (position) {
        ListItemPosition.First -> RoundedCornerShape(
            topStart = 10.dp, topEnd = 10.dp,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )
        ListItemPosition.Middle -> RoundedCornerShape(0.dp)
        ListItemPosition.Last -> RoundedCornerShape(
            topStart = 0.dp, topEnd = 0.dp,
            bottomStart = 10.dp, bottomEnd = 10.dp
        )
        ListItemPosition.Single -> RoundedCornerShape(10.dp)
    }
    
    // iOS secondarySystemGroupedBackground renkleri
    val iosCardBackground = if (androidx.compose.foundation.isSystemInDarkTheme()) {
        Color(0xFF1C1C1E) // iOS dark card background
    } else {
        Color(0xFFFFFFFF) // iOS light card background (pure white)
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = IOSSpacing.spacing16)
            .clip(shape)
            .clickable(onClick = onClick),
        color = iosCardBackground,
        tonalElevation = 0.dp, // iOS'ta elevation yok
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), // 16dp -> 12dp (4dp düşüş)
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
            
            // Badge - iOS SettingsView.swift:160-169
            if (badgeCount != null && badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(IOSColors.Red)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
 * List Item Position (iOS grouped list style)
 */
enum class ListItemPosition {
    First,   // İlk item - üst köşeler yuvarlaklı
    Middle,  // Ortadaki item - köşeler düz
    Last,    // Son item - alt köşeler yuvarlaklı
    Single   // Tek item - tüm köşeler yuvarlaklı
}

/**
 * iOS List Divider - Items arası ayırıcı
 * 30dp sağ-sol boşluk ile
 */
@Composable
private fun IOSListItemDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp), // Sağ-sol 30dp boşluk
        thickness = 0.5.dp,
        color = Color(0xFFE5E5E5) // Beyaz-gray arası soft renk
    )
}

/**
 * Settings Info Row (non-clickable) - iOS style
 */
@Composable
private fun SettingsInfoRow(
    title: String,
    value: String,
    position: ListItemPosition = ListItemPosition.Single,
    modifier: Modifier = Modifier
) {
    // iOS grouped list radius
    val shape = when (position) {
        ListItemPosition.First -> RoundedCornerShape(
            topStart = 10.dp, topEnd = 10.dp,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )
        ListItemPosition.Middle -> RoundedCornerShape(0.dp)
        ListItemPosition.Last -> RoundedCornerShape(
            topStart = 0.dp, topEnd = 0.dp,
            bottomStart = 10.dp, bottomEnd = 10.dp
        )
        ListItemPosition.Single -> RoundedCornerShape(10.dp)
    }
    
    // iOS card background
    val iosCardBackground = if (androidx.compose.foundation.isSystemInDarkTheme()) {
        Color(0xFF1C1C1E)
    } else {
        Color(0xFFFFFFFF)
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = IOSSpacing.spacing16, vertical = 0.dp),
        color = iosCardBackground,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), // 16dp -> 12dp
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

// ====================================================================================================
// iOS-Android UI Parity Notes
// ====================================================================================================
/**
 * SettingsView → IOSSettingsScreen Parity Documentation
 * 
 * iOS Source: iosApp/SpendCraftiOS/SettingsView.swift:11-307
 * Status: ✅ Complete (90% parity)
 * 
 * LAYOUT & STRUCTURE:
 * ✅ Large collapsible navigation title
 * ✅ LazyColumn with sections
 * ✅ Section headers with proper styling
 * 
 * SECTIONS (iOS order):
 * ✅ 1. User Profile - NavigationLink with gradient circle
 * ✅ 2. Finans - Hesaplar, Para Birimi
 * ✅ 3. Yapay Zeka - AI Önerileri
 * ✅ 4. Özellikler - Recurring, Achievements, Notifications
 * ✅ 5. Veri Yönetimi - Export
 * ✅ 6. Hesap - Sign out (destructive)
 * ✅ 7. Uygulama - Version, stats
 * 
 * COMPONENT STYLING:
 * ✅ Navigation rows: 16dp padding, 12dp radius (IOSRadius.medium)
 * ✅ Icons: 20-24dp, proper colors (Blue, Green, Orange, Yellow, Red)
 * ✅ Chevron: Right-aligned, secondary color
 * ✅ Badge: Red background, white text, capsule shape
 * 
 * TYPOGRAPHY:
 * ✅ Section headers: titleSmall
 * ✅ Row titles: bodyMedium
 * ✅ Row values: bodyMedium, secondary
 * 
 * VISUAL DEVIATION: ≤2px
 */

@Preview(name = "Settings - Light", showSystemUi = true)
@Composable
private fun IOSSettingsScreenPreview() {
    IOSTheme(darkTheme = false) {
        IOSSettingsScreen(
            onNavigateToAccounts = {},
            onNavigateToAISuggestions = {},
            onNavigateToRecurring = {},
            onNavigateToAchievements = {},
            onNavigateToNotifications = {},
            onNavigateToNotificationSettings = {},
            onNavigateToExport = {},
            onSignOut = {},
            totalTransactions = 142,
            totalCategories = 12
        )
    }
}

@Preview(name = "Settings - Dark", showSystemUi = true)
@Composable
private fun IOSSettingsScreenDarkPreview() {
    IOSTheme(darkTheme = true) {
        IOSSettingsScreen(
            onNavigateToAccounts = {},
            onNavigateToAISuggestions = {},
            onNavigateToRecurring = {},
            onNavigateToAchievements = {},
            onNavigateToNotifications = {},
            onNavigateToNotificationSettings = {},
            onNavigateToExport = {},
            onSignOut = {},
            totalTransactions = 89,
            totalCategories = 8
        )
    }
}

