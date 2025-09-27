package com.alperen.spendcraft.feature.recurrence

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
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.data.db.entities.RecurringRuleEntity
import com.alperen.spendcraft.core.model.Category
import java.util.*

@Composable
fun AddRecurringRuleScreen(
    categories: List<Category>,
    onSave: (RecurringRuleData) -> Unit,
    onCancel: () -> Unit
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
                            style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.bodyMedium,
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
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Kategori Seçimi
                        Text(
                            text = "Kategori",
                            style = MaterialTheme.typography.bodyMedium,
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Sıklık
                        Text(
                            text = "Sıklık",
                            style = MaterialTheme.typography.bodyMedium,
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
                                style = MaterialTheme.typography.bodyMedium
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
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Başlangıç Tarihi
                        Text(
                            text = "Başlangıç Tarihi",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(startDate)),
                            onValueChange = { },
                            label = { Text("Başlangıç Tarihi") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { /* TODO: Show date picker */ }) {
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
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (hasEndDate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = endDate?.let { 
                                    java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                                } ?: "",
                                onValueChange = { },
                                label = { Text("Bitiş Tarihi") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { /* TODO: Show date picker */ }) {
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (title.isNotBlank() && amount.isNotBlank()) {
                            Text(
                                text = "$title - ${amount} TL",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Her $interval ${selectedFrequency.unit} tekrarlanacak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (description.isNotBlank()) {
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = "Bilgileri doldurun",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            if (title.isNotBlank() && amount.isNotBlank() && selectedCategory != null) {
                                val ruleData = RecurringRuleData(
                                    title = title,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    categoryId = selectedCategory!!,
                                    type = selectedType,
                                    frequency = selectedFrequency,
                                    interval = interval.toIntOrNull() ?: 1,
                                    startDate = startDate,
                                    endDate = if (hasEndDate) endDate else null,
                                    description = description
                                )
                                onSave(ruleData)
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
