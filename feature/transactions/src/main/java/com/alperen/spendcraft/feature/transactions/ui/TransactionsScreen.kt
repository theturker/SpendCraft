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
import com.alperen.spendcraft.core.ui.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAdd: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onAllTransactions: () -> Unit,
    onDashboard: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onAchievements: () -> Unit = {},
    currentUserName: String? = null
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val streak by viewModel.streak.collectAsState()
    
    // Premium state - Get from ViewModel
    val isPremium = false // TODO: Implement premium state

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
                    "💰 ${context.getString(R.string.total_balance)}",
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
            Color(0xFF1A1B3A), // Koyu mor-mavi
            Color(0xFF0F0F23), // Daha koyu mor
            Color(0xFF0A0B1A)  // En koyu arka plan
        )
    )

    val shown = remember(items, txFilter) {
        val base = when (txFilter) {
            TxFilter.ALL -> items
            TxFilter.INCOME -> items.filter { it.type == TransactionType.INCOME }
            TxFilter.EXPENSE -> items.filter { it.type == TransactionType.EXPENSE }
        }
        base.take(5)
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
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.primary
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
            // (1) SpendCraft kartı — ALT YAZI GERİ
            item {
                GradientCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    onClick = { /* welcome anim */ }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SpendCraft",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (currentUserName != null) "Hoşgeldin, $currentUserName" else stringResource(R.string.welcome_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

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
                            painter = painterResource(R.drawable.ic_speed_vector),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hızlı Ekle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = onReports) { 
                            Text(stringResource(R.string.reports)) 
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionPill(
                            label = stringResource(R.string.add_income_button),
                            icon = painterResource(R.drawable.ic_income_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                initialTransactionType = true
                                showAddTransactionSheet = true
                            }
                        )
                        QuickActionPill(
                            label = stringResource(R.string.add_expense_button),
                            icon = painterResource(R.drawable.ic_expense_vector),
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
                            label = "Dashboard",
                            icon = painterResource(R.drawable.ic_dashboard_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = onDashboard
                        )
                        QuickActionPill(
                            label = "Bildirimler",
                            icon = painterResource(R.drawable.ic_notifications_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = onNotifications
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionPill(
                            label = "Başarımlar",
                            icon = painterResource(R.drawable.ic_trophy_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = onAchievements
                        )
                        QuickActionPill(
                            label = "Raporlar",
                            icon = painterResource(R.drawable.ic_analytics_vector),
                            positive = true,
                            modifier = Modifier.weight(1f),
                            onClick = onReports
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
                                painter = painterResource(R.drawable.ic_bar_chart_vector),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.transaction_statistics),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = onAllTransactions) { 
                            Text(stringResource(R.string.view_all)) 
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.this_month),
                            value = "${items.size} ${stringResource(R.string.transactions_count)}",
                            icon = painterResource(R.drawable.ic_calendar_month_vector),
                            color = Color(0xFF667EEA),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(R.string.average),
                            value = if (items.isNotEmpty()) formatMinor(totalAmount / items.size) else "₺0",
                            icon = painterResource(R.drawable.ic_analytics_vector),
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
                            painter = painterResource(R.drawable.ic_history_vector),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.recent_transactions),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    TextButton(onClick = onAllTransactions) { Text(stringResource(R.string.view_all)) }
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
                                text = stringResource(R.string.no_transactions_yet),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.start_adding_transactions),
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
    onClick: () -> Unit
) {
    val bg = if (positive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)

    val border = if (positive)
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
    else
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.outline))

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

    // Hero’daki hissi veren geçiş: mor->mavi (dark’ta hoş durur)
    val gradient = Brush.linearGradient(
        listOf(Color(0xFF6A5AE0), Color(0xFF4C8DFF))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account_balance_vector),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = { onEdit(selectedIndex) }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Düzenle",
                        tint = Color.White
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = account.balance,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))

            // Kapsüller: Gelir / Gider
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Capsule(text = "↑ ${account.income}", positive = true)
                Capsule(text = "↓ ${account.expenses}", positive = false)
            }

            // Altında küçük hesap seçicisi (dot tarzı)
            if (data.size > 1) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.forEachIndexed { i, _ ->
                        val selected = i == selectedIndex
                        Box(
                            modifier = Modifier
                                .size(if (selected) 10.dp else 8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (selected) Color.White
                                    else Color.White.copy(alpha = 0.35f)
                                )
                                .clickable { onSelect(i) }
                        )
                    }
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
                        painter = painterResource(if (isIncome) R.drawable.ic_income_vector else R.drawable.ic_expense_vector),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isIncome) stringResource(R.string.income) else stringResource(R.string.expense),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tx.note ?: stringResource(R.string.no_description),
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
                        contentDescription = stringResource(R.string.delete),
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
