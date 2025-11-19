package com.alperen.spendcraft.feature.accounts

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.data.db.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    accountsFlow: Flow<List<AccountEntity>>,
    onAddAccount: (String, String, String) -> Unit,  // iOS: name, type, currency
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
    
    // Scroll oranına göre text boyutunu ayarlayalım - daha çok küçülsün
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 34.sp,
        stop = 17.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                LargeTopAppBar(
                    title = { Spacer(modifier = Modifier) },
                navigationIcon = {
                    val context = LocalContext.current
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = stringResource(R.string.accounts_back)
                        )
                    }
                },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (collapsedFraction > 0.5f) 64.dp else 152.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = if (collapsedFraction > 0.5f) {
                        Alignment.Center
                    } else {
                        Alignment.BottomStart
                    }
                ) {
                    Text(
                        text = stringResource(R.string.accounts_title),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = if (collapsedFraction > 0.5f) {
                            Modifier
                        } else {
                            Modifier.padding(start = 16.dp, bottom = 8.dp)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            // iOS-style FAB
            FloatingActionButton(
                onClick = { showAddAccountDialog = true },
                containerColor = IOSColors.Blue,
                contentColor = Color.White
            ) {
                val context = LocalContext.current
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.accounts_add)
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
                            val context = LocalContext.current
                            Text(
                                text = stringResource(R.string.accounts_empty_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.accounts_empty_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = accounts,
                    key = { it.id }
                ) { account ->
                    // iOS: .swipeActions(edge: .trailing, allowsFullSwipe: false)
                    // SettingsView.swift:340-353
                    val dismissState = rememberSwipeToDismissBoxState()
                    
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            // iOS: 2 actions - Delete (red) and Set Default (blue)
                            val color = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> IOSColors.Red
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Varsayılan yap - iOS: Label("Varsayılan", systemImage: "star")
                                    if (!account.isDefault) {
                                        IconButton(
                                            onClick = { onSetDefaultAccount(account) }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_star_fill),
                                                contentDescription = "Varsayılan",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    
                                    // Sil - iOS: Label("Sil", systemImage: "trash")
                                    IconButton(
                                        onClick = { onArchiveAccount(account) }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_trash_fill),
                                            contentDescription = "Sil",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        },
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = false
                    ) {
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
            }
            
            // Add Account Dialog - iOS Style
            if (showAddAccountDialog) {
                IOSAccountDialog(
                    account = null,
                    onDismiss = { showAddAccountDialog = false },
                    onSave = { name, type, currency ->
                        // iOS: accountsViewModel.addAccount(name: name, type: type, currency: currency)
                        onAddAccount(name, type, currency)
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
                        // Update account with new values
                        selectedAccount?.let { acc ->
                            val updated = acc.copy(name = name, type = type, currency = currency)
                            onEditAccount(updated)
                        }
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
                    
                    val context = LocalContext.current
                    Column {
                        Text(
                            text = com.alperen.spendcraft.core.ui.AccountLocalization.localize(context, account.name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getAccountTypeDisplayName(context, account.type),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!account.isDefault) {
                        val context = LocalContext.current
                        IconButton(onClick = onSetDefault) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = stringResource(R.string.accounts_set_default),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    val context = LocalContext.current
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.accounts_edit_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (!account.isDefault) {
                        val context = LocalContext.current
                        IconButton(onClick = { showArchiveDialog = true }) {
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_archive_vector),
                                contentDescription = stringResource(R.string.accounts_archive_description),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.accounts_currency_label, account.currency),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (account.isDefault) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.accounts_default),
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
                            text = stringResource(R.string.accounts_archived),
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
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text(stringResource(R.string.accounts_archive_dialog_title)) },
            text = { Text(stringResource(R.string.accounts_archive_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onArchive()
                        showArchiveDialog = false
                    }
                ) {
                    Text(stringResource(R.string.accounts_archive))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showArchiveDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
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
                    val context = LocalContext.current
                    Text(
                        text = if (account == null) stringResource(R.string.accounts_new) else stringResource(R.string.accounts_edit),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    val context = LocalContext.current
                    IconButton(onClick = onCancel) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = stringResource(R.string.accounts_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
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
                    val context = LocalContext.current
                    Text(
                        text = stringResource(R.string.accounts_info_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.accounts_account_name_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(R.string.accounts_account_type_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // ✅ iOS AddAccountView.swift:382 ile birebir aynı
                    val accountTypes = listOf(
                        "CASH" to stringResource(R.string.account_type_cash),
                        "BANK" to stringResource(R.string.account_type_bank),
                        "CREDIT_CARD" to stringResource(R.string.account_type_credit_card),
                        "SAVINGS" to stringResource(R.string.account_type_savings)
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
                    
                    // ✅ iOS: Currency Picker (AddAccountView.swift:398-402)
                    Text(
                        text = stringResource(R.string.accounts_currency),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val currencies = listOf("TRY", "USD", "EUR")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { curr ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = if (currency == curr) {
                                    IOSColors.Blue.copy(alpha = 0.2f)
                                } else {
                                    Color.Gray.copy(alpha = 0.1f)
                                },
                                onClick = { currency = curr }
                            ) {
                                Box(
                                    modifier = Modifier.padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (currency == curr) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currency == curr) IOSColors.Blue else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val context = LocalContext.current
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
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
                    Text(if (account == null) stringResource(R.string.accounts_add_button) else stringResource(R.string.accounts_update_button))
                }
            }
        }
    }
}

@Composable
private fun getAccountTypeIcon(type: String) = when (type) {
    "CASH" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_banknote)
    "BANK" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector)
    "CREDIT_CARD", "CARD" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_credit_card_vector)
    "SAVINGS" -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_savings)
    else -> painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector)
}

@Composable
private fun getAccountTypeDisplayName(context: android.content.Context, type: String) = when (type) {
    "CASH" -> context.getString(R.string.account_type_cash)
    "BANK" -> context.getString(R.string.account_type_bank)
    "CREDIT_CARD", "CARD" -> context.getString(R.string.account_type_credit_card)
    "SAVINGS" -> context.getString(R.string.account_type_savings)
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
    
    val context = LocalContext.current
    // ✅ iOS AddAccountView.swift:382 ile birebir aynı
    val accountTypes = listOf(
        AccountTypeItem("CASH", context.getString(R.string.account_type_cash), com.alperen.spendcraft.core.ui.R.drawable.ic_banknote),
        AccountTypeItem("BANK", context.getString(R.string.account_type_bank), com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
        AccountTypeItem("CREDIT_CARD", context.getString(R.string.account_type_credit_card), com.alperen.spendcraft.core.ui.R.drawable.ic_credit_card_vector),
        AccountTypeItem("SAVINGS", context.getString(R.string.account_type_savings), com.alperen.spendcraft.core.ui.R.drawable.ic_savings)
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
                val context = LocalContext.current
                Text(
                    text = if (account == null) stringResource(R.string.accounts_new) else stringResource(R.string.accounts_edit),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Account Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.accounts_account_name_placeholder)) },
                    placeholder = { Text("Örn: Banka Hesabım") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Account Type - iOS Card Style
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.accounts_account_type_label),
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
                
                // Currency Picker - iOS AddAccountView.swift:398-402
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.accounts_currency),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    // iOS: Picker with TRY, USD, EUR
                    val currencies = listOf("TRY", "USD", "EUR")
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currencies.forEach { curr ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                color = if (currency == curr) {
                                    IOSColors.Blue.copy(alpha = 0.1f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(12.dp),
                                onClick = { currency = curr }
                            ) {
                                Box(
                                    modifier = Modifier.padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (currency == curr) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (currency == curr) IOSColors.Blue else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
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
                        Text(if (account == null) stringResource(R.string.accounts_add_button) else stringResource(R.string.accounts_update_button))
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
