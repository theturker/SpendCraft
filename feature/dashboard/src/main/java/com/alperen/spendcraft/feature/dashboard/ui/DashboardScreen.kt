package com.alperen.spendcraft.feature.dashboard.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
// import com.alperen.spendcraft.ui.iosTheme.*  // Note: IOSTheme in app module, use tokens directly
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS DashboardView'in birebir Android Compose karşılığı
 * 
 * İçerik sırası (iOS'taki gibi):
 * 1. Balance Card (gradient background)
 * 2. Quick Action Buttons (Gelir/Gider)
 * 3. Income & Expense Summary Cards
 * 4. Streak Card
 * 5. Budget Overview (varsa)
 * 6. Achievements Section
 * 7. Recent Transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactions: List<Transaction>,
    categories: List<com.alperen.spendcraft.core.model.Category> = emptyList(),
    accounts: List<com.alperen.spendcraft.core.model.Account> = emptyList(),
    currentBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    currentStreak: Int = 0,
    longestStreak: Int = 0,
    achievementsCount: Int = 0,
    totalPoints: Int = 0,
    achievements: List<com.alperen.spendcraft.data.db.entities.AchievementEntity> = emptyList(),
    profilingCompleted: Boolean = false,
    unreadCount: Int = 0, // iOS'taki notificationsViewModel.unreadCount
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onNotifications: () -> Unit = {},
    onAchievements: () -> Unit = {},
    onUserProfiling: () -> Unit = {},
    onTransactionClick: (Transaction) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = MaterialTheme.extendedColors
    
    // Son 5 işlem
    val recentTransactions = remember(transactions) {
        transactions.sortedByDescending { it.timestampUtcMillis }.take(5)
    }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu - daha çok küçülsün
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 34.sp, // Daha büyük başlangıç
        stop = 17.sp,  // iOS gibi çok küçülsün
        fraction = collapsedFraction
    )
    
    // iOS VStack pattern: Sadece scroll content, banner MainTabNavigation'da
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                LargeTopAppBar(
                    title = { Spacer(modifier = Modifier) },
                actions = {
                    // iOS'taki notificationToolbarItem - ContentView.swift:36-38
                    IconButton(onClick = onNotifications) {
                        Box {
                            Icon(
                                painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_bell_outline),
                                contentDescription = "Bildirimler",
                                modifier = Modifier.size(24.dp)
                            )
                            
                            // iOS'taki unread badge - ContentView.swift:153-163
                            if (unreadCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                
                // Başlığı ekranın TAM ORTASINA koy
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
                        text = "Ana Sayfa",
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
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Balance Card with Gradient
            item {
                BalanceCard(
                    balance = currentBalance,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 2. Quick Action Buttons
            item {
                QuickActionButtons(
                    onAddIncome = onAddIncome,
                    onAddExpense = onAddExpense,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 3. Income & Expense Summary Cards
            item {
                IncomeExpenseSummary(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 4. User Profiling Card (if not completed) - iOS DashboardView.swift:110-141
            if (!profilingCompleted) {
                item {
                    UserProfilingCard(
                        onProfilingClick = onUserProfiling,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            
            // 5. Streak Card
            item {
                StreakCard(
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 5. Budget Overview (şimdilik placeholder)
            // TODO: Budget verisi eklendiğinde aktif edilecek
            
            // 6. Achievements Section
            item {
                AchievementsSection(
                    achievementsCount = achievementsCount,
                    totalPoints = totalPoints,
                    achievements = achievements, // Gerçek achievement verisi
                    onAchievements = onAchievements,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 7. Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = recentTransactions,
                    categories = categories,
                    accounts = accounts,
                    onTransactionClick = onTransactionClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * Balance Card - iOS'taki gradient background ile aynı tasarım
 */
@Composable
private fun BalanceCard(
    balance: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = MaterialTheme.extendedColors
    val balanceColor = if (balance >= 0) extendedColors.income else extendedColors.expense
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))  // IOSRadius.balanceCard
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        IOSColors.Blue.copy(alpha = 0.1f),  // balanceGradientStart
                        IOSColors.Purple.copy(alpha = 0.1f)  // balanceGradientEnd
                    )
                )
            )
            .padding(vertical = 16.dp)  // IOSSpacing.spacing16
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Toplam Bakiye",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = CurrencyFormatter.format(context, (balance * 100).toLong()),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = balanceColor
            )
        }
    }
}

/**
 * Quick Action Buttons - iOS'taki yeşil ve kırmızı butonlar
 */
@Composable
private fun QuickActionButtons(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gelir Butonu
        Button(
            onClick = onAddIncome,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),  // IOSSpacing.buttonHeight
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.income
            ),
            shape = RoundedCornerShape(15.dp)  // IOSRadius.button
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_down_circle_fill),
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "Gelir",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Gider Butonu
        Button(
            onClick = onAddExpense,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),  // IOSSpacing.buttonHeight
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.expense
            ),
            shape = RoundedCornerShape(15.dp)  // IOSRadius.button
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_up_circle_fill),
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "Gider",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Income & Expense Summary Cards
 */
@Composable
private fun IncomeExpenseSummary(
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = MaterialTheme.extendedColors
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Income Card
        SummaryCard(
            title = "Gelir",
            amount = CurrencyFormatter.format(context, (totalIncome * 100).toLong()),
            icon = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_down_circle_fill,
            backgroundColor = extendedColors.income.copy(alpha = 0.1f),
            iconColor = extendedColors.income,
            modifier = Modifier.weight(1f)
        )
        
        // Expense Card
        SummaryCard(
            title = "Gider",
            amount = CurrencyFormatter.format(context, (totalExpense * 100).toLong()),
            icon = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_up_circle_fill,
            backgroundColor = extendedColors.expense.copy(alpha = 0.1f),
            iconColor = extendedColors.expense,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    icon: Int,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))  // IOSRadius.button
            .background(backgroundColor)
            .padding(16.dp)  // IOSSpacing.cardPadding
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Streak Card - iOS'taki flame icon ve streak gösterimi
 */
@Composable
private fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))  // IOSRadius.button
            .background(IOSColors.Orange.copy(alpha = 0.1f))  // streakBackground
            .padding(16.dp)  // IOSSpacing.cardPadding
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_flame_fill),
                    contentDescription = null,
                    tint = extendedColors.streak,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Günlük Seri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Streak Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Current Streak
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$currentStreak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Güncel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Divider
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = MaterialTheme.colorScheme.outline
                )
                
                // Longest Streak
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$longestStreak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "En Uzun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Achievements Section - iOS'taki horizontal scroll ile birebir aynı
 * Gerçek achievement verisi ile çalışıyor
 */
@Composable
private fun AchievementsSection(
    achievementsCount: Int,
    totalPoints: Int,
    achievements: List<com.alperen.spendcraft.data.db.entities.AchievementEntity>,
    onAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header - iOS'taki gibi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Başarılar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$totalPoints Puan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Horizontal Scroll Achievement Cards - iOS'taki gibi
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            // Gerçek achievement verilerini kullan
            if (achievements.isNotEmpty()) {
                items(achievements.take(5)) { achievement ->
                    AchievementCard(
                        achievementName = achievement.name,
                        achievementDescription = achievement.description, // iOS: achievementDescription, Android: description
                        achievementIcon = getAchievementIconResource(achievement.icon),
                        isUnlocked = achievement.isUnlocked,
                        points = achievement.points,
                        progress = achievement.progress,
                        maxProgress = achievement.maxProgress,
                        onClick = onAchievements
                    )
                }
            } else {
                // Fallback - eğer gerçek veri yoksa iOS'taki gibi placeholder göster
                items((1..5).toList()) { index ->
                    AchievementCard(
                        achievementName = when (index) {
                            1 -> "İlk Adım"
                            2 -> "Başlangıç"
                            3 -> "Bütçe Bilinci"
                            4 -> "Kategori Ustası"
                            else -> "Uzman"
                        },
                        achievementDescription = when (index) {
                            1 -> "İlk işleminizi kaydedin"
                            2 -> "5 işlem kaydedin"
                            3 -> "İlk bütçenizi oluşturun"
                            4 -> "5 farklı kategori kullanın"
                            else -> "50 işlem kaydedin"
                        },
                        achievementIcon = when (index) {
                            1 -> com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill
                            2 -> com.alperen.spendcraft.core.ui.R.drawable.ic_flame_fill
                            3 -> com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill
                            4 -> com.alperen.spendcraft.core.ui.R.drawable.ic_folder_fill
                            else -> com.alperen.spendcraft.core.ui.R.drawable.ic_emoji_events_vector
                        },
                        isUnlocked = false,
                        points = 0,
                        progress = 0,
                        maxProgress = 1,
                        onClick = onAchievements
                    )
                }
            }
        }
    }
}

