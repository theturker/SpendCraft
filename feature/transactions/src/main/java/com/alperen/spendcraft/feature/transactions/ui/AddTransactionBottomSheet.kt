package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    categories: StateFlow<List<Category>>,
    initialTransactionType: Boolean? = null,
    onSave: (amountMinor: Long, note: String?, categoryId: Long?, isIncome: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val cats by categories.collectAsState()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(cats.firstOrNull()?.id) }
    var isIncome by remember { mutableStateOf(initialTransactionType ?: false) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // HTML tasarımına uygun handle bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
            }
            
            // HTML tasarımına uygun title
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // HTML tasarımına uygun Category Selection
                item {
                    Column {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Food category (selected by default)
                            item {
                                ModernCategoryChip(
                                    text = "Food",
                                    icon = Icons.Filled.Menu,
                                    isSelected = selectedCategoryId == cats.firstOrNull()?.id,
                                    onClick = { selectedCategoryId = cats.firstOrNull()?.id }
                                )
                            }
                            // Transport category
                            item {
                                ModernCategoryChip(
                                    text = "Transport",
                                    icon = Icons.Filled.Menu,
                                    isSelected = false,
                                    onClick = { /* Handle transport selection */ }
                                )
                            }
                            // Shopping category
                            item {
                                ModernCategoryChip(
                                    text = "Shopping",
                                    icon = Icons.Filled.Menu,
                                    isSelected = false,
                                    onClick = { /* Handle shopping selection */ }
                                )
                            }
                            // Entertainment category
                            item {
                                ModernCategoryChip(
                                    text = "Entertainment",
                                    icon = Icons.Filled.Menu,
                                    isSelected = false,
                                    onClick = { /* Handle entertainment selection */ }
                                )
                            }
                            // Bills category
                            item {
                                ModernCategoryChip(
                                    text = "Bills",
                                    icon = Icons.Filled.Menu,
                                    isSelected = false,
                                    onClick = { /* Handle bills selection */ }
                                )
                            }
                            // Other category
                            item {
                                ModernCategoryChip(
                                    text = "Other",
                                    icon = Icons.Filled.Menu,
                                    isSelected = false,
                                    onClick = { /* Handle other selection */ }
                                )
                            }
                        }
                    }
                }
                
                // HTML tasarımına uygun Amount Input
                item {
                    Column {
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Dollar sign prefix
                            Text(
                                text = "₺",
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp)
                            )
                            
                            // Large amount input
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    val cleanValue = newValue.replace(Regex("[^0-9]"), "")
                                    if (cleanValue.length <= 8) {
                                        amount = cleanValue
                                    }
                                },
                                placeholder = { 
                                    Text(
                                        "0.00",
                                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) 
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(96.dp)
                                    .padding(start = 48.dp),
                                textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
                
                // HTML tasarımına uygun Date/Time and Note
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Date
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = "Today",
                                onValueChange = { },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        // Time
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = "10:00 AM",
                                onValueChange = { },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
                
                // Note Input
                item {
                    Column {
                        Text(
                            text = "Note",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { 
                                Text(
                                    "Add a note",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )
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
                                    value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: stringResource(R.string.select_category),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { 
                                        Text(
                                            stringResource(R.string.category),
                                            style = MaterialTheme.typography.bodyMedium
                                        ) 
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
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
            
            // HTML tasarımına uygun Save Button
            Button(
                onClick = {
                    val minor = amount.toLong()
                    onSave(minor, note.ifBlank { null }, selectedCategoryId, isIncome)
                    onDismiss()
                },
                enabled = amount.toLongOrNull() != null && amount.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ModernCategoryChip(
    text: String,
    icon: ImageVector,
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
                    imageVector = icon,
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
