package com.alperen.spendcraft.feature.transactions.ui

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    transactions: List<Transaction>,
    categories: List<com.alperen.spendcraft.core.model.Category>,
    onBack: () -> Unit,
    onAddCategoryToTransaction: (Long, Long) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tüm İşlemler") },
                navigationIcon = {
                    TextButton(onClick = onBack) { 
                        Text(stringResource(R.string.back)) 
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (transactions.isEmpty()) {
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
                                imageVector = Icons.Filled.List,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "İşlem Yok",
                                style = SpendCraftTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendCraftTheme.colors.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "İşlem eklemeye başlayın",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionItemCard(
                        transaction = transaction,
                        categories = categories,
                        onAddCategory = { categoryId ->
                            transaction.id?.let { txId ->
                                onAddCategoryToTransaction(txId, categoryId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItemCard(
    transaction: Transaction,
    categories: List<com.alperen.spendcraft.core.model.Category>,
    onAddCategory: (Long) -> Unit
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.INCOME) {
                        Icons.Filled.Add
                    } else {
                        Icons.Filled.Close
                    },
                    contentDescription = null,
                    tint = if (transaction.type == TransactionType.INCOME) {
                        SpendCraftTheme.colors.primary
                    } else {
                        SpendCraftTheme.colors.error
                    },
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = transaction.note ?: "No description",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendCraftTheme.colors.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (transaction.categoryId != null) {
                                categories.find { it.id == transaction.categoryId }?.name ?: "Bilinmeyen Kategori"
                            } else {
                                "Kategori Yok"
                            },
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                        if (transaction.categoryId == null) {
                            IconButton(
                                onClick = { showCategoryDialog = true },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Kategori Ekle",
                                    tint = SpendCraftTheme.colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = formatDate(transaction.timestampUtcMillis),
                        style = SpendCraftTheme.typography.bodySmall,
                        color = SpendCraftTheme.colors.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = formatAmount(transaction.amount.minorUnits, transaction.type),
                style = SpendCraftTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.type == TransactionType.INCOME) {
                    SpendCraftTheme.colors.primary
                } else {
                    SpendCraftTheme.colors.error
                }
            )
        }
    }
    
    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Kategori Seç") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAddCategory(category.id ?: 0L)
                                    showCategoryDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = SpendCraftTheme.colors.surfaceVariant
                            )
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(16.dp),
                                style = SpendCraftTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

private fun formatAmount(amountMinor: Long, type: TransactionType): String {
    val sign = if (type == TransactionType.INCOME) "+" else "-"
    return "$sign₺${amountMinor / 100}.${(amountMinor % 100).toString().padStart(2, '0')}"
}

private fun formatDate(timestampUtcMillis: Long): String {
    val date = Date(timestampUtcMillis)
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}
