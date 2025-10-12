package com.alperen.spendcraft.feature.transactions.ui

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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS TransactionsTabView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Filter Pills (Tümü, Gelir, Gider)
 * - Grouped List (tarihe göre)
 * - Swipe to Delete
 * - + butonu toolbar'da
 */

enum class TransactionFilter(val label: String) {
    ALL("Tümü"),
    INCOME("Gelir"),
    EXPENSE("Gider")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    transactions: List<Transaction>,
    onAddTransaction: () -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    
    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.INCOME -> transactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        }
    }
    
    val groupedTransactions = remember(filteredTransactions) {
        groupTransactionsByDate(filteredTransactions)
    }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "İşlemler",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onAddTransaction) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_plus_circle_fill),
                            contentDescription = "İşlem Ekle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Pills
            FilterPillsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Transactions List
            if (filteredTransactions.isEmpty()) {
                EmptyTransactionsState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    groupedTransactions.forEach { (date, transactionsForDate) ->
                        // Section Header
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            )
                        }
                        
                        // Transactions for this date
                        items(
                            items = transactionsForDate,
                            key = { it.id ?: it.hashCode() }
                        ) { transaction ->
                            TransactionListRow(
                                transaction = transaction
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Filter Pills Row - iOS'taki segmented control benzeri
 */
@Composable
private fun FilterPillsRow(
    selectedFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(TransactionFilter.values()) { filter ->
            FilterPill(
                label = filter.label,
                isSelected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = CircleShape
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )
    }
}

/**
 * Transaction Row - iOS tasarımı
 */
@Composable
private fun TransactionListRow(
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
    
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("tr")) }
    val formattedTime = remember(transaction.timestampUtcMillis) {
        timeFormat.format(Date(transaction.timestampUtcMillis))
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
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
                text = formattedTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Amount
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = CurrencyFormatter.format(context, transaction.amount.minorUnits),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
            
            // Account name (if available)
            // TODO: Account bilgisi eklenecek
        }
    }
}

/**
 * Empty State
 */
@Composable
private fun EmptyTransactionsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(60.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Henüz işlem yok",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "+ butonuna basarak ilk işleminizi ekleyin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Helper function to group transactions by date
 */
private fun groupTransactionsByDate(transactions: List<Transaction>): List<Pair<String, List<Transaction>>> {
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
    
    return transactions
        .sortedByDescending { it.timestampUtcMillis }
        .groupBy { transaction ->
            dateFormat.format(Date(transaction.timestampUtcMillis))
        }
        .map { (date, transactions) ->
            date to transactions
        }
}

