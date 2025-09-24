package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.core.ui.R

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAdd: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onAllTransactions: () -> Unit
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val streak by viewModel.streak.collectAsState()
    var selectedAccountIndex by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingAccountIndex by remember { mutableStateOf(0) }
    
    val totalAmount = items.sumOf {
        if (it.type == TransactionType.INCOME) it.amount.minorUnits else -it.amount.minorUnits
    }
    
    val incomeAmount = items.filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount.minorUnits }
    val expenseAmount = items.filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount.minorUnits }

    // Create account data for cards
    val accountsData = remember(accounts, items) {
        if (accounts.isEmpty()) {
            listOf(
                AccountData(null, "💰 ${context.getString(com.alperen.spendcraft.core.ui.R.string.total_balance)}", formatMinor(totalAmount), formatMinor(incomeAmount), formatMinor(expenseAmount))
            )
        } else {
            accounts.mapIndexed { index, account ->
                val accountTransactions = if (index == 0) items else items.filter { it.accountId == account.id }
                val accountIncome = accountTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
                val accountExpense = accountTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
                val accountBalance = accountIncome - accountExpense
                
                AccountData(
                    id = account.id,
                    name = if (index == 0) "💰 ${account.name}" else "🏠 ${account.name}",
                    balance = formatMinor(accountBalance),
                    income = formatMinor(accountIncome),
                    expenses = formatMinor(accountExpense)
                )
            }
        }
    }
    
    AppScaffold(
        title = "💳 ${stringResource(R.string.app_title)}",
        navigationIcon = {
            IconButton(onClick = onReports) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.reports),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.primary
                )
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
            // Welcome Header with Gradient
            item {
                GradientCard(
                    onClick = { /* Welcome animation */ }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💰 SpendCraft",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.welcome_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
            
            // Balance Card
            item {
                MultiAccountBalanceCard(
                    accounts = accountsData,
                    currentAccountIndex = selectedAccountIndex,
                    onAccountClick = { index -> selectedAccountIndex = index },
                    onEditAccount = { index ->
                        editingAccountIndex = index
                        showEditDialog = true
                    }
                )
            }
            
            // Streak Banner
            item {
                StreakBanner(
                    streak = streak,
                    onClick = {
                        // Focus on QuickAdd - scroll to it or expand it
                    }
                )
            }
            
            
            
            // Quick Actions with Beautiful Cards
            item {
                Column {
                    Text(
                        text = "🚀 Hızlı Ekle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IncomeExpenseButton(
                            text = stringResource(R.string.add_income_button),
                            onClick = onAddIncome,
                            isIncome = true,
                            modifier = Modifier.weight(1f)
                        )
                        IncomeExpenseButton(
                            text = stringResource(R.string.add_expense_button),
                            onClick = onAddExpense,
                            isIncome = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Statistics Cards with Beautiful Design
            item {
                Column {
                    Text(
                        text = "📊 ${stringResource(R.string.transaction_statistics)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.this_month),
                            value = "${items.size} ${stringResource(R.string.transactions_count)}",
                            icon = Icons.Filled.Call,
                            color = Color(0xFF667EEA),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(R.string.average),
                            value = if (items.isNotEmpty()) formatMinor(totalAmount / items.size) else "₺0",
                            icon = Icons.Filled.KeyboardArrowUp,
                            color = Color(0xFFF093FB),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // AdMob Banner
            item {
                AdMobBannerWithPadding()
            }
            
            // Recent Transactions Header with Beautiful Design
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💳 ${stringResource(R.string.recent_transactions)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = onAllTransactions,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.view_all))
                    }
                }
            }
            
            // Transactions List
            if (items.isEmpty()) {
                item {
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = null
                ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_transactions_yet),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.start_adding_transactions),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
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
    
    // Account Name Edit Dialog
    if (showEditDialog && editingAccountIndex < accountsData.size) {
        val editingAccount = accountsData[editingAccountIndex]
        AccountNameEditDialog(
            currentName = editingAccount.name.removePrefix("💰 ").removePrefix("🏠 "),
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                editingAccount.id?.let { accountId ->
                    viewModel.updateAccountName(accountId, newName)
                }
            }
        )
    }
}

@Composable
private fun ModernTransactionRow(tx: Transaction, onDelete: () -> Unit) {
    val context = LocalContext.current
    val amount = if (tx.type == TransactionType.INCOME) tx.amount.minorUnits else -tx.amount.minorUnits
    val isIncome = tx.type == TransactionType.INCOME
    var isHovered by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isHovered) 2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )
    
    ModernCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .rotate(rotation)
            .clickable { 
                isHovered = !isHovered
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Beautiful Icon with Gradient Background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isIncome) 
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF48BB78),
                                        Color(0xFF38A169)
                                    )
                                )
                            else 
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFF56565),
                                        Color(0xFFE53E3E)
                                    )
                                )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isIncome) "💰" else "💸",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = if (isIncome) stringResource(R.string.income) else stringResource(R.string.expense),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tx.note ?: stringResource(R.string.no_description),
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
                        contentDescription = stringResource(R.string.delete),
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