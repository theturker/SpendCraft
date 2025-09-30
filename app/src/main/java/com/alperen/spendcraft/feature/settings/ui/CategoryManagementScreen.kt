package com.alperen.spendcraft.feature.settings.ui

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    AppScaffold(
        title = "📂 ${stringResource(R.string.category_management)}",
        onBack = onBack,
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_category))
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📊 ${stringResource(R.string.category_statistics)}",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatCard(
                                title = stringResource(R.string.total),
                                value = "${categories.size} ${stringResource(R.string.categories)}",
                                icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector),
                                color = SpendCraftTheme.colors.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            if (categories.isEmpty()) {
                item {
                    ModernCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz kategori yok",
                                style = SpendCraftTheme.typography.titleLarge,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "İlk kategorinizi ekleyerek başlayın",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(categories) { category ->
                    ModernCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = null,
                                tint = SpendCraftTheme.colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = category.name,
                                style = SpendCraftTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendCraftTheme.colors.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { category.id?.let { onDeleteCategory(it) } }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Sil",
                                    tint = SpendCraftTheme.colors.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Kategori Ekleme Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("Yeni Kategori Ekle")
            },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Kategori Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Filled.AccountBox, contentDescription = null)
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName.trim())
                            newCategoryName = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}
