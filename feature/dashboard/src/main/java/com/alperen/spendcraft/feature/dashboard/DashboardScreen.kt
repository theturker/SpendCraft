package com.alperen.spendcraft.feature.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactionsFlow: StateFlow<List<Transaction>>,
    categoriesFlow: StateFlow<List<com.alperen.spendcraft.core.model.Category>>,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onBack: () -> Unit = {}
) {
    val transactions by transactionsFlow.collectAsState()
    val categories by categoriesFlow.collectAsState()
    
    // Premium state - Get from parameter
    val isPremium = false // TODO: Pass from parent
    
    // Analytics data
    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
    val netBalance = totalIncome - totalExpense
    
    // Recent transactions
    val recentTransactions = transactions.take(5)
    
    // Top categories
    val topCategories = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount.minorUnits } }
        .entries.sortedByDescending { it.value }
        .take(3)
        .map { (categoryId, amount) ->
            val categoryName = categories.find { it.id == categoryId }?.name ?: "Bilinmeyen"
            categoryName to amount
        }

    AppScaffold(
        title = "Dashboard",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome Card with Gradient
            item {
                ModernCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hoş Geldiniz",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Finansal durumunuzu kontrol edin",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_dashboard_vector),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Balance Overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Toplam Gelir",
                        value = formatCurrency(totalIncome),
                        icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_income_vector),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Toplam Gider",
                        value = formatCurrency(totalExpense),
                        icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_expense_vector),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Net Balance Card
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Net Bakiye",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatCurrency(netBalance),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) 
                                MaterialTheme.colorScheme.secondary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Quick Actions
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Hızlı Erişim",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                QuickActionCard(
                                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector),
                                    title = "Raporlar",
                                    onClick = onNavigateToReports
                                )
                            }
                            item {
                                QuickActionCard(
                                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
                                    title = "AI Önerileri",
                                    onClick = onNavigateToAI
                                )
                            }
                            item {
                                QuickActionCard(
                                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
                                    title = "Hesaplar",
                                    onClick = onNavigateToAccounts
                                )
                            }
                            item {
                                QuickActionCard(
                                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_settings_vector),
                                    title = "Ayarlar",
                                    onClick = onNavigateToSettings
                                )
                            }
                        }
                    }
                }
            }

            // Top Categories
            if (topCategories.isNotEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "En Çok Harcanan Kategoriler",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            topCategories.forEachIndexed { index, (categoryName, amount) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    when (index) {
                                                        0 -> MaterialTheme.colorScheme.primary
                                                        1 -> MaterialTheme.colorScheme.secondary
                                                        else -> MaterialTheme.colorScheme.tertiary
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        Text(
                                            text = categoryName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = formatCurrency(amount),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                
                                if (index < topCategories.lastIndex) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions
            if (recentTransactions.isNotEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Son İşlemler",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            recentTransactions.forEach { transaction ->
                                RecentTransactionItem(
                                    transaction = transaction,
                                    categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Bilinmeyen"
                                )
                            }
                        }
                    }
                }
            }

            // AdMob Banner
            item {
                AdMobBannerWithPadding(isPremium = isPremium)
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: Painter,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun RecentTransactionItem(
    transaction: Transaction,
    categoryName: String
) {
    val dateFormatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (transaction.type == TransactionType.INCOME) painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_trending_up_vector) else painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_trending_down_vector),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (transaction.type == TransactionType.INCOME) 
                    MaterialTheme.colorScheme.secondary 
                else 
                    MaterialTheme.colorScheme.error
            )
            Column {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormatter.format(Date(transaction.timestampUtcMillis)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${formatCurrency(transaction.amount.minorUnits)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.INCOME) 
                MaterialTheme.colorScheme.secondary 
            else 
                MaterialTheme.colorScheme.error
        )
    }
}

private fun formatCurrency(amount: Long): String {
    val sign = if (amount < 0) "-" else ""
    val abs = kotlin.math.abs(amount)
    val major = abs / 100
    val cents = abs % 100

    val majorFormatted = major.toString().reversed().chunked(3).joinToString(".").reversed()

    return if (cents > 0) {
        "$sign₺$majorFormatted,${cents.toString().padStart(2, '0')}"
    } else {
        "$sign₺$majorFormatted"
    }
}
