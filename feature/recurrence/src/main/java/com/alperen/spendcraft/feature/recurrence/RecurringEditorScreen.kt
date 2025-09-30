package com.alperen.spendcraft.feature.recurrence

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.PremiumGate
import java.text.SimpleDateFormat
import java.util.*

enum class RecurrenceFrequency(val displayName: String, val value: String) {
    DAILY("Günlük", "DAILY"),
    WEEKLY("Haftalık", "WEEKLY"),
    MONTHLY("Aylık", "MONTHLY"),
    YEARLY("Yıllık", "YEARLY")
}

@Composable
fun RecurringEditorScreen(
    templateTransaction: Transaction?,
    onSave: (RecurringRuleData) -> Unit,
    onCancel: () -> Unit,
    isPremium: Boolean
) {
    var frequency by remember { mutableStateOf(RecurrenceFrequency.MONTHLY) }
    var interval by remember { mutableStateOf(1) }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var hasEndDate by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    AppScaffold(
        title = "Tekrarlayan İşlem",
        onBack = onCancel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Template Transaction Info
            templateTransaction?.let { transaction ->
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Şablon İşlem",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${if (transaction.type == com.alperen.spendcraft.core.model.TransactionType.INCOME) "Gelir" else "Gider"}: ${transaction.amount.minorUnits / 100} TL",
                            style = SpendCraftTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Not: ${transaction.note ?: "Yok"}",
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Frequency Selection
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sıklık",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    RecurrenceFrequency.values().forEach { freq ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = frequency == freq,
                                    onClick = { frequency = freq }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = frequency == freq,
                                onClick = { frequency = freq }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = freq.displayName,
                                style = SpendCraftTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            
            // Interval
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Aralık",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (interval > 1) interval-- },
                            enabled = interval > 1
                        ) {
                            Icon(painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_remove_vector), contentDescription = null)
                        }
                        
                        Text(
                            text = "Her $interval ${frequency.displayName.lowercase()}",
                            style = SpendCraftTheme.typography.bodyLarge
                        )
                        
                        OutlinedButton(
                            onClick = { interval++ },
                            enabled = interval < 12
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            }
            
            // Start Date
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Başlangıç Tarihi",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = dateFormatter.format(Date(startDate)),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tarih") },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // End Date (Optional)
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = hasEndDate,
                            onCheckedChange = { hasEndDate = it }
                        )
                        Text(
                            text = "Bitiş tarihi belirle",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (hasEndDate) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Bitiş Tarihi") },
                            trailingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            // Premium Gate
            if (!isPremium) {
                PremiumGate(
                    title = "Premium Özellik",
                    description = "Tekrarlayan işlemler Premium üyeler için özel bir özelliktir.",
                    onUpgrade = { /* Navigate to paywall */ }
                )
            }
            
            // Action Buttons
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
                        val ruleData = RecurringRuleData(
                            title = templateTransaction?.note ?: "Tekrarlayan İşlem",
                            amount = templateTransaction?.amount?.minorUnits?.toDouble()?.div(100) ?: 0.0,
                            categoryId = templateTransaction?.categoryId ?: 0L,
                            type = if (templateTransaction?.type == com.alperen.spendcraft.core.model.TransactionType.INCOME) TransactionType.INCOME else TransactionType.EXPENSE,
                            frequency = Frequency.valueOf(frequency.name),
                            interval = interval,
                            startDate = startDate,
                            endDate = if (hasEndDate) endDate else null,
                            description = templateTransaction?.note ?: ""
                        )
                        onSave(ruleData)
                    },
                    enabled = isPremium && templateTransaction != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kaydet")
                }
            }
        }
    }
}

