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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    categories: StateFlow<List<Category>>,
    onSave: (amountMinor: Long, note: String?, categoryId: Long?, isIncome: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val cats by categories.collectAsState()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(cats.firstOrNull()?.id) }
    var isIncome by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    AppScaffold(
        title = "💳 İşlem Ekle",
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
                    "Kaydet",
                    fontWeight = FontWeight.SemiBold
                ) 
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "İşlem Türü",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = if (isIncome) 
                                            MaterialTheme.colorScheme.secondary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "💰 Gelir",
                                        style = MaterialTheme.typography.labelLarge,
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
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ThumbUp,
                                        contentDescription = null,
                                        tint = if (!isIncome) 
                                            MaterialTheme.colorScheme.error 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "💸 Gider",
                                        style = MaterialTheme.typography.labelLarge,
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tutar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { if (it.all(Char::isDigit)) amount = it },
                            label = { Text("Tutar (kuruş cinsinden)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            },
                            supportingText = {
                                if (amount.isNotEmpty()) {
                                    Text("₺${amount.toLongOrNull()?.div(100) ?: 0}")
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Açıklama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Not (isteğe bağlı)") },
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kategori",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: "Kategori Seçin",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori") },
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


