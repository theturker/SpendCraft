package com.alperen.spendcraft.feature.accounts

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.data.db.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Composable
fun AccountsScreen(
    accountsFlow: Flow<List<AccountEntity>>,
    onAddAccount: () -> Unit,
    onEditAccount: (AccountEntity) -> Unit,
    onArchiveAccount: (AccountEntity) -> Unit,
    onSetDefaultAccount: (AccountEntity) -> Unit,
    onBack: () -> Unit = {}
) {
    val accounts by accountsFlow.collectAsState(initial = emptyList())
    
    AppScaffold(
        title = "Hesaplar",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (accounts.isEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz hesap yok",
                                style = SpendCraftTheme.typography.titleMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            Text(
                                text = "İlk hesabınızı ekleyerek başlayın",
                                style = SpendCraftTheme.typography.bodyMedium,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(accounts) { account ->
                    AccountItem(
                        account = account,
                        onEdit = { onEditAccount(account) },
                        onArchive = { onArchiveAccount(account) },
                        onSetDefault = { onSetDefaultAccount(account) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountItem(
    account: AccountEntity,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onSetDefault: () -> Unit
) {
    var showArchiveDialog by remember { mutableStateOf(false) }
    
    ModernCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        getAccountTypeIcon(account.type),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = SpendCraftTheme.colors.primary
                    )
                    
                    Column {
                        Text(
                            text = account.name,
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getAccountTypeDisplayName(account.type),
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!account.isDefault) {
                        IconButton(onClick = onSetDefault) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Varsayılan yap",
                                tint = SpendCraftTheme.colors.primary
                            )
                        }
                    }
                    
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = SpendCraftTheme.colors.primary
                        )
                    }
                    
                    if (!account.isDefault) {
                        IconButton(onClick = { showArchiveDialog = true }) {
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_archive_vector),
                                contentDescription = "Arşivle",
                                tint = SpendCraftTheme.colors.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Para birimi: ${account.currency}",
                    style = SpendCraftTheme.typography.bodySmall,
                    color = SpendCraftTheme.colors.onSurfaceVariant
                )
                
                if (account.isDefault) {
                    Surface(
                        color = SpendCraftTheme.colors.primary,
                        shape = SpendCraftTheme.shapes.small
                    ) {
                        Text(
                            text = "Varsayılan",
                            style = SpendCraftTheme.typography.bodySmall,
                            color = SpendCraftTheme.colors.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                if (account.archived) {
                    Surface(
                        color = SpendCraftTheme.colors.onSurfaceVariant,
                        shape = SpendCraftTheme.shapes.small
                    ) {
                        Text(
                            text = "Arşivlendi",
                            style = SpendCraftTheme.typography.bodySmall,
                            color = SpendCraftTheme.colors.surface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
    
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Hesabı Arşivle") },
            text = { Text("Bu hesabı arşivlemek istediğinizden emin misiniz? Arşivlenen hesaplar işlemlerde görünmez.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onArchive()
                        showArchiveDialog = false
                    }
                ) {
                    Text("Arşivle")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showArchiveDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun AccountEditorScreen(
    account: AccountEntity?,
    onSave: (AccountData) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(account?.name ?: "") }
    var type by remember { mutableStateOf(account?.type ?: "CASH") }
    var currency by remember { mutableStateOf(account?.currency ?: "TRY") }
    
    AppScaffold(
        title = if (account == null) "Yeni Hesap" else "Hesap Düzenle",
        onBack = onCancel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Hesap Bilgileri",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Hesap Adı") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Hesap Türü",
                        style = SpendCraftTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val accountTypes = listOf(
                        "CASH" to "Nakit",
                        "CARD" to "Kredi Kartı",
                        "BANK" to "Banka"
                    )
                    
                    accountTypes.forEach { (typeValue, typeName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = type == typeValue,
                                    onClick = { type = typeValue }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = type == typeValue,
                                onClick = { type = typeValue }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = typeName,
                                style = SpendCraftTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Para Birimi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
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
                        val accountData = AccountData(
                            name = name,
                            type = type,
                            currency = currency
                        )
                        onSave(accountData)
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (account == null) "Ekle" else "Güncelle")
                }
            }
        }
    }
}

@Composable
private fun getAccountTypeIcon(type: String) = when (type) {
    "CASH" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector)
    "CARD" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_credit_card_vector)
    "BANK" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector)
    else -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector)
}

private fun getAccountTypeDisplayName(type: String) = when (type) {
    "CASH" -> "Nakit"
    "CARD" -> "Kredi Kartı"
    "BANK" -> "Banka"
    else -> type
}

data class AccountData(
    val name: String,
    val type: String,
    val currency: String
)
