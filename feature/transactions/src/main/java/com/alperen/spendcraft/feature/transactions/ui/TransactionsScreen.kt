package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.AddFab
import com.alperen.spendcraft.core.ui.AppScaffold
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
    
    AppScaffold(
        title = "💳 Transactions",
        fab = { AddFab(onClick = onAdd) }
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: ${formatMinor(totalAmount)}")
                Row {
                    TextButton(onClick = onReports) { Text("📊 Reports") }
                    TextButton(onClick = onSettings) { Text("⚙️ Settings") }
                }
            }
            Spacer(Modifier.height(16.dp))
            
            // Quick Action Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddIncome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("💰 Income")
                }
                Button(
                    onClick = onAddExpense,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("💸 Expense")
                }
            }
            Spacer(Modifier.height(16.dp))
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions yet")
                }
            } else {
                LazyColumn {
                    items(items) { tx ->
                        TxRow(tx = tx, onDelete = { tx.id?.let(viewModel::deleteTransaction) })
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TxRow(tx: Transaction, onDelete: () -> Unit) {
    val context = LocalContext.current
    val amount = if (tx.type == TransactionType.INCOME) tx.amount.minorUnits else -tx.amount.minorUnits
    
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = if (tx.type == TransactionType.INCOME) "💰 Income" else "💸 Expense", style = MaterialTheme.typography.titleMedium)
            Text(text = tx.note ?: "", style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = formatMinor(amount))
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onDelete) { Text("🗑️ Delete") }
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