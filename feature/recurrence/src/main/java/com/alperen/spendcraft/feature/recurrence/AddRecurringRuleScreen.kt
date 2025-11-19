package com.alperen.spendcraft.feature.recurrence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS AddRecurringTransactionView'in birebir aynısı
 * Form yapısı: İşlem Bilgileri, Kategori, Tekrar Sıklığı, Durum
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringRuleScreen(
    categories: List<Category>,
    onSave: (RecurringRuleData) -> Unit,
    onCancel: () -> Unit,
    viewModel: RecurringViewModel? = null
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf("MONTHLY") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isActive by remember { mutableStateOf(true) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showFrequencyPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val frequencies = listOf(
        stringResource(R.string.recurring_frequency_daily) to "DAILY",
        stringResource(R.string.recurring_frequency_weekly) to "WEEKLY",
        stringResource(R.string.recurring_frequency_monthly) to "MONTHLY",
        stringResource(R.string.recurring_frequency_yearly) to "YEARLY"
    )
    
    val frequencyDisplay = frequencies.firstOrNull { it.second == selectedFrequency }?.first
        ?: stringResource(R.string.recurring_frequency_monthly)
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) }
    
    AppScaffold(
        title = stringResource(CoreR.string.add_recurring_transaction),
        onBack = onCancel,
        actions = {
            TextButton(onClick = onCancel) {
                Text(stringResource(CoreR.string.cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // İşlem Bilgileri Section
            item {
                ModernCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(CoreR.string.transaction_info),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // İşlem Adı
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(stringResource(CoreR.string.transaction_name)) },
                            placeholder = { Text(stringResource(CoreR.string.example_rent_salary)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Tutar - iOS: CurrencyTextField with formatting
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            com.alperen.spendcraft.core.ui.CurrencyTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                placeholder = { Text("0.00") },
                                modifier = Modifier.weight(1f)
                            )
                            
                            Text(
                                "₺",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Tip (Segmented Control - iOS Style)
                        Text(
                            text = stringResource(CoreR.string.type),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Gider
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (!isIncome) MaterialTheme.colorScheme.surface else Color.Transparent
                                    )
                                    .clickable { isIncome = false }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(CoreR.string.expense_label),
                                    fontWeight = if (!isIncome) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            // Gelir
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isIncome) MaterialTheme.colorScheme.surface else Color.Transparent
                                    )
                                    .clickable { isIncome = true }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(CoreR.string.income_label),
                                    fontWeight = if (isIncome) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            
            // Kategori Section
            item {
                ModernCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(CoreR.string.category),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (selectedCategory != null) {
                            // Selected category display - iOS'taki gibi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = getCategoryIconResource(selectedCategory?.icon ?: "circle.fill")),
                                        contentDescription = null,
                                        tint = try { 
                                            Color(android.graphics.Color.parseColor(selectedCategory?.color ?: "#007AFF")) 
                                        } catch (e: Exception) { 
                                            IOSColors.Blue 
                                        }
                                    )
                                    Text(com.alperen.spendcraft.core.ui.CategoryLocalization.localize(context, selectedCategory?.name))
                                }
                                TextButton(onClick = { selectedCategory = null }) {
                                    Text(stringResource(CoreR.string.change), style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        } else {
                            // Category picker - iOS: Picker
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { showCategoryPicker = true }
                            ) {
                                OutlinedTextField(
                                    value = stringResource(CoreR.string.select),
                                    onValueChange = { },
                                    label = { Text(stringResource(CoreR.string.select_category)) },
                                    readOnly = true,
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Tekrar Sıklığı Section
            item {
                ModernCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(CoreR.string.recurring_frequency),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Frequency Picker - iOS: Picker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .clickable { showFrequencyPicker = true }
                        ) {
                            OutlinedTextField(
                                value = frequencyDisplay,
                                onValueChange = { },
                                label = { Text(stringResource(CoreR.string.frequency)) },
                                readOnly = true,
                                enabled = false,
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Start Date - iOS: DatePicker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = dateFormat.format(Date(startDate)),
                                onValueChange = { },
                                label = { Text(stringResource(CoreR.string.start_date)) },
                                readOnly = true,
                                enabled = false,
                                trailingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
            
            // Durum Section
            item {
                ModernCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(CoreR.string.active),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it }
                        )
                    }
                }
            }
            
            // Kaydet Button
            item {
                Button(
                    onClick = {
                        // iOS locale-aware: virgül de nokta da kabul edilir
                        val normalizedAmount = amount.replace(',', '.')
                        val amountValue = normalizedAmount.toDoubleOrNull()
                        val category = selectedCategory
                        if (name.isNotEmpty() && amountValue != null && category != null && category.id != null) {
                            onSave(
                                RecurringRuleData(
                                    title = name,
                                    amount = amountValue,
                                    categoryId = category.id!!,
                                    type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                                    frequency = when (selectedFrequency) {
                                        "DAILY" -> Frequency.DAILY
                                        "WEEKLY" -> Frequency.WEEKLY
                                        "MONTHLY" -> Frequency.MONTHLY
                                        "YEARLY" -> Frequency.YEARLY
                                        else -> Frequency.MONTHLY
                                    },
                                    interval = 1,
                                    startDate = startDate,
                                    endDate = null,
                                    description = ""
                                )
                            )
                        }
                    },
                    enabled = name.isNotEmpty() && amount.replace(',', '.').toDoubleOrNull() != null && selectedCategory != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        stringResource(CoreR.string.save),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    // Category Picker Dialog
    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = { Text(stringResource(CoreR.string.select_category)) },
            text = {
                Column {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = category
                                    showCategoryPicker = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = getCategoryIconResource(category.icon ?: "circle.fill")),
                                contentDescription = null,
                                tint = try { 
                                    Color(android.graphics.Color.parseColor(category.color)) 
                                } catch (e: Exception) { 
                                    IOSColors.Blue 
                                }
                            )
                            Text(com.alperen.spendcraft.core.ui.CategoryLocalization.localize(context, category.name))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryPicker = false }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            }
        )
    }
    
    // Frequency Picker Dialog
    if (showFrequencyPicker) {
        AlertDialog(
            onDismissRequest = { showFrequencyPicker = false },
            title = { Text(stringResource(CoreR.string.select_frequency)) },
            text = {
                Column {
                    frequencies.forEach { (display, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFrequency = value
                                    showFrequencyPicker = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(display)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFrequencyPicker = false }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            }
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { startDate = it }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(CoreR.string.select))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * iOS SF Symbol to Android Drawable mapping for categories
 */
private fun getCategoryIconResource(sfSymbol: String): Int {
    return when (sfSymbol) {
        "cart.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_cart_fill
        "fork.knife" -> com.alperen.spendcraft.core.ui.R.drawable.ic_fork_knife
        "house.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_house_fill
        "car.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_car_fill
        "tram.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_tram_fill
        "airplane" -> com.alperen.spendcraft.core.ui.R.drawable.ic_airplane
        "bolt.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_bolt_fill
        "bag.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_bag_fill
        "gift.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_gift_fill
        "book.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_book_fill
        "gamecontroller.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_gamecontroller_fill
        "film.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_film_fill
        "heart.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_heart_fill
        "creditcard.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_creditcard_fill
        "pills.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_pills_fill
        "briefcase.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_briefcase_fill
        "graduationcap.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_graduationcap_fill
        "phone.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_phone_fill
        "circle.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_circle_fill
        else -> com.alperen.spendcraft.core.ui.R.drawable.ic_circle_fill
    }
}

// Data classes
enum class TransactionType(val displayName: String) {
    INCOME("Gelir"),
    EXPENSE("Gider")
}

enum class Frequency(val displayName: String, val unit: String) {
    DAILY("Günlük", "gün"),
    WEEKLY("Haftalık", "hafta"),
    MONTHLY("Aylık", "ay"),
    YEARLY("Yıllık", "yıl")
}

data class RecurringRuleData(
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val type: TransactionType,
    val frequency: Frequency,
    val interval: Int,
    val startDate: Long,
    val endDate: Long?,
    val description: String
)
