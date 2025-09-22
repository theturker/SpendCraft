package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    categories: StateFlow<List<Category>>,
    initialTransactionType: Boolean? = null,
    onSave: (amountMinor: Long, note: String?, categoryId: Long?, isIncome: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val cats by categories.collectAsState()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(cats.firstOrNull()?.id) }
    var isIncome by remember { mutableStateOf(initialTransactionType ?: false) }
    var expanded by remember { mutableStateOf(false) }

    AppScaffold(
        title = "💳 ${stringResource(R.string.add_transaction)}",
        onBack = onBack,
        actions = {
            TextButton(
                enabled = amount.toLongOrNull() != null && amount.isNotEmpty(),
                onClick = {
                    val minor = amount.toLong()
                    onSave(minor, note.ifBlank { null }, selectedCategoryId, isIncome)
                }
            ) { 
                Text(
                    stringResource(R.string.save),
                    fontWeight = FontWeight.SemiBold
                ) 
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Transaction Type Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.transaction_type),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectableGroup(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Income Button
                            ModernCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = isIncome,
                                        onClick = { isIncome = true }
                                    ),
                                onClick = { isIncome = true }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = if (isIncome) 
                                            MaterialTheme.colorScheme.secondary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.income),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isIncome) 
                                            MaterialTheme.colorScheme.secondary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Expense Button
                            ModernCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = !isIncome,
                                        onClick = { isIncome = false }
                                    ),
                                onClick = { isIncome = false }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ThumbUp,
                                        contentDescription = null,
                                        tint = if (!isIncome) 
                                            MaterialTheme.colorScheme.error 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.expense),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (!isIncome) 
                                            MaterialTheme.colorScheme.error 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Amount Input
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.amount),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = formatCurrencyInput(amount),
                            onValueChange = { newValue ->
                                val cleanValue = newValue.replace(Regex("[^0-9]"), "")
                                if (cleanValue.length <= 8) { // Max 8 digits
                                    amount = cleanValue
                                }
                            },
                            label = { Text(stringResource(R.string.amount)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = null
                                )
                            },
                            supportingText = {
                                if (amount.isNotEmpty()) {
                                    val formattedAmount = formatCurrencyDisplay(amount.toLongOrNull() ?: 0)
                                    Text("₺$formattedAmount")
                                }
                            }
                        )
                    }
                }
            }
            
            // Note Input
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.note),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text(stringResource(R.string.note_optional)) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.MailOutline,
                                    contentDescription = null
                                )
                            },
                            maxLines = 3
                        )
                    }
                }
            }
            
            // Category Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.category),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: stringResource(R.string.select_category),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.category)) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                cats.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = {
                                            selectedCategoryId = cat.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Para birimi formatlaması fonksiyonları
private fun formatCurrencyInput(amount: String): String {
    if (amount.isEmpty()) return ""
    val number = amount.toLongOrNull() ?: 0
    return formatCurrencyDisplay(number)
}

private fun formatCurrencyDisplay(amount: Long): String {
    val major = amount / 100
    val minor = amount % 100
    
    val majorFormatted = major.toString().reversed().chunked(3).joinToString(".").reversed()
    
    return if (minor > 0) {
        "$majorFormatted,${minor.toString().padStart(2, '0')}"
    } else {
        majorFormatted
    }
}


