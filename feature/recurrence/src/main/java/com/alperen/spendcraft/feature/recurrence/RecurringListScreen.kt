package com.alperen.spendcraft.feature.recurrence

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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.ModernCard
// import com.alperen.spendcraft.ui.iosTheme.*  // Note: IOSTheme in app module
import com.alperen.spendcraft.data.db.entities.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS RecurringTransactionsListView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Premium gate YOK (iOS'ta yok)
 * - + button toolbar'da (her kullanıcı için)
 * - List with swipe actions
 * - Active/Inactive status
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringListScreen(
    recurringTransactionsFlow: Flow<List<RecurringTransactionEntity>>,
    onAddRule: () -> Unit,
    onEditRule: (RecurringTransactionEntity) -> Unit,
    onDeleteRule: (RecurringTransactionEntity) -> Unit,
    isPremium: Boolean,
    onBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val recurringTransactions by recurringTransactionsFlow.collectAsState(initial = emptyList())
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu ayarlayalım - daha çok küçülsün
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 34.sp,
        stop = 17.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                LargeTopAppBar(
                    title = { Spacer(modifier = Modifier) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                                contentDescription = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.back)
                            )
                    }
                },
                actions = {
                    // iOS'ta premium kontrolü YOK - herkes ekleyebilir
                    IconButton(onClick = onAddRule) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_add)
                        )
                    }
                },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                
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
                        text = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_title),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (recurringTransactions.isEmpty()) {
                // iOS'taki empty state
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_repeat_circle_fill),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_empty_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(recurringTransactions) { transaction ->
                    RecurringTransactionItem(
                        transaction = transaction,
                        onEdit = { onEditRule(transaction) },
                        onDelete = { onDeleteRule(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringTransactionItem(
    transaction: RecurringTransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    ModernCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${transaction.amount} TL - ${getFrequencyDisplayName(context, transaction.frequency)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_next_execution),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(Date(transaction.nextExecution)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (transaction.endDate != null) {
                    Column {
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_end_date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateFormatter.format(Date(transaction.endDate!!)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (transaction.isActive) painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_play_arrow_vector) else painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_pause_vector),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (transaction.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (transaction.isActive) context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_active) else context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_paused),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_delete_title)) },
            text = { Text(context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(context.getString(com.alperen.spendcraft.feature.recurrence.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun getFrequencyDisplayName(context: android.content.Context, frequency: com.alperen.spendcraft.data.db.entities.RecurringFrequency): String {
    return when (frequency) {
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.DAILY -> context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_frequency_daily)
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.WEEKLY -> context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_frequency_weekly)
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.MONTHLY -> context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_frequency_monthly)
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.YEARLY -> context.getString(com.alperen.spendcraft.feature.recurrence.R.string.recurring_frequency_yearly)
    }
}

@Preview(name = "Recurring - Light")
@Composable
private fun RecurringListScreenPreview() {
    RecurringListScreen(
        recurringTransactionsFlow = kotlinx.coroutines.flow.flowOf(emptyList()),
        onAddRule = {},
        onEditRule = {},
        onDeleteRule = {},
        isPremium = false,
        onBack = {}
    )
}
