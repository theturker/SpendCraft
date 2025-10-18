package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.data.db.entities.AccountEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS EditTransactionView'in birebir Android karşılığı
 * EditTransactionView.swift - İşlem Düzenleme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSEditTransactionScreen(
    transaction: Transaction,
    categories: List<Category>,
    accounts: List<AccountEntity> = emptyList(),
    onSave: (Transaction) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Load existing transaction data - EditTransactionView.swift:223-244
    var amount by remember { 
        mutableStateOf((transaction.amount.minorUnits / 100.0).toString().replace(".0", ""))
    }
    var note by remember { mutableStateOf(transaction.note ?: "") }
    val isIncomeState = remember { mutableStateOf(transaction.type == TransactionType.INCOME) }
    var isIncome by isIncomeState
    var selectedCategory by remember { 
        mutableStateOf<Category?>(categories.find { it.id == transaction.categoryId }) 
    }
    var selectedAccount by remember { 
        mutableStateOf<AccountEntity?>(accounts.find { it.id == transaction.accountId }) 
    }
    var selectedDate by remember { mutableStateOf(Date(transaction.timestampUtcMillis)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Auto-select first account if none selected
    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts.isNotEmpty()) {
            selectedAccount = accounts.firstOrNull()
        }
    }
    
    // iOS: filteredCategories based on transaction type
    val filteredCategories = remember(categories, isIncomeState.value) {
        categories.filter { it.isIncome == isIncomeState.value }
    }
    
    val normalizedAmount = amount.replace(',', '.')
    val isValid = amount.isNotEmpty() && 
                  normalizedAmount.toDoubleOrNull() != null && 
                  normalizedAmount.toDouble() > 0 &&
                  selectedCategory != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "İşlemi Düzenle",  // iOS: "İşlemi Düzenle"
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onDismiss) {
                        Text("İptal", color = IOSColors.Blue)
                    }
                },
                actions = {
                    // iOS: Sil butonu - EditTransactionView.swift:203-209
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Sil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 1. Transaction Type Section (Segmented Control)
            // iOS: EditTransactionView.swift:107-118
            item {
                FormSection(title = null) {
                    SegmentedControl(
                        selectedIsIncome = isIncome,
                        onSelectionChange = { newType ->
                            isIncome = newType
                            selectedCategory = null  // iOS: Clear category when type changes
                        }
                    )
                }
            }
            
            // 2. Amount Section
            // iOS: EditTransactionView.swift:120-128
            item {
                FormSection(title = "Miktar") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.titleLarge
                        )
                        
                        Text(
                            text = "₺",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 3. Category Section (Horizontal Buttons)
            // iOS: EditTransactionView.swift:130-165
            item {
                FormSection(title = "Kategori") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredCategories) { category ->
                            CategoryButtonEdit(
                                category = category,
                                isSelected = selectedCategory?.id == category.id,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }
            
            // 4. Account Section
            // iOS: EditTransactionView.swift:167-174
            item {
                FormSection(title = "Hesap") {
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedAccount?.name ?: "Hesap Seçiniz",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account.name) },
                                    onClick = {
                                        selectedAccount = account
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // 5. Date Section
            // iOS: EditTransactionView.swift:176-178
            item {
                FormSection(title = "Tarih") {
                    OutlinedTextField(
                        value = remember(selectedDate) { 
                            val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("tr"))
                            dateFormat.format(selectedDate)
                        },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // 6. Note Section
            // iOS: EditTransactionView.swift:180-182
            item {
                FormSection(title = "Not") {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("İsteğe bağlı not") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // 7. Save Button
            // iOS: EditTransactionView.swift:181-199
            item {
                FormSection(title = null) {
                    Button(
                        onClick = {
                            val normalizedAmount = amount.replace(',', '.')
                            val amountValue = normalizedAmount.toDoubleOrNull() ?: 0.0
                            val amountMinor = (amountValue * 100).toLong()
                            
                            val updatedTx = transaction.copy(
                                amount = com.alperen.spendcraft.core.model.Money(amountMinor),
                                note = note.ifEmpty { null },
                                categoryId = selectedCategory?.id,
                                accountId = selectedAccount?.id,
                                timestampUtcMillis = selectedDate.time,
                                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
                            )
                            onSave(updatedTx)
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isValid) IOSColors.Blue else Color.Gray,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Değişiklikleri Kaydet",  // iOS: "Değişiklikleri Kaydet"
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("İşlemi Sil?") },
            text = { Text("Bu işlem kalıcı olarak silinecek.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
    
    // DatePicker Dialog - iOS: EditTransactionView.swift:176-178
    if (showDatePicker) {
        // TODO: Material3 DatePicker implementation
        // For now, close immediately
        LaunchedEffect(Unit) {
            showDatePicker = false
        }
    }
}

/**
 * Category Button - iOS: EditTransactionView.swift:130-165
 * Note: Duplicate from IOSAddTransactionScreen (can't access private)
 */
@Composable
internal fun CategoryButtonEdit(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        try { Color(android.graphics.Color.parseColor(category.color)) }
        catch (e: Exception) { IOSColors.Blue }
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val borderColor = if (isSelected) {
        try { Color(android.graphics.Color.parseColor(category.color)) }
        catch (e: Exception) { IOSColors.Blue }
    } else {
        Color.Transparent
    }
    
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = if (isSelected) 0.3f else 1f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Category Icon (circle with emoji/icon)
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = if (isSelected) backgroundColor else Color(0xFFE8E8E8),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Show category icon when available
            Icon(
                painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_plus_circle_fill),
                contentDescription = null,
                tint = if (isSelected) {
                    try { Color(android.graphics.Color.parseColor(category.color)) }
                    catch (e: Exception) { IOSColors.Blue }
                } else {
                    Color.Gray
                },
                modifier = Modifier.size(28.dp)
            )
        }
        
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                try { Color(android.graphics.Color.parseColor(category.color)) }
                catch (e: Exception) { IOSColors.Blue }
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

