package com.alperen.spendcraft.feature.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS AccountsListView ve AddAccountView'in birebir Android karşılığı
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSAccountsScreen(
    accounts: List<com.alperen.spendcraft.data.db.entities.AccountEntity>,
    onAddAccount: (name: String, type: String, currency: String) -> Unit,
    onDeleteAccount: (com.alperen.spendcraft.data.db.entities.AccountEntity) -> Unit,
    onSetDefaultAccount: (com.alperen.spendcraft.data.db.entities.AccountEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddAccountDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hesaplar",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    IconButton(onClick = { showAddAccountDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Hesap Ekle"
                        )
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(accounts) { account ->
                AccountRow(
                    account = account,
                    onSetDefault = { onSetDefaultAccount(account) },
                    onDelete = { onDeleteAccount(account) }
                )
            }
        }
    }
    
    // Add Account Dialog - iOS AddAccountView
    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onSave = { name, type, currency ->
                onAddAccount(name, type, currency)
                showAddAccountDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountRow(
    account: com.alperen.spendcraft.data.db.entities.AccountEntity,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(IOSColors.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.White
                )
            }
        },
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = account.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (account.isDefault) {
                        Surface(
                            color = IOSColors.Blue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Varsayılan",
                                style = MaterialTheme.typography.bodySmall,
                                color = IOSColors.Blue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AddAccountDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, currency: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CASH") }
    var selectedCurrency by remember { mutableStateOf("TRY") }
    
    val accountTypes = listOf(
        "CASH" to "Nakit",
        "BANK" to "Banka",
        "CREDIT_CARD" to "Kredi Kartı",
        "SAVINGS" to "Tasarruf"
    )
    
    val currencies = listOf("TRY", "USD", "EUR")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Hesap") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Field
                Text(
                    text = "Hesap Bilgileri",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Hesap Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Account Type
                Text(
                    text = "Hesap Tipi",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accountTypes.forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedType == type) IOSColors.Blue.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .clickable { selectedType = type }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = IOSColors.Blue
                                )
                            )
                            Text(
                                text = label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                
                // Currency
                Text(
                    text = "Para Birimi",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currencies.forEach { currency ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedCurrency == currency) IOSColors.Blue
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedCurrency = currency }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currency,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedCurrency == currency) Color.White 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedType, selectedCurrency) },
                enabled = name.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
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

