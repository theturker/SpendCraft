package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.draw.clip
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.res.painterResource
import com.alperen.spendcraft.feature.transactions.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    categories: StateFlow<List<Category>>,
    initialTransactionType: Boolean? = null,
    onSave: (amountMinor: Long, note: String?, categoryId: Long?, isIncome: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cats by categories.collectAsState()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(cats.firstOrNull()?.id) }
    var isIncome by remember { mutableStateOf(initialTransactionType ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    
    // Date ve time için state'ler
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedTime by remember { mutableStateOf(Date()) }
    
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currencySymbol = com.alperen.spendcraft.core.ui.CurrencyFormatter.getCurrencySymbol(context)

    val fieldShape = RoundedCornerShape(12.dp)
    val fieldBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val fieldBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            // Modern drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.65f) // Sayfanın %65'i
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            
            // Segmented Gelir/Gider toggle
            IncomeExpenseSegmented(
                isIncome = isIncome,
                onChange = { isIncome = it },
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Kategori Seçimi - 3 satır gözükecek şekilde grid (daha fazla ise scroll)
                item {
                    Column {
                        Text(
                            text = "Kategori",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(fieldShape)
                                .background(fieldBg)
                                .border(1.dp, fieldBorderColor, fieldShape)
                                .padding(8.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(cats.size) { index ->
                                    val category = cats[index]
                                    CompactCategoryChip(
                                        text = category.name,
                                        isSelected = selectedCategoryId == category.id,
                                        onClick = { selectedCategoryId = category.id }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Tutar Girişi - Para girişi gibi
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_wallet),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tutar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Büyük, merkezli tutar alanı (normal yükseklik)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(fieldBg)
                                .border(1.dp, fieldBorderColor, RoundedCornerShape(20.dp))
                                .padding(vertical = 4.dp, horizontal = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    val cleanValue = newValue.replace(Regex("[^0-9.,]"), "")
                                    if (cleanValue.length <= 10) amount = cleanValue
                                },
                                placeholder = {
                                    Text(
                                        "0,00",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                                trailingIcon = {
                                    Text(
                                        text = currencySymbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(0.dp)
                            )
                        }
                    }
                }
                
                // Tarih ve Saat - Chip'lerle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AssistChip(
                            onClick = { showDatePicker = true },
                            label = { Text(dateFormatter.format(selectedDate)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_calendar),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                        AssistChip(
                            onClick = { showTimePicker = true },
                            label = { Text(timeFormatter.format(selectedTime)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_clock),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                
                // Not
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_note),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Not (İsteğe bağlı)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        ModernCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(fieldShape)
                                    .background(fieldBg)
                                    .border(1.dp, fieldBorderColor, fieldShape)
                                    .padding(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    placeholder = {
                                        Text(
                                            "İşlem hakkında not ekleyin...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(0.dp),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                // Ödeme Yöntemi
                item {
                    Column {
                        Text(
                            text = "Ödeme Yöntemi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(PaymentMethod.values().size) { idx ->
                                val method = PaymentMethod.values()[idx]
                                FilterChip(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = if (paymentMethod == method) null else method },
                                    label = { Text(method.label, maxLines = 1) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Kaydet Butonu
            Button(
                onClick = {
                    val amountValue = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val amountMinor = (amountValue * 100).toLong()
                    val mergedNote = buildString {
                        if (!note.isNullOrBlank()) append(note.trim())
                        paymentMethod?.let {
                            if (this.isNotEmpty()) append(" • ")
                            append(it.label)
                        }
                    }.ifBlank { null }
                    onSave(amountMinor, mergedNote, selectedCategoryId, isIncome)
                    onDismiss()
                },
                enabled = amount.isNotEmpty() && selectedCategoryId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIncome) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        painter = painterResource(if (isIncome) com.alperen.spendcraft.core.ui.R.drawable.ic_income_vector else com.alperen.spendcraft.core.ui.R.drawable.ic_expense_vector),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isIncome) "Gelir Ekle" else "Gider Ekle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Date Picker
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // Time Picker
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

private enum class PaymentMethod(val label: String) {
    CASH("Nakit"), CARD("Kredi Kartı"), DEBIT_CARD("Banka Kartı"), TRANSFER("Havale/EFT")
}

@Composable
private fun IncomeExpenseSegmented(
    isIncome: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), shape)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SegmentedItem(
            text = "Gelir",
            selected = isIncome,
            onClick = { onChange(true) },
            leading = {
                Icon(
                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_income_vector),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = Modifier.weight(1f)
        )
        SegmentedItem(
            text = "Gider",
            selected = !isIncome,
            onClick = { onChange(false) },
            leading = {
                Icon(
                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_expense_vector),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SegmentedItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val container = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = modifier
            .clip(shape)
            .background(container)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            CompositionLocalProvider(LocalContentColor provides content) {
                leading()
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = content,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CompactCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        },
        selected = isSelected,
        modifier = Modifier.height(32.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(6.dp)
    )
}

@Composable
private fun ModernCategoryChip(
    text: String,
    iconResource: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        selected = isSelected,
        modifier = Modifier.height(40.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

// Kategori ikonlarını döndüren fonksiyon
private fun getCategoryIconResource(categoryName: String): Int {
    return when (categoryName.lowercase()) {
        "food", "yemek", "yiyecek" -> R.drawable.ic_category_food
        "transport", "ulaşım", "taşıma" -> R.drawable.ic_category_transport
        "shopping", "alışveriş" -> R.drawable.ic_category_shopping
        "entertainment", "eğlence" -> R.drawable.ic_category_entertainment
        "bills", "faturalar", "fatura" -> R.drawable.ic_category_bills
        "health", "sağlık" -> R.drawable.ic_category_health
        "education", "eğitim" -> R.drawable.ic_category_education
        "travel", "seyahat" -> R.drawable.ic_category_travel
        "income", "gelir" -> R.drawable.ic_category_income
        else -> R.drawable.ic_category_default
    }
}

// Date Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                    onDismiss()
                }
            ) {
                Text("Seç")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// Time Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saat Seç") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    onTimeSelected(calendar.time)
                    onDismiss()
                }
            ) {
                Text("Seç")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
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
