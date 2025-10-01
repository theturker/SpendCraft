package com.alperen.spendcraft.feature.budget.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.R
import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.ActionButton
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.StatCard
import com.alperen.spendcraft.core.premium.PremiumGate
import com.alperen.spendcraft.core.premium.PremiumLockScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagementScreen(
    budgets: List<Budget> = emptyList(),
    categories: List<Category> = emptyList(),
    spentAmounts: Map<String, Long> = emptyMap(),
    isPremium: Boolean = false,
    onAddBudget: (Budget) -> Unit = {},
    onUpdateBudget: (Budget) -> Unit = {},
    onDeleteBudget: (String) -> Unit = {},
    onBack: () -> Unit,
    onNavigateToPaywall: () -> Unit = {},
    onCalculateSpentAmounts: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBudget by remember { mutableStateOf<Budget?>(null) }
    
    LaunchedEffect(Unit) {
        onCalculateSpentAmounts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.budget_management)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { 
                        Text(stringResource(R.string.back)) 
                    }
                },
                actions = {
                    if (isPremium || budgets.size < 3) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_budget)
                            )
                        }
                    } else {
                        IconButton(onClick = { onNavigateToPaywall() }) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Premium özellik"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            PremiumGate(
                isPremium = isPremium,
                premiumContent = {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_budget)
                        )
                    }
                },
                freeContent = {
                    FloatingActionButton(
                        onClick = { 
                            android.util.Log.d("BudgetManagementScreen", "Lock button clicked, navigating to paywall")
                            onNavigateToPaywall() 
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Premium özellik"
                        )
                    }
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
            // Budget Summary
            item {
                BudgetSummaryCard(budgets = budgets)
            }

            // Budget List
            if (budgets.isEmpty()) {
                item {
                    EmptyBudgetsCard()
                }
            } else {
                items(budgets) { budget ->
                    BudgetItemCard(
                        budget = budget,
                        categories = categories,
                        spentAmount = spentAmounts[budget.categoryId] ?: 0L,
                        onEdit = { selectedBudget = budget },
                        onDelete = { onDeleteBudget(budget.categoryId) }
                    )
                }
            }
        }
    }

    // Add/Edit Budget Dialog
    if (showAddDialog || selectedBudget != null) {
        BudgetDialog(
            budget = selectedBudget,
            categories = categories,
            onSave = { budget ->
                if (selectedBudget != null) {
                    onUpdateBudget(budget)
                } else {
                    onAddBudget(budget)
                }
                showAddDialog = false
                selectedBudget = null
            },
            onDismiss = {
                showAddDialog = false
                selectedBudget = null
            }
        )
    }
}

@Composable
private fun BudgetSummaryCard(budgets: List<Budget>) {
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.budget_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = stringResource(R.string.total_budgets),
                    value = budgets.size.toString(),
                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector),
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                StatCard(
                    title = stringResource(R.string.monthly_limit),
                    value = formatCurrency(budgets.sumOf { it.monthlyLimitMinor }),
                    icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_calendar_month_vector),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BudgetItemCard(
    budget: Budget,
    categories: List<Category>,
    spentAmount: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val category = categories.find { it.name == budget.categoryId }
    val progress = if (budget.monthlyLimitMinor > 0) {
        (spentAmount.toFloat() / budget.monthlyLimitMinor.toFloat()).coerceIn(0f, 1f)
    } else 0f

    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category?.name ?: budget.categoryId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.monthly_budget),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit_budget)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_budget)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Budget Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.spent_this_month),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        progress >= 1f -> MaterialTheme.colorScheme.error
                        progress >= 0.8f -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatCurrency(spentAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatCurrency(budget.monthlyLimitMinor),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyBudgetsCard() {
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
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.no_budgets),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.no_budgets_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetDialog(
    budget: Budget?,
    categories: List<Category>,
    onSave: (Budget) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(budget?.categoryId ?: "") }
    var amount by remember { mutableStateOf(budget?.monthlyLimitMinor?.toString() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (budget != null) stringResource(R.string.edit_budget) else stringResource(R.string.add_budget)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Selection - Debug için basit dropdown
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Kategori (${categories.size} adet)") },
                    trailingIcon = { 
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Kategori seç"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Dropdown Menu
                if (expanded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column {
                            if (categories.isEmpty()) {
                                Text(
                                    text = "Henüz kategori yok - önce kategori ekleyin",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                categories.forEach { category ->
                                    TextButton(
                                        onClick = {
                                            selectedCategory = category.name
                                            expanded = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${category.name} (ID: ${category.id})",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 8) {
                            amount = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.budget_limit)) },
                    suffix = { Text("₺") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountMinor = amount.toLongOrNull()?.times(100) ?: 0L
                    if (selectedCategory.isNotEmpty() && amountMinor > 0) {
                        onSave(Budget(selectedCategory, amountMinor))
                    }
                },
                enabled = selectedCategory.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun formatCurrency(amountMinor: Long): String {
    return "₺${amountMinor / 100}.${(amountMinor % 100).toString().padStart(2, '0')}"
}
