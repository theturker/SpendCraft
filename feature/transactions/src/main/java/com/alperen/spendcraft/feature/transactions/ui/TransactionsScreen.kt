package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.R as CoreR

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAdd: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onAllTransactions: () -> Unit,
    onNotifications: () -> Unit = {},
    onAchievements: () -> Unit = {},
    currentUserName: String? = null,
    isPremium: Boolean = false
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val streak by viewModel.streak.collectAsState()
    
    // Premium state is provided by caller

    var selectedAccountIndex by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingAccountIndex by remember { mutableStateOf(0) }

    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var initialTransactionType by remember { mutableStateOf<Boolean?>(null) }
    var txFilter by remember { mutableStateOf(TxFilter.ALL) }

    val totalAmount = items.sumOf {
        if (it.type == TransactionType.INCOME) it.amount.minorUnits else -it.amount.minorUnits
    }
    val incomeAmount = items.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
    val expenseAmount = items.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }

    val accountsData = remember(accounts, items) { /* Sende olduğu gibi */
        if (accounts.isEmpty()) {
            listOf(
                AccountData(
                    null,
                    "💰 ${context.getString(CoreR.string.total_balance)}",
                    formatMinor(totalAmount),
                    formatMinor(incomeAmount),
                    formatMinor(expenseAmount)
                )
            )
        } else {
            accounts.mapIndexed { index, account ->
                val accountTransactions = if (index == 0) items else items.filter { it.accountId == account.id }
                val accountIncome = accountTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
                val accountExpense = accountTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
                val accountBalance = accountIncome - accountExpense
                AccountData(
                    id = account.id,
                    name = if (index == 0) "💰 ${account.name}" else "🏠 ${account.name}",
                    balance = formatMinor(accountBalance),
                    income = formatMinor(accountIncome),
                    expenses = formatMinor(accountExpense)
                )
            }
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF8FAFC)
        )
    )

    val shown = remember(items, txFilter) {
        val base = when (txFilter) {
            TxFilter.ALL -> items
            TxFilter.INCOME -> items.filter { it.type == TransactionType.INCOME }
            TxFilter.EXPENSE -> items.filter { it.type == TransactionType.EXPENSE }
        }
        // Son işlemler için tarihe göre sırala ve ilk 5'i al
        base.sortedByDescending { it.timestampUtcMillis }.take(5)
    }

    // Reklam yüksekliği - sadece reklam yüklendiğinde kullanılacak
    var isAdLoaded by remember { mutableStateOf(false) }
    val adHeight = if (isAdLoaded) 56.dp else 0.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Reklam: EN ÜSTTE SABİT - ayarlar butonu altında
        AdMobBanner(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 16.dp, end = 16.dp), // Ayarlar butonu altında
            isPremium = isPremium,
            onAdLoaded = { isAdLoaded = true },
            onAdFailedToLoad = { isAdLoaded = false }
        )

        // Sağ-üst ayarlar
        IconButton(
            onClick = onSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Icon(
                painter = painterResource(CoreR.drawable.settingsicon),
                contentDescription = stringResource(CoreR.string.settings),
                tint = Color.Unspecified
            )
        }

        // İçerik listesi — üstte reklam + altta FAB için padding
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = adHeight + 60.dp), // reklam üstte + FAB altta
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Hesap kartı (gradient + kapsüller)
            item {
                GradientAccountsSection(
                    data = accountsData,
                    selectedIndex = selectedAccountIndex,
                    onSelect = { selectedAccountIndex = it },
                    onEdit = {
                        editingAccountIndex = it
                        showEditDialog = true
                    }
                )
            }

            // Streak
            item { StreakBanner(streak = streak, onClick = { /*...*/ }) }

            // (3) Hızlı Ekle — kartları pill stiline çevirdik
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        Icon(
                            painter = painterResource(CoreR.drawable.walleticon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(28.dp)
                        )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hızlı Ekle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    // Başlık ile kartlar arasında ekstra boşluk
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionPill(
                            label = stringResource(CoreR.string.add_income_button),
                            icon = painterResource(CoreR.drawable.ic_income_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                initialTransactionType = true
                                showAddTransactionSheet = true
                            }
                        )
                        QuickActionPill(
                            label = stringResource(CoreR.string.add_expense_button),
                            icon = painterResource(CoreR.drawable.ic_expense_vector),
                            positive = false,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                initialTransactionType = false
                                showAddTransactionSheet = true
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionPill(
                            label = "Bildirimler",
                            icon = painterResource(CoreR.drawable.ic_notifications_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = onNotifications,
                            backgroundColor = Color(0xFF3B82F6).copy(alpha = 0.16f),
                            borderBrush = Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF3B82F6)))
                        )

                        QuickActionPill(
                            label = "Başarımlar",
                            icon = painterResource(CoreR.drawable.ic_trophy_vector),
                            positive = false,
                            modifier = Modifier.weight(1f),
                            onClick = onAchievements,
                            backgroundColor = Color(0xFFF59E0B).copy(alpha = 0.18f),
                            borderBrush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))
                        )

                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            // İstatistikler
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        Icon(
                            painter = painterResource(CoreR.drawable.statisticicon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(28.dp)
                        )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(CoreR.string.transaction_statistics),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        AssistChip(
                            onClick = onReports,
                            label = { Text(stringResource(CoreR.string.reports)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(CoreR.drawable.statisticicon),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(CoreR.string.this_month),
                            value = "${items.size} ${stringResource(CoreR.string.transactions_count)}",
                            icon = painterResource(CoreR.drawable.calendericon),
                            color = Color(0xFF667EEA),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(CoreR.string.average),
                            value = if (items.isNotEmpty()) formatMinor(totalAmount / items.size) else "₺0",
                            icon = painterResource(CoreR.drawable.rangeicon),
                            color = Color(0xFFF093FB),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // (reklam artık sticky olduğundan buradaki AdMob item'ı kaldırabilirsin // item { AdMobBannerWithPadding() })

            // Son hareketler
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(CoreR.drawable.reloadicon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(CoreR.string.recent_transactions),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    AssistChip(
                        onClick = onAllTransactions,
                        label = { Text(stringResource(CoreR.string.view_all)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(CoreR.drawable.reloadicon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            if (items.isEmpty()) {
                item {
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = null
                ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(CoreR.string.no_transactions_yet),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(CoreR.string.start_adding_transactions),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            } else {
                items(shown) { tx ->
                    ModernTransactionRow(
                        tx = tx,
                        onDelete = { tx.id?.let(viewModel::deleteTransaction) }
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }

    }

    if (showEditDialog && editingAccountIndex < accountsData.size) {
        val editingAccount = accountsData[editingAccountIndex]
        AccountNameEditDialog(
            currentName = editingAccount.name.removePrefix("💰 ").removePrefix("🏠 "),
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                editingAccount.id?.let { accountId ->
                    viewModel.updateAccountName(accountId, newName)
                }
            }
        )
    }

    if (showAddTransactionSheet) {
        AddTransactionBottomSheet(
            categories = viewModel.categories,
            initialTransactionType = initialTransactionType,
            onSave = { amountMinor, note, categoryId, isIncome ->
                viewModel.saveTransaction(amountMinor, note, categoryId, null, isIncome)
            },
            onDismiss = { showAddTransactionSheet = false }
        )
    }
}

/* -------------------------- Yeni: QuickActionPill -------------------------- */

@Composable
private fun QuickActionPill(
    label: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    positive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: Color? = null,
    borderBrush: Brush? = null
) {
    val bg = backgroundColor ?: if (positive) Color(0xFF34D399).copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.14f)

    val border = borderBrush ?: if (positive)
        Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
    else
        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171)))

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

private enum class TxFilter { ALL, INCOME, EXPENSE }

/* -------------------------- Yardımcı UI --------------------------- */

/** Gradient hesap bölümü: seçili hesap özet + gelir/gider kapsülleri */
@Composable
private fun GradientAccountsSection(
    data: List<AccountData>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    val account = data.getOrNull(selectedIndex) ?: return

    // Kredi kartı hissi veren açık tema gradienti
    val gradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .height(180.dp)
            .padding(18.dp)
    ) {
        // Dekor: EMV çip ve marka
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol üstte hesap adı
            Text(
                text = account.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.95f)
            )
            // Sağ üstte marka
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(CoreR.drawable.ic_app_mark),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Paratik",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Kart numarası kaldırıldı

        // Bakiye
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = "Bakiye",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = account.balance,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Capsule(text = "↑ ${account.income}", positive = true)
                Capsule(text = "↓ ${account.expenses}", positive = false)
            }
        }

        // Edit butonu
        IconButton(
            onClick = { onEdit(selectedIndex) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Düzenle",
                tint = Color.White
            )
        }

        // Sayfa göstergesi (dot)
        if (data.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                data.forEachIndexed { i, _ ->
                    val selected = i == selectedIndex
                    Box(
                        modifier = Modifier
                            .size(if (selected) 8.dp else 6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (selected) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                            .clickable { onSelect(i) }
                    )
                }
            }
        }
    }
}

/** Mini kapsül */
@Composable
private fun Capsule(text: String, positive: Boolean) {
    val bg = if (positive) Color(0xFF2ECC71).copy(alpha = 0.25f)
    else Color(0xFFE74C3C).copy(alpha = 0.25f)

    val border = if (positive)
        Brush.linearGradient(listOf(Color(0xFF2ECC71), Color(0x332ECC71)))
    else
        Brush.linearGradient(listOf(Color(0xFFE74C3C), Color(0x33E74C3C)))

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

/** Bölüm başlığı + opsiyonel sağ aksiyon */
@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) { Text(actionText) }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val bg by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        label = "chip-bg"
    )
    val borderBrush = if (selected) {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outlineVariant))
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, borderBrush, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun ModernTransactionRow(tx: Transaction, onDelete: () -> Unit) {
    val amount = if (tx.type == TransactionType.INCOME) tx.amount.minorUnits else -tx.amount.minorUnits
    val isIncome = tx.type == TransactionType.INCOME
    var isHovered by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.015f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isHovered) 1.5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rotation"
    )

    ModernCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .rotate(rotation)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    if (isIncome)
                        listOf(Color(0xFF48BB78), Color(0xFF2F855A), Color(0x2248BB78))
                    else
                        listOf(Color(0xFFF56565), Color(0xFFE53E3E), Color(0x22F56565))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                isHovered = !isHovered
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isIncome)
                                Brush.linearGradient(listOf(Color(0xFF48BB78), Color(0xFF38A169)))
                            else
                                Brush.linearGradient(listOf(Color(0xFFF56565), Color(0xFFE53E3E)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (isIncome) CoreR.drawable.ic_income_vector else CoreR.drawable.ic_expense_vector),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isIncome) stringResource(CoreR.string.income) else stringResource(CoreR.string.expense),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tx.note ?: stringResource(CoreR.string.no_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatMinor(amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(CoreR.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun formatMinor(minor: Long): String {
    val sign = if (minor < 0) "-" else ""
    val abs = kotlin.math.abs(minor)
    val major = abs / 100
    val cents = abs % 100
    return "$sign$major.${cents.toString().padStart(2, '0')}"
}