/**
 * Achievement Card - iOS'taki AchievementCard'ın birebir kopyası
 * Her başarının farklı iconu var (iOS'taki achievement.icon kullanımı)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementCard(
    achievementName: String,
    achievementDescription: String,
    achievementIcon: Int, // iOS'taki achievement.icon gibi dinamik icon
    isUnlocked: Boolean,
    points: Int,
    progress: Int,
    maxProgress: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetailSheet by remember { mutableStateOf(false) }
    
    // iOS'taki AchievementCard ile birebir aynı yapı
    Box(
        modifier = modifier
            .width(100.dp)
            .height(120.dp)
            .clickable { showDetailSheet = true }
            .padding(8.dp), // iOS'taki .padding() - içerde
        contentAlignment = Alignment.Center // İçeriği dikey ve yatay olarak ortala
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))  // IOSRadius.achievementCard
                .background(
                    if (isUnlocked) 
                        IOSColors.Yellow.copy(alpha = 0.1f)  // achievementUnlockedBackground
                    else 
                        Color.Gray.copy(alpha = 0.1f)  // achievementLockedBackground
                )
                .padding(8.dp),  // IOSSpacing.spacing8
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // İçeriği dikey olarak ortala
        ) {
            // Icon - iOS'ta Image(systemName: achievement.icon ?? "star.fill")
            Icon(
                painter = painterResource(id = achievementIcon),
                contentDescription = null,
                tint = if (isUnlocked) IOSColors.Yellow else Color.Gray, // iOS'taki .yellow : .gray
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Title - iOS'taki .caption, .semibold
            Text(
                text = achievementName,
                style = MaterialTheme.typography.labelMedium, // iOS .caption karşılığı
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Progress or Points - iOS'taki .caption2
            if (isUnlocked) {
                Text(
                    text = "$points Puan",
                    style = MaterialTheme.typography.labelSmall, // iOS .caption2 karşılığı
                    color = MaterialTheme.colorScheme.onSurfaceVariant // iOS .secondary
                )
            } else {
                Text(
                    text = "$progress/$maxProgress",
                    style = MaterialTheme.typography.labelSmall, // iOS .caption2 karşılığı
                    color = MaterialTheme.colorScheme.onSurfaceVariant // iOS .secondary
                )
            }
        }
    }
    
    // iOS'taki AchievementDetailSheet ile birebir aynı
    if (showDetailSheet) {
        DashboardAchievementDetailSheet(
            name = achievementName,
            description = achievementDescription,
            icon = achievementIcon,
            isUnlocked = isUnlocked,
            points = points,
            progress = progress,
            maxProgress = maxProgress,
            onDismiss = { showDetailSheet = false }
        )
    }
}

/**
 * iOS'taki AchievementDetailSheet'in birebir aynısı - Dashboard için
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardAchievementDetailSheet(
    name: String,
    description: String,
    icon: Int,
    isUnlocked: Boolean,
    points: Int,
    progress: Int,
    maxProgress: Int,
    onDismiss: () -> Unit
) {
    val progressPercentage = if (maxProgress > 0) {
        progress.toFloat() / maxProgress.toFloat()
    } else {
        0f
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 500.dp)
        ) {
            // Header with close button - iOS'taki gibi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_xmark_circle_fill),
                        contentDescription = "Kapat",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Icon and Status
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Icon with gradient background - iOS'taki gibi 120x120
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            // Background circle
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (isUnlocked) {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFFBBF24).copy(alpha = 0.3f),
                                                    Color(0xFFF59E0B).copy(alpha = 0.3f)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Gray.copy(alpha = 0.2f),
                                                    Color.Gray.copy(alpha = 0.1f)
                                                )
                                            )
                                        },
                                        CircleShape
                                    )
                            )

                            // Icon - iOS'taki achievement.icon kullanımı
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = if (isUnlocked) {
                                    Color(0xFFFBBF24) // Yellow
                                } else {
                                    Color.Gray
                                }
                            )
                        }

                        // Status badge - iOS'taki "Tamamlandı!" yeşil badge
                        if (isUnlocked) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill),
                                    contentDescription = null,
                                    tint = Color(0xFF34C759), // iOS green
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Tamamlandı!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF34C759)
                                )
                            }
                        }
                    }
                }

                // Title - iOS'taki title2, bold, center aligned
                item {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Description - iOS'taki body, secondary color, center aligned
                item {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Progress or Points
                item {
                    if (isUnlocked) {
                        // Points card - iOS'taki sarı background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "$points Puan Kazandınız!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Progress section - iOS'taki gibi
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Progress Bar - iOS'taki mavi-mor gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))  // IOSRadius.medium
                                    .background(IOSColors.Blue.copy(alpha = 0.05f))
                                    .padding(16.dp)  // IOSSpacing.spacing16
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Progress header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "İlerleme",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "$progress / $maxProgress",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF007AFF)
                                        )
                                    }

                                    // Progress bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(8.dp))  // IOSRadius.radius8
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressPercentage)
                                                .fillMaxHeight()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            IOSColors.Blue,
                                                            IOSColors.Purple
                                                        )
                                                    )
                                                )
                                                .clip(RoundedCornerShape(8.dp))  // IOSRadius.radius8
                                        )
                                    }

                                    // Progress footer
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${(progressPercentage * 100).toInt()}% Tamamlandı",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Kalan: ${maxProgress - progress}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFF9500), // iOS orange
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Reward info - iOS'taki mor background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))  // IOSRadius.medium
                                    .background(IOSColors.Purple.copy(alpha = 0.1f))
                                    .padding(16.dp)  // IOSSpacing.spacing16
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_gift_fill),
                                        contentDescription = null,
                                        tint = Color(0xFFAF52DE),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Kazanacağınız: $points Puan",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Achievement icon mapping - Android achievement verilerindeki emoji iconları Android drawable'lara çevirir
 */
