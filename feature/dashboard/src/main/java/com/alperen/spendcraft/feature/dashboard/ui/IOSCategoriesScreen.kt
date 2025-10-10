package com.alperen.spendcraft.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS CategoriesView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Category list with icons and colors
 * - Budget information per category
 * - Progress bars for budget tracking
 * - Add category button
 * - Tap to add/edit budget
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSCategoriesScreen(
    categories: List<Category>,
    budgets: Map<Long, Double> = emptyMap(), // categoryId to budget limit
    spent: Map<Long, Double> = emptyMap(), // categoryId to spent amount
    onAddCategory: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Kategoriler",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onAddCategory) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Kategori Ekle"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryRow(
                    category = category,
                    budgetLimit = budgets[category.id],
                    spentAmount = spent[category.id] ?: 0.0,
                    onClick = {
                        selectedCategory = category
                        showAddBudgetDialog = true
                    }
                )
            }
        }
    }
    
    // Add Budget Dialog
    if (showAddBudgetDialog && selectedCategory != null) {
        AddBudgetDialog(
            category = selectedCategory!!,
            currentBudget = budgets[selectedCategory!!.id],
            onDismiss = {
                showAddBudgetDialog = false
                selectedCategory = null
            },
            onSave = { amount ->
                // TODO: Save budget
                showAddBudgetDialog = false
                selectedCategory = null
            }
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    budgetLimit: Double?,
    spentAmount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    val progress = if (budgetLimit != null && budgetLimit > 0) {
        (spentAmount / budgetLimit).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = CoreR.drawable.ic_category_vector),
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Category Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (budgetLimit != null) {
                        Text(
                            text = "Bütçe: ${CurrencyFormatter.format(context, (budgetLimit * 100).toLong())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Bütçe yok",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Spent Amount
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.format(context, (spentAmount * 100).toLong()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (budgetLimit != null) {
                        val progressPercent = (progress * 100).toInt()
                        val progressColor = when {
                            progress > 0.9 -> IOSColors.Red
                            progress > 0.7 -> IOSColors.Orange
                            else -> IOSColors.Green
                        }
                        
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.bodySmall,
                            color = progressColor
                        )
                    }
                }
            }
            
            // Budget Progress Bar
            if (budgetLimit != null) {
                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        progress > 0.9 -> IOSColors.Red
                        progress > 0.7 -> IOSColors.Orange
                        else -> IOSColors.Green
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddBudgetDialog(
    category: Category,
    currentBudget: Double?,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    val context = LocalContext.current
    var budgetAmount by remember { 
        mutableStateOf(currentBudget?.toString() ?: "") 
    }
    
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = CoreR.drawable.ic_category_vector),
                        contentDescription = null,
                        tint = categoryColor
                    )
                    Text(category.name)
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aylık Bütçe Limiti",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = { budgetAmount = it },
                    placeholder = { Text("0.00") },
                    trailingIcon = { Text("₺") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    budgetAmount.toDoubleOrNull()?.let { onSave(it) }
                },
                enabled = budgetAmount.toDoubleOrNull() != null
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

