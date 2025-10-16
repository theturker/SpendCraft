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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.alperen.spendcraft.CurrencyHelper
import com.alperen.spendcraft.ThemeHelper
import com.alperen.spendcraft.ThemeMode
import com.alperen.spendcraft.core.model.Category
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.R
import com.alperen.spendcraft.core.ui.R as CoreR
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.alperen.spendcraft.core.ui.ModernCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate

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
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToRecurring: () -> Unit = {},
    onNavigateToSharing: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onBack: () -> Unit,
    // Yeni: header iyileştirmeleri için
    userName: String? = null,
    userAvatar: androidx.compose.ui.graphics.painter.Painter? = null,
    defaultAccountName: String? = null,
    defaultAccountBalance: String? = null,
    onChangeThemeStyle: (String) -> Unit = {},
    isPremium: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val themeMode by ThemeHelper.getThemeMode(context).collectAsState(initial = ThemeMode.SYSTEM)
    val systemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkMode = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    // Premium durumu parent'tan gelir
    var currency by rememberSaveable { mutableStateOf(CurrencyHelper.getCurrency(context)) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var languageExpanded by rememberSaveable { mutableStateOf(false) }
    
    // Para birimleri ve sembolleri
    val currencies = remember { CurrencyHelper.currencies.toList() }
    
    // Dil seçimi şimdilik devre dışı

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
            
            // Gradient Header + Quick Actions
            item {
                ModernCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorScheme.primary.copy(alpha = 0.85f),
                                        colorScheme.tertiary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val infinite = rememberInfiniteTransition(label = "avatar-pulse")
                                val pulse = infinite.animateFloat(
                                    initialValue = 0.96f,
                                    targetValue = 1.04f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1400, easing = LinearEasing),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                    ),
                                    label = "pulse"
                                )
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .scale(pulse.value)
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    colorScheme.primary,
                                                    colorScheme.tertiary
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                ) {
                                    // Cam arkaplan + logo ve ışın efektleri
                                    androidx.compose.material3.Surface(
                                        shape = CircleShape,
                                        color = colorScheme.surface.copy(alpha = 0.65f)
                                    ) {
                                        androidx.compose.foundation.layout.Box(
                                            modifier = Modifier.size(64.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Basit ışın efekti
                                            val rays = rememberInfiniteTransition(label = "rays")
                                            val angle = rays.animateFloat(
                                                initialValue = 0f,
                                                targetValue = 360f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(8000, easing = LinearEasing)
                                                ),
                                                label = "angle"
                                            )
                                            val rayColor = colorScheme.primary.copy(alpha = 0.18f)
                                            Canvas(modifier = Modifier.matchParentSize().padding(6.dp)) {
                                                val c = center
                                                val r = size.minDimension / 2f
                                                rotate(degrees = angle.value) {
                                                    for (i in 0 until 12) {
                                                        val t = i / 12f
                                                        drawLine(
                                                            color = rayColor,
                                                            start = c,
                                                            end = androidx.compose.ui.geometry.Offset(
                                                                x = c.x + r * 0.9f * kotlin.math.cos(2 * Math.PI * t).toFloat(),
                                                                y = c.y + r * 0.9f * kotlin.math.sin(2 * Math.PI * t).toFloat()
                                                            ),
                                                            strokeWidth = 8f,
                                                            cap = StrokeCap.Round
                                                        )
                                                    }
                                                }
                                            }
                                            Icon(
                                                painter = androidx.compose.ui.res.painterResource(R.drawable.ic_app_logo),
                                                contentDescription = null,
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                }
                                Column {
                                    Text(
                                        text = userName ?: stringResource(R.string.settings),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colorScheme.onPrimary
                                    )
                                    if (defaultAccountName != null) {
                                        AssistChip(
                                            onClick = onNavigateToAccounts,
                                            label = { Text("Varsayılan: ${defaultAccountName} • ${defaultAccountBalance ?: ""}") },
                                            leadingIcon = {
                                                Icon(
                                                    painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_account_balance_vector),
                                                    contentDescription = null
                                                )
                                            },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = colorScheme.surface.copy(alpha = 0.85f)
                                            )
                                        )
                                    }
                                }
                            }

                            // Üst kart: sadece profil + varsayılan/para birimi özeti
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    AssistChip(
                                        onClick = {},
                                        enabled = false,
                                        label = {
                                            val current = currencies.find { it.first == currency }
                                            Text("Para birimi: ${current?.second ?: ""} ${current?.first ?: currency}")
                                        },
                                        leadingIcon = {
                                    Icon(
                                        painter = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_wallet_vector),
                                                contentDescription = null
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = colorScheme.surface.copy(alpha = 0.85f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hızlı Ayarlar Izgara (tamamen yeni görünüm)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingTile(
                            title = "Hesaplar",
                            subtitle = "Hesapları yönetin",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_account_balance_vector),
                            onClick = onNavigateToAccounts,
                            modifier = Modifier.weight(1f)
                        )
                        SettingTile(
                            title = "Bütçeler",
                            subtitle = "Limitleri ayarlayın",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_wallet_vector),
                            onClick = onNavigateToBudgets,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingTile(
                            title = "Kategoriler",
                            subtitle = "Kategorileri düzenleyin",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_category_vector),
                            onClick = onNavigateToCategories,
                            modifier = Modifier.weight(1f)
                        )
                        SettingTile(
                            title = "Tekrarlayan",
                            subtitle = "Otomatik işlemler",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_repeat_vector),
                            onClick = onNavigateToRecurring,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingTile(
                            title = "Bildirimler",
                            subtitle = "Uyarıları yönetin",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_notifications_vector),
                            onClick = onNavigateToNotifications,
                            modifier = Modifier.weight(1f)
                        )
                        SettingTile(
                            title = "Başarımlar",
                            subtitle = "Rozetleriniz",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_trophy_vector),
                            onClick = onNavigateToAchievements,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingTile(
                            title = "Paylaşım",
                            subtitle = "Aile bütçesi",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_share_vector),
                            onClick = onNavigateToSharing,
                            modifier = Modifier.weight(1f)
                        )
                        SettingTile(
                            title = "AI Önerileri",
                            subtitle = "İçgörüler",
                            icon = androidx.compose.ui.res.painterResource(CoreR.drawable.ic_ai_spark),
                            onClick = onNavigateToAISuggestions,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Eski kategori/bütçe kartları kaldırıldı
            item { Spacer(Modifier.height(0.dp)) }

            // Para Birimi (inline, tek kart) - Tema stili kaldırıldı
            item {
                ModernCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                            text = "Para Birimi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                        // Para Birimi
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val current = currencies.find { it.first == currency }
                            Text(
                                text = "Seçili: ${current?.second ?: ""} ${current?.first ?: currency}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(currencies) { (currencyCode, symbol) ->
                                    val selected = currency == currencyCode
                                    AssistChip(
                                        onClick = {
                                            currency = currencyCode
                                            CurrencyHelper.setCurrency(context, currencyCode)
                                        },
                                        label = { Text(text = "$symbol $currencyCode") },
                                        leadingIcon = if (selected) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (selected) colorScheme.primaryContainer else colorScheme.surface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dark Mode seçimi şimdilik kaldırıldı

            // Eski AI kartı kaldırıldı
            item { Spacer(Modifier.height(0.dp)) }

            // Eski "Yeni Özellikler" bölümü kaldırıldı
            item { Spacer(Modifier.height(0.dp)) }

            // Eski bildirimler/başarımlar kartları kaldırıldı
            item { Spacer(Modifier.height(0.dp)) }

            // Debug Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Premium durumunu test et",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onErrorContainer
                            )
                        }
                        IconButton(onClick = onNavigateToPremiumDebug) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Premium Debug",
                                tint = colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun SettingTile(
    title: String,
    subtitle: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ModernCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
//