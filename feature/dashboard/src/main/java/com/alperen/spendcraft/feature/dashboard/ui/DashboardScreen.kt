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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
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
    currentBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    currentStreak: Int = 0,
    longestStreak: Int = 0,
    achievementsCount: Int = 0,
    totalPoints: Int = 0,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onNotifications: () -> Unit = {},
    onAchievements: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = MaterialTheme.extendedColors
    
    // Son 5 işlem
    val recentTransactions = remember(transactions) {
        transactions.sortedByDescending { it.timestampUtcMillis }.take(5)
    }
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Ana Sayfa",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Bildirim ikonu
                    IconButton(onClick = onNotifications) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_bell),
                            contentDescription = "Bildirimler",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
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
            
            // 4. Streak Card
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
                    onAchievements = onAchievements,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 7. Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = recentTransactions,
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
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        IOSColors.Blue.copy(alpha = 0.1f),
                        IOSColors.Purple.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(vertical = 16.dp)
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
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.income
            ),
            shape = RoundedCornerShape(15.dp)
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
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.expense
            ),
            shape = RoundedCornerShape(15.dp)
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
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .padding(16.dp)
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
            .clip(RoundedCornerShape(15.dp))
            .background(extendedColors.streak.copy(alpha = 0.1f))
            .padding(16.dp)
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
 * Achievements Section
 */
@Composable
private fun AchievementsSection(
    achievementsCount: Int,
    totalPoints: Int,
    onAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
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
        
        // Achievement Cards Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.extendedColors.achievement.copy(alpha = 0.1f))
                .clickable(onClick = onAchievements)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_trophy_fill),
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.achievement,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "$achievementsCount Başarı",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tümünü görmek için tıklayın",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Recent Transactions Section
 */
@Composable
private fun RecentTransactionsSection(
    transactions: List<Transaction>,
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
                TransactionRow(transaction = transaction)
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
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon (placeholder for now)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(IOSColors.Blue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = IOSColors.Blue,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Transaction Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "İşlem", // TODO: Kategori adı eklenecek
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

