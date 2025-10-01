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
import com.alperen.spendcraft.core.ui.R as CoreR

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
        title = "💳 ${stringResource(CoreR.string.add_transaction)}",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Transaction Type Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(CoreR.string.transaction_type),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
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
                                        text = stringResource(CoreR.string.income),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
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
                                        text = stringResource(CoreR.string.expense),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
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
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(CoreR.string.amount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    val cleanValue = newValue.replace(Regex("[^0-9]"), "")
                                    if (cleanValue.length <= 8) { // Max 8 digits
                                        amount = cleanValue
                                    }
                                },
                                label = { 
                                    Text(
                                        stringResource(CoreR.string.amount),
                                        style = MaterialTheme.typography.bodyMedium
                                    ) 
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Call,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                supportingText = {
                                    if (amount.isNotEmpty()) {
                                        val formattedAmount = formatCurrencyDisplay(amount.toLongOrNull() ?: 0)
                                        Text(
                                            "₺$formattedAmount",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                },
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                            
                            // Camera button for expense transactions
                            if (!isIncome) {
                                IconButton(
                                    onClick = {
                                        // TODO: Implement camera receipt scanning
                                        // For now, just show a placeholder
                                    },
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountBox,
                                        contentDescription = "Fiş Tara",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Note Input
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(CoreR.string.note),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { 
                                Text(
                                    stringResource(CoreR.string.note_optional),
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            maxLines = 2,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            // Category Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(CoreR.string.category),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: stringResource(CoreR.string.select_category),
                                onValueChange = {},
                                readOnly = true,
                                label = { 
                                    Text(
                                        stringResource(CoreR.string.category),
                                        style = MaterialTheme.typography.bodyMedium
                                    ) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                cats.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                cat.name,
                                                style = MaterialTheme.typography.bodyMedium
                                            ) 
                                        },
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
            
            // Save Button at Bottom
            Button(
                onClick = {
                    val minor = amount.toLong()
                    onSave(minor, note.ifBlank { null }, selectedCategoryId, isIncome)
                },
                enabled = amount.toLongOrNull() != null && amount.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(CoreR.string.save),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// Para birimi formatlaması fonksiyonları
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


