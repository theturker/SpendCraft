package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAdd: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val totalAmount = items.sumOf {
        if (it.type == TransactionType.INCOME) it.amount.minorUnits else -it.amount.minorUnits
    }
    
    val incomeAmount = items.filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount.minorUnits }
    val expenseAmount = items.filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount.minorUnits }
    
    AppScaffold(
        title = "💳 SpendCraft",
        actions = {
            IconButton(onClick = onReports) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Reports")
            }
            IconButton(onClick = onSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
        fab = { ModernFab(onClick = onAdd) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                AnimatedBalanceCard(
                    balance = formatMinor(totalAmount),
                    income = formatMinor(incomeAmount),
                    expenses = formatMinor(expenseAmount)
                )
            }
            
            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IncomeExpenseButton(
                        text = "💰 Gelir Ekle",
                        onClick = onAddIncome,
                        isIncome = true,
                        modifier = Modifier.weight(1f)
                    )
                    IncomeExpenseButton(
                        text = "💸 Gider Ekle",
                        onClick = onAddExpense,
                        isIncome = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Statistics Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Bu Ay",
                        value = "${items.size} işlem",
                        icon = Icons.Filled.Call,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Ortalama",
                        value = if (items.isNotEmpty()) formatMinor(totalAmount / items.size) else "₺0",
                        icon = Icons.Filled.KeyboardArrowUp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Son İşlemler",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onReports) {
                        Text("Tümünü Gör")
                    }
                }
            }
            
            // Transactions List
            if (items.isEmpty()) {
                item {
                    ModernCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz işlem yok",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "İlk işleminizi ekleyerek başlayın",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(items.take(5)) { tx ->
                    ModernTransactionRow(
                        tx = tx,
                        onDelete = { tx.id?.let(viewModel::deleteTransaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernTransactionRow(tx: Transaction, onDelete: () -> Unit) {
    val context = LocalContext.current
    val amount = if (tx.type == TransactionType.INCOME) tx.amount.minorUnits else -tx.amount.minorUnits
    val isIncome = tx.type == TransactionType.INCOME
    
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon with background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isIncome) 
                                MaterialTheme.colorScheme.secondaryContainer 
                            else 
                                MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = if (isIncome) 
                            MaterialTheme.colorScheme.onSecondaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = if (isIncome) "💰 Gelir" else "💸 Gider",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tx.note ?: "Açıklama yok",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMinor(amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) 
                        MaterialTheme.colorScheme.secondary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatMinor(minor: Long): String {
    val sign = if (minor < 0) "-" else ""
    val abs = kotlin.math.abs(minor)
    val major = abs / 100
    val cents = abs % 100
    return "$sign$major.${cents.toString().padStart(2, '0')}"
}