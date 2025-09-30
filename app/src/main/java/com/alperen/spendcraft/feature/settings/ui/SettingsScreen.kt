package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.CurrencyHelper
import com.alperen.spendcraft.ThemeHelper
import com.alperen.spendcraft.core.model.Category
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.R
import com.alperen.spendcraft.core.ui.R as CoreR
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    categories: List<Category> = emptyList(),
    onAddCategory: (String) -> Unit = {},
    onDeleteCategory: (Long) -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onNavigateToPremiumDebug: () -> Unit = {},
    onNavigateToAISuggestions: () -> Unit = {},
    onNavigateToAISettings: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToRecurring: () -> Unit = {},
    onNavigateToSharing: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = false)
    
    // Premium state - TODO: Implement premium state integration
    val isPremium = false
    var currency by rememberSaveable { mutableStateOf(CurrencyHelper.getCurrency(context)) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var languageExpanded by rememberSaveable { mutableStateOf(false) }
    
    // Para birimleri ve sembolleri
    val currencies = remember { CurrencyHelper.currencies.toList() }
    
    // Dil seçenekleri
    val languages = remember { 
        listOf(
            "🇹🇷 Türkçe" to "tr", 
            "🇺🇸 English" to "en"
        ) 
    }
    
    val currentLanguage = LocaleHelper.getLanguage(context)
    var selectedLanguage by rememberSaveable { 
        mutableStateOf(languages.find { it.second == currentLanguage }?.first ?: "🇺🇸 English")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AdMob Banner
            item {
                com.alperen.spendcraft.core.ui.AdMobBannerWithPadding(isPremium = isPremium)
            }
            
            // Language Selection
            item {
                Column {
                    Text(
                        text = "🌍 ${stringResource(R.string.language_selection)}",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "${stringResource(CoreR.string.current_settings)}: $selectedLanguage",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                languages.forEach { (displayName, languageCode) ->
                                    Button(
                                        onClick = {
                                            selectedLanguage = displayName
                                            LocaleHelper.setLocale(context, languageCode)
                                            // Restart activity to apply language change
                                            (context as? android.app.Activity)?.recreate()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = if (selectedLanguage == displayName) {
                                            ButtonDefaults.buttonColors(
                                                containerColor = SpendCraftTheme.colors.primary
                                            )
                                        } else {
                                            ButtonDefaults.buttonColors(
                                                containerColor = SpendCraftTheme.colors.surfaceVariant
                                            )
                                        }
                                    ) {
                                        Text(displayName)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Budget Management
            item {
                Column {
                    Text(
                        text = "💰 ${stringResource(R.string.budget_management)}",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.set_budget_limits),
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stringResource(R.string.monthly_budget),
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToBudgets) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(R.string.budget_management),
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Management
            item {
                Column {
                    Text(
                        text = "📂 ${stringResource(CoreR.string.category_management)}",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(CoreR.string.manage_categories),
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${categories.size} ${stringResource(CoreR.string.categories_available)}",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToCategories) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(CoreR.string.category_management),
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Currency Selection
            item {
                Column {
                    Text(
                        text = "💰 ${stringResource(CoreR.string.currency_selection)}",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "${stringResource(CoreR.string.current_settings)}: ${currencies.find { it.first == currency }?.let { "${it.second} ${it.first}" } ?: currency}",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                currencies.forEach { (currencyCode, symbol) ->
                                    Button(
                                        onClick = {
                                            currency = currencyCode
                                            CurrencyHelper.setCurrency(context, currencyCode)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = if (currency == currencyCode) {
                                            ButtonDefaults.buttonColors(
                                                containerColor = SpendCraftTheme.colors.primary
                                            )
                                        } else {
                                            ButtonDefaults.buttonColors(
                                                containerColor = SpendCraftTheme.colors.surfaceVariant
                                            )
                                        }
                                    ) {
                                        Text(
                                            text = "$symbol $currencyCode",
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dark Mode
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SpendCraftTheme.colors.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌙", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(CoreR.string.dark_mode),
                                style = SpendCraftTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(CoreR.string.switch_to_dark_theme),
                                style = SpendCraftTheme.typography.bodySmall,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { 
                                coroutineScope.launch {
                                    ThemeHelper.setDarkMode(context, it)
                                }
                            }
                        )
                    }
                }
            }

            // AI Features
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SpendCraftTheme.colors.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🤖 AI Özellikleri",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToAISuggestions,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("AI Önerileri")
                            }
                            
                            Button(
                                onClick = onNavigateToAISettings,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("AI Ayarları")
                            }
                        }
                    }
                }
            }

            // New Features Section
            item {
                Column {
                    Text(
                        text = "🚀 Yeni Özellikler",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Dashboard
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_dashboard_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = SpendCraftTheme.colors.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Dashboard",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Finansal durumunuzu görün",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToDashboard) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Dashboard",
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }

                            // Accounts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_account_balance_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = SpendCraftTheme.colors.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Hesaplar",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Çoklu hesap yönetimi",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToAccounts) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Hesaplar",
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }

                            // Recurring Transactions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_repeat_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = SpendCraftTheme.colors.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tekrarlayan İşlemler",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Otomatik işlem oluşturma",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToRecurring) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Tekrarlayan İşlemler",
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }

                            // Sharing (Premium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_share_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isPremium) SpendCraftTheme.colors.primary else SpendCraftTheme.colors.onSurfaceVariant
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Aile/Ortak Bütçe",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isPremium) SpendCraftTheme.colors.onSurface else SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isPremium) "Bütçeyi paylaşın" else "Premium özellik",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToSharing) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Paylaşım",
                                        tint = if (isPremium) SpendCraftTheme.colors.primary else SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notifications & Achievements
            item {
                Column {
                    Text(
                        text = "🔔 Bildirimler & Başarımlar",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SpendCraftTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Notifications
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_notifications_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = SpendCraftTheme.colors.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Bildirimler",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Uyarılar ve hatırlatmalar",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToNotifications) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Bildirimler",
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }

                            // Achievements
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_trophy_vector),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = SpendCraftTheme.colors.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Başarımlar",
                                        style = SpendCraftTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Rozetler ve seviyeler",
                                        style = SpendCraftTheme.typography.bodyMedium,
                                        color = SpendCraftTheme.colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = onNavigateToAchievements) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Başarımlar",
                                        tint = SpendCraftTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Debug Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SpendCraftTheme.colors.errorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔧", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Premium Debug",
                                style = SpendCraftTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendCraftTheme.colors.onErrorContainer
                            )
                            Text(
                                text = "Premium durumunu test et",
                                style = SpendCraftTheme.typography.bodySmall,
                                color = SpendCraftTheme.colors.onErrorContainer
                            )
                        }
                        IconButton(onClick = onNavigateToPremiumDebug) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Premium Debug",
                                tint = SpendCraftTheme.colors.onErrorContainer
                            )
                        }
                    }
                }
            }

        }
    }
}
//