package com.alperen.spendcraft.feature.transactions.ui

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
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
                            text = stringResource(R.string.transaction_type),
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = SpendCraftTheme.colors.onSurface
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
                                            SpendCraftTheme.colors.secondary 
                                        else 
                                            SpendCraftTheme.colors.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.income),
                                        style = SpendCraftTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isIncome) 
                                            SpendCraftTheme.colors.secondary 
                                        else 
                                            SpendCraftTheme.colors.onSurfaceVariant
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
                                            SpendCraftTheme.colors.error 
                                        else 
                                            SpendCraftTheme.colors.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.expense),
                                        style = SpendCraftTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (!isIncome) 
                                            SpendCraftTheme.colors.error 
                                        else 
                                            SpendCraftTheme.colors.onSurfaceVariant
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
                            text = stringResource(R.string.amount),
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendCraftTheme.colors.onSurface
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
                                        stringResource(R.string.amount),
                                        style = SpendCraftTheme.typography.bodyMedium
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
                                            style = SpendCraftTheme.typography.bodyMedium
                                        )
                                    }
                                },
                                textStyle = SpendCraftTheme.typography.bodyLarge
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
                                        tint = SpendCraftTheme.colors.primary,
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
                            text = stringResource(R.string.note),
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { 
                                Text(
                                    stringResource(R.string.note_optional),
                                    style = SpendCraftTheme.typography.bodyMedium
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
                            textStyle = SpendCraftTheme.typography.bodyLarge
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
                            text = stringResource(R.string.category),
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: stringResource(R.string.select_category),
                                onValueChange = {},
                                readOnly = true,
                                label = { 
                                    Text(
                                        stringResource(R.string.category),
                                        style = SpendCraftTheme.typography.bodyMedium
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
                                textStyle = SpendCraftTheme.typography.bodyLarge
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
                                                style = SpendCraftTheme.typography.bodyMedium
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
                    containerColor = SpendCraftTheme.colors.primary,
                    contentColor = SpendCraftTheme.colors.onPrimary
                ),
                shape = SpendCraftTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = SpendCraftTheme.typography.titleMedium,
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


