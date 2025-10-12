package com.alperen.spendcraft.feature.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.data.db.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
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
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showEditAccountDialog by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Hesaplar",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = "Geri"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            // iOS-style FAB
            FloatingActionButton(
                onClick = { showAddAccountDialog = true },
                containerColor = IOSColors.Blue,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Hesap Ekle"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz hesap yok",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "İlk hesabınızı ekleyerek başlayın",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(accounts) { account ->
                    AccountItem(
                        account = account,
                        onEdit = { 
                            selectedAccount = account
                            showEditAccountDialog = true
                        },
                        onArchive = { onArchiveAccount(account) },
                        onSetDefault = { onSetDefaultAccount(account) }
                    )
                }
            }
            }
            
            // Add Account Dialog - iOS Style
            if (showAddAccountDialog) {
                IOSAccountDialog(
                    account = null,
                    onDismiss = { showAddAccountDialog = false },
                    onSave = { name, type, currency ->
                        // Create new account
                        onAddAccount()
                        showAddAccountDialog = false
                    }
                )
            }
            
            // Edit Account Dialog - iOS Style
            if (showEditAccountDialog && selectedAccount != null) {
                IOSAccountDialog(
                    account = selectedAccount,
                    onDismiss = {
                        showEditAccountDialog = false
                        selectedAccount = null
                    },
                    onSave = { name, type, currency ->
                        // Update account
                        selectedAccount?.let { onEditAccount(it) }
                        showEditAccountDialog = false
                        selectedAccount = null
                    }
                )
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getAccountTypeDisplayName(account.type),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (!account.isDefault) {
                        IconButton(onClick = { showArchiveDialog = true }) {
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_archive_vector),
                                contentDescription = "Arşivle",
                                tint = MaterialTheme.colorScheme.error
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
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (account.isDefault) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Varsayılan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                if (account.archived) {
                    Surface(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Arşivlendi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.surface,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorScreen(
    account: AccountEntity?,
    onSave: (AccountData) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(account?.name ?: "") }
    var type by remember { mutableStateOf(account?.type ?: "CASH") }
    var currency by remember { mutableStateOf(account?.currency ?: "TRY") }
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (account == null) "Yeni Hesap" else "Hesap Düzenle",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = "Geri"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Hesap Bilgileri",
                        style = MaterialTheme.typography.titleMedium,
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
                        style = MaterialTheme.typography.bodyMedium,
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
                                style = MaterialTheme.typography.bodyLarge
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

/**
 * iOS-style Account Dialog - İOS AccountsViewModel'deki addAccount benzeri
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IOSAccountDialog(
    account: AccountEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(account?.name ?: "") }
    var type by remember { mutableStateOf(account?.type ?: "CASH") }
    var currency by remember { mutableStateOf(account?.currency ?: "TRY") }
    
    data class AccountTypeItem(val typeValue: String, val typeName: String, val iconRes: Int)
    
    val accountTypes = listOf(
        AccountTypeItem("CASH", "Nakit", com.alperen.spendcraft.core.ui.R.drawable.ic_banknote),
        AccountTypeItem("BANK", "Banka Hesabı", com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
        AccountTypeItem("CARD", "Kredi Kartı", com.alperen.spendcraft.core.ui.R.drawable.ic_credit_card_vector)
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Text(
                    text = if (account == null) "Yeni Hesap" else "Hesap Düzenle",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Account Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hesap Adı") },
                    placeholder = { Text("Örn: Banka Hesabım") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Account Type - iOS Card Style
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Hesap Türü",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    accountTypes.forEach { accountType ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = if (type == accountType.typeValue) {
                                IOSColors.Blue.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(12.dp),
                            onClick = { type = accountType.typeValue }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = accountType.iconRes),
                                    contentDescription = null,
                                    tint = if (type == accountType.typeValue) IOSColors.Blue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                Text(
                                    text = accountType.typeName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (type == accountType.typeValue) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (type == accountType.typeValue) IOSColors.Blue else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                if (type == accountType.typeValue) {
                                    Icon(
                                        painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill),
                                        contentDescription = null,
                                        tint = IOSColors.Blue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Currency
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Para Birimi") },
                    placeholder = { Text("TRY") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name, type, currency)
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IOSColors.Blue
                        )
                    ) {
                        Text(if (account == null) "Ekle" else "Güncelle")
                    }
                }
            }
        }
    }
}

data class AccountData(
    val name: String,
    val type: String,
    val currency: String
)