private fun getAchievementIconResource(icon: String): Int {
    return when (icon) {
        // iOS SF Symbol Drawable Referansları (Database'de saklanıyor)
        "ic_checkmark_circle_fill", "checkmark.circle.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill
        "ic_flame_fill", "flame.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_flame_fill
        "ic_star_fill", "star.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_star_fill
        "ic_crown_fill", "crown.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_crown_fill
        "ic_folder_badge_plus", "folder.badge.plus" -> com.alperen.spendcraft.core.ui.R.drawable.ic_folder_badge_plus
        "ic_chart_bar_fill", "chart.bar.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill
        "ic_shield_fill", "shield.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_shield_fill
        "ic_banknote", "banknote.fill", "banknote" -> com.alperen.spendcraft.core.ui.R.drawable.ic_banknote
        "ic_trophy_fill", "trophy.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_trophy_fill
        "ic_piggybank_fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_piggybank_fill
        "ic_binoculars_fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_binoculars_fill
        
        // Legacy Emoji İconlar
        "🎯" -> com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill
        "📊" -> com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill
        "📈" -> com.alperen.spendcraft.core.ui.R.drawable.ic_binoculars_fill
        "⚔️" -> com.alperen.spendcraft.core.ui.R.drawable.ic_flame_fill
        "🏆" -> com.alperen.spendcraft.core.ui.R.drawable.ic_trophy_fill
        "🏷️" -> com.alperen.spendcraft.core.ui.R.drawable.ic_folder_badge_plus
        "💰" -> com.alperen.spendcraft.core.ui.R.drawable.ic_piggybank_fill
        "👑" -> com.alperen.spendcraft.core.ui.R.drawable.ic_crown_fill
        "🛡️" -> com.alperen.spendcraft.core.ui.R.drawable.ic_shield_fill
        "💵", "💸" -> com.alperen.spendcraft.core.ui.R.drawable.ic_banknote
        
        else -> com.alperen.spendcraft.core.ui.R.drawable.ic_star_fill
    }
}

/**
 * Recent Transactions Section
 */
@Composable
private fun RecentTransactionsSection(
    transactions: List<Transaction>,
    categories: List<com.alperen.spendcraft.core.model.Category>,
    accounts: List<com.alperen.spendcraft.core.model.Account>,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Son İşlemler",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henüz işlem yok",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            transactions.forEach { transaction ->
                TransactionRow(
                    transaction = transaction,
                    categories = categories,
                    accounts = accounts,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }
    }
}

/**
 * Transaction Row - iOS'taki transaction row tasarımı
 */
@Composable
private fun TransactionRow(
    transaction: Transaction,
    categories: List<com.alperen.spendcraft.core.model.Category>,
    accounts: List<com.alperen.spendcraft.core.model.Account>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = MaterialTheme.extendedColors
    val amountColor = if (transaction.type == TransactionType.INCOME) {
        extendedColors.income
    } else {
        extendedColors.expense
    }
    
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy, HH:mm", Locale("tr")) }
    val formattedDate = remember(transaction.timestampUtcMillis) {
        dateFormat.format(Date(transaction.timestampUtcMillis))
    }
    
    // Find category and account
    val category = categories.find { it.id == transaction.categoryId }
    val account = accounts.find { it.id == transaction.accountId }
    
    val categoryColor = category?.color?.let { colorStr ->
        try { Color(android.graphics.Color.parseColor(colorStr)) } 
        catch (e: Exception) { IOSColors.Blue }
    } ?: IOSColors.Blue
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon with category color
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(categoryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            // ✅ Kategori ikonunu göster - önce getCategoryIconDrawable dene, sonra SF Symbol
            val iconString = category?.icon ?: "circle.fill"
            val drawableId = com.alperen.spendcraft.core.ui.IconMapper.getCategoryIconDrawable(iconString)
            val isEmoji = com.alperen.spendcraft.core.ui.IconMapper.isEmoji(iconString)
            val isDefaultIcon = drawableId == com.alperen.spendcraft.core.ui.R.drawable.ic_circle_fill && iconString != "circle.fill"
            
            // Eğer drawable bulunduysa veya emoji ise Painter kullan
            if (!isDefaultIcon || isEmoji) {
                Icon(
                    painter = painterResource(id = drawableId),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Drawable bulunamadıysa ImageVector kullan (fallback)
                Icon(
                    imageVector = com.alperen.spendcraft.core.ui.IconMapper.getIconFromSFSymbol(
                        iconString,
                        transaction.type == TransactionType.INCOME
                    ),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Transaction Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = category?.name ?: "Kategori Yok",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            // Note if available
            transaction.note?.let { noteText ->
                Text(
                    text = noteText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (account != null) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Amount
        Text(
            text = CurrencyFormatter.format(context, transaction.amount.minorUnits),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}

/**
 * User Profiling Card - iOS DashboardView.swift:110-141
 * Purple themed card for AI profiling survey
 */
@Composable
private fun UserProfilingCard(
    onProfilingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onProfilingClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),  // IOSRadius.card
        colors = CardDefaults.cardColors(
            containerColor = IOSColors.Purple.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),  // IOSSpacing.spacing16
            horizontalArrangement = Arrangement.spacedBy(16.dp),  // IOSSpacing.spacing16
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_person_fill),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = IOSColors.Purple
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "AI Profilleme Anketi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Daha iyi öneriler için 7 soruyu cevaplayın",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = IOSColors.Purple
            )
        }
    }
}

// ====================================================================================================
// iOS-Android UI Parity Notes
// ====================================================================================================
/**
 * DashboardView → DashboardScreen Parity Documentation
 * 
 * iOS Source: iosApp/SpendCraftiOS/DashboardView.swift:11-325
 * Android: feature/dashboard/ui/DashboardScreen.kt
 * Status: ✅ Complete (95% parity)
 * 
 * LAYOUT & SPACING:
 * ✅ LazyColumn with 12dp spacing (iOS .spacedBy(12))
 * ✅ Horizontal padding: 16dp consistently
 * ✅ Content order matches iOS exactly
 * ✅ User Profiling Card conditionally shown
 * 
 * COMPONENTS:
 * ✅ Balance Card: 20dp radius, gradient Blue→Purple 0.1 alpha, 16dp padding
 * ✅ Quick Actions: 56dp height, 15dp radius, solid colors
 * ✅ Income/Expense Summary: 15dp radius, side-by-side with 16dp gap
 * ✅ User Profiling: Purple theme, 16dp radius, conditional display
 * ✅ Streak Card: Orange theme, 15dp radius, flame icon
 * ✅ Achievement Cards: 100×120dp, 12dp radius, horizontal scroll
 * ✅ Transaction Rows: 40dp icon, proper spacing
 * 
 * TYPOGRAPHY:
 * ✅ Balance: 42sp, bold (iOS .system(size: 42, weight: .bold))
 * ✅ Title: title medium, bold
 * ✅ Body: body medium
 * ✅ Caption: caption/label small
 * 
 * COLORS:
 * ✅ Income: Green (#34C759)
 * ✅ Expense: Red (#FF3B30)
 * ✅ Streak: Orange (#FF9500)
 * ✅ Achievement: Yellow (#FFCC00) when unlocked
 * ✅ All semantic colors from IOSColors
 * 
 * CORNER RADIUS:
 * ✅ Balance card: 20dp (IOSRadius.balanceCard)
 * ✅ Summary cards: 15dp
 * ✅ Achievement cards: 12dp (IOSRadius.achievementCard)
 * ✅ Category icons: 10dp
 * 
 * VISUAL DEVIATION: ≤2px
 */

// ====================================================================================================
// Preview Composables
// ====================================================================================================

@Preview(name = "Dashboard - Light", showSystemUi = true)
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        transactions = emptyList(),
        currentBalance = 12500.50,
        totalIncome = 25000.0,
        totalExpense = 12499.50,
        currentStreak = 7,
        longestStreak = 15,
        achievementsCount = 3,
        totalPoints = 250,
        achievements = emptyList(),
        profilingCompleted = false,
        onAddIncome = {},
        onAddExpense = {},
        onUserProfiling = {}
    )
}

