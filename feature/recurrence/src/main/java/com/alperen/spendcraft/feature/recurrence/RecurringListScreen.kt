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
    val recurringTransactions by recurringTransactionsFlow.collectAsState(initial = emptyList())
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu ayarlayalım
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 32.sp,
        stop = 22.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Tekrarlayan İşlemler",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    // iOS'ta premium kontrolü YOK - herkes ekleyebilir
                    IconButton(onClick = onAddRule) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Yeni Tekrarlayan İşlem Ekle"
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
                            text = "Henüz tekrarlayan işlem yok",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Sağ üst köşedeki + butonuna basarak ekleyin",
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
                        text = "${transaction.amount} TL - ${getFrequencyDisplayName(transaction.frequency)}",
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
                            contentDescription = "Düzenle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Sil",
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
                        text = "Sonraki çalışma:",
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
                            text = "Bitiş:",
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
                    text = if (transaction.isActive) "Aktif" else "Duraklatıldı",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Tekrarlayan İşlemi Sil") },
            text = { Text("Bu tekrarlayan işlemi silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}

private fun getFrequencyDisplayName(frequency: com.alperen.spendcraft.data.db.entities.RecurringFrequency): String {
    return when (frequency) {
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.DAILY -> "Günlük"
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.WEEKLY -> "Haftalık"
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.MONTHLY -> "Aylık"
        com.alperen.spendcraft.data.db.entities.RecurringFrequency.YEARLY -> "Yıllık"
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
