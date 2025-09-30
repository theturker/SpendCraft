package com.alperen.spendcraft.feature.recurrence

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.data.db.entities.RecurringRuleEntity
import com.alperen.spendcraft.core.model.Category
import java.util.*

@Composable
fun AddRecurringRuleScreen(
    categories: List<Category>,
    onSave: (RecurringRuleData) -> Unit,
    onCancel: () -> Unit,
    viewModel: RecurringViewModel? = null
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Long?>(null) }
    var selectedType by remember { mutableStateOf<TransactionType>(TransactionType.EXPENSE) }
    var selectedFrequency by remember { mutableStateOf<Frequency>(Frequency.MONTHLY) }
    var interval by remember { mutableStateOf("1") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var hasEndDate by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    
    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    AppScaffold(
        title = "Tekrarlayan İşlem Ekle",
        onBack = onCancel
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "İşlem Bilgileri",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Başlık (örn: Kira, Maaş)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Tutar") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // İşlem Türü
                        Text(
                            text = "İşlem Türü",
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TransactionType.values().forEach { type ->
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .selectable(
                                            selected = selectedType == type,
                                            onClick = { selectedType = type }
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedType == type,
                                        onClick = { selectedType = type }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = type.displayName,
                                        style = SpendCraftTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Kategori Seçimi
                        Text(
                            text = "Kategori",
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                FilterChip(
                                    onClick = { 
                                        selectedCategory = if (selectedCategory == category.id) null else category.id
                                    },
                                    label = { Text(category.name) },
                                    selected = selectedCategory == category.id
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Açıklama (opsiyonel)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            }
            
            // Tekrar Ayarları
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tekrar Ayarları",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Sıklık
                        Text(
                            text = "Sıklık",
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Frequency.values().forEach { frequency ->
                                FilterChip(
                                    onClick = { selectedFrequency = frequency },
                                    label = { Text(frequency.displayName) },
                                    selected = selectedFrequency == frequency
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Aralık
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Her",
                                style = SpendCraftTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            OutlinedTextField(
                                value = interval,
                                onValueChange = { interval = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(80.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = selectedFrequency.unit,
                                style = SpendCraftTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Başlangıç Tarihi
                        Text(
                            text = "Başlangıç Tarihi",
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(startDate)),
                            onValueChange = { },
                            label = { Text("Başlangıç Tarihi") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStartDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Tarih Seç")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Bitiş Tarihi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = hasEndDate,
                                onCheckedChange = { hasEndDate = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bitiş tarihi belirle",
                                style = SpendCraftTheme.typography.bodyMedium
                            )
                        }
                        
                        if (hasEndDate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = endDate?.let { 
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                                } ?: "",
                                onValueChange = { },
                                label = { Text("Bitiş Tarihi") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showEndDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Tarih Seç")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Önizleme
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Önizleme",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (title.isNotBlank() && amount.isNotBlank()) {
                            Text(
                                text = "$title - ${amount} TL",
                                style = SpendCraftTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Her $interval ${selectedFrequency.unit} tekrarlanacak",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            if (description.isNotBlank()) {
                                Text(
                                    text = description,
                                    style = SpendCraftTheme.typography.bodySmall,
                                    color = SpendCraftTheme.colors.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = "Bilgileri doldurun",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Kaydet Butonu
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }
                    
                    Button(
                        onClick = {
                            println("🔍 DEBUG: Kaydet butonuna basıldı")
                            if (title.isNotBlank() && amount.isNotBlank() && selectedCategory != null) {
                                println("🔍 DEBUG: Form valid, kaydetme işlemi başlıyor")
                                val amountLong = ((amount.toDoubleOrNull() ?: 0.0) * 100).toLong() // Convert to minor units
                                val frequency = when (selectedFrequency) {
                                    Frequency.DAILY -> com.alperen.spendcraft.data.db.entities.RecurringFrequency.DAILY
                                    Frequency.WEEKLY -> com.alperen.spendcraft.data.db.entities.RecurringFrequency.WEEKLY
                                    Frequency.MONTHLY -> com.alperen.spendcraft.data.db.entities.RecurringFrequency.MONTHLY
                                    Frequency.YEARLY -> com.alperen.spendcraft.data.db.entities.RecurringFrequency.YEARLY
                                }
                                val type = when (selectedType) {
                                    TransactionType.INCOME -> com.alperen.spendcraft.core.model.TransactionType.INCOME
                                    TransactionType.EXPENSE -> com.alperen.spendcraft.core.model.TransactionType.EXPENSE
                                }
                                
                                println("🔍 DEBUG: ViewModel'e kaydetme başlıyor")
                                // Önce ViewModel'e kaydet
                                viewModel?.addRecurringTransaction(
                                    name = title,
                                    amount = amountLong,
                                    categoryId = selectedCategory!!,
                                    accountId = 1L, // Default account
                                    type = type,
                                    frequency = frequency,
                                    startDate = startDate,
                                    endDate = if (hasEndDate) endDate else null,
                                    note = description
                                )
                                
                                println("🔍 DEBUG: onSave callback çağrılıyor")
                                // Sonra callback'i çağır (navigation'ı kapatır)
                                onSave(RecurringRuleData(
                                    title = title,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    categoryId = selectedCategory!!,
                                    type = selectedType,
                                    frequency = selectedFrequency,
                                    interval = interval.toIntOrNull() ?: 1,
                                    startDate = startDate,
                                    endDate = if (hasEndDate) endDate else null,
                                    description = description
                                ))
                                println("🔍 DEBUG: onSave callback tamamlandı")
                            } else {
                                println("🔍 DEBUG: Form invalid - title: '$title', amount: '$amount', category: $selectedCategory")
                            }
                        },
                        enabled = title.isNotBlank() && amount.isNotBlank() && selectedCategory != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
    
    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                startDate = date
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                endDate = date
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@Composable
fun DatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var day by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tarih Seçin",
                    style = SpendCraftTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Gün seçimi
                Text("Gün", style = SpendCraftTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = day.toString(),
                    onValueChange = { day = it.toIntOrNull() ?: 1 },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Ay seçimi
                Text("Ay", style = SpendCraftTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = (month + 1).toString(),
                    onValueChange = { month = (it.toIntOrNull() ?: 1) - 1 },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Yıl seçimi
                Text("Yıl", style = SpendCraftTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = year.toString(),
                    onValueChange = { year = it.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }
                    
                    Button(
                        onClick = { 
                            val calendar = Calendar.getInstance()
                            calendar.set(year, month, day)
                            onDateSelected(calendar.timeInMillis)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Seç")
                    }
                }
            }
        }
    }
}

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
