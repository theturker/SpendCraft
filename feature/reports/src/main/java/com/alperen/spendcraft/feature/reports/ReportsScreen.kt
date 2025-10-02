package com.alperen.spendcraft.feature.reports

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.StatCard
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.R
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.PI
import kotlin.math.sqrt
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ReportsScreen(
    transactionsFlow: StateFlow<List<Transaction>>,
    categoriesFlow: StateFlow<List<com.alperen.spendcraft.core.model.Category>>,
    onBack: () -> Unit = {},
    onExport: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(-1) }
    var showBarChart by remember { mutableStateOf(false) }
    val items by transactionsFlow.collectAsState()
    val categories by categoriesFlow.collectAsState()
    val totalExpense = items.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
    val totalIncome = items.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
    val netAmount = totalIncome - totalExpense
    
    // Premium state - Get from parameter
    val isPremium = false // TODO: Pass from parent

    // Kategori bazında harcama analizi - kategori isimleri ile
    val expenseByCategory = items
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount.minorUnits } }
        .map { (categoryId, amount) ->
            val categoryName = categories.find { it.id == categoryId }?.name ?: "Bilinmeyen Kategori"
            Triple(categoryId, categoryName, amount)
        }
        .sortedByDescending { it.third }

    AppScaffold(
        title = stringResource(R.string.reports),
        onBack = onBack,
        showBannerAd = true,
        isPremium = isPremium,
        bannerContent = {
            AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                isPremium = isPremium
            )
        },
        actions = {
            IconButton(onClick = onExport) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Raporu İndir"
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Özet Kartları
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.total_income),
                        value = formatCurrency(totalIncome),
                        icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_income_vector),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(R.string.total_expense),
                        value = formatCurrency(totalExpense),
                        icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_expense_vector),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Net Bakiye Kartı
            item {
                ModernCard(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
                                contentDescription = null,
                                tint = if (netAmount >= 0)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.net_balance),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = formatCurrency(netAmount),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (netAmount >= 0)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // AdMob Banner
            item {
                AdMobBannerWithPadding(isPremium = isPremium)
            }

            // Pie Chart - Harcama Dağılımı
            if (expenseByCategory.isNotEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_bar_chart_vector),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.expense_distribution),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                // Toggle Button
                                OutlinedButton(
                                    onClick = { showBarChart = !showBarChart },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (showBarChart) com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector 
                                            else com.alperen.spendcraft.core.ui.R.drawable.ic_bar_chart_vector
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (showBarChart) "Bar" else "Pie",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            if (showBarChart) {
                                ExpenseBarChart(
                                    data = expenseByCategory.take(8),
                                    selectedIndex = selectedIndex,
                                    onSelectedIndexChanged = { idx -> selectedIndex = idx },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                )
                            } else {
                                ExpensePieChart(
                                    expenseByCategory = expenseByCategory,
                                    totalExpense = totalExpense,
                                    selectedIndex = selectedIndex,
                                    onSelectedIndexChanged = { idx -> selectedIndex = idx },
                                    modifier = Modifier.size(260.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ExpenseLegend(
                                    expenseByCategory = expenseByCategory,
                                    totalExpense = totalExpense,
                                    selectedIndex = selectedIndex,
                                    onSelectedIndexChanged = { idx -> selectedIndex = idx }
                                )
                            }
                        }
                    }
                }
            }

            // Kategori harcamaları ikinci kartı kaldırıldı (üstte bar chart zaten var)

            // Harcama Dağılımı – İlk 5
            if (expenseByCategory.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.expense_distribution),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                items(expenseByCategory.take(5)) { (categoryId, categoryName, amount) ->
                    ExpenseCategoryItem(
                        categoryId = categoryId,
                        categoryName = categoryName,
                        amount = amount,
                        totalExpense = totalExpense
                    )
                }
            }

            // İşlem İstatistikleri
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_speed_vector),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.transaction_statistics),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.total_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${items.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.income_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${items.count { it.type == TransactionType.INCOME }}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.expense_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${items.count { it.type == TransactionType.EXPENSE }}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCategoryItem(
    categoryId: Long?,
    categoryName: String,
    amount: Long,
    totalExpense: Long
) {
    val percentage = if (totalExpense > 0) (amount.toFloat() / totalExpense.toFloat() * 100) else 0f
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.outline
    )
    val colorIndex = (categoryId?.toInt() ?: 0) % colors.size

    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Renkli çubuk
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors[colorIndex])
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fiyat tek satırda
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ExpensePieChart(
    expenseByCategory: List<Triple<Long?, String, Long>>,
    totalExpense: Long,
    selectedIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF4C5EE6), // Primary blue
        Color(0xFF10B981), // Green
        Color(0xFFF59E0B), // Orange
        Color(0xFFEF4444), // Red
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFF84CC16), // Lime
        Color(0xFFF97316), // Orange-500
        Color(0xFFEC4899), // Pink
        Color(0xFF6366F1)  // Indigo
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    radius = 200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Arka plan glow efekti
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 130f
                    ),
                    shape = CircleShape
                )
        )

        val bgColor = MaterialTheme.colorScheme.background

        Canvas(
            modifier = Modifier
                .size(200.dp)
                .pointerInput(expenseByCategory, totalExpense, selectedIndex) {
                    detectTapGestures { offset ->
                        val canvasSize = minOf(size.width, size.height)
                        val radius = canvasSize / 2f * 0.75f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        val distance = sqrt(dx * dx + dy * dy)

                        if (distance <= radius && totalExpense > 0) {
                            // 0° tepe (yukarı), saat yönü artacak şekilde açı
                            val angleFromTopCw =
                                ((Math.toDegrees(kotlin.math.atan2(dy, dx).toDouble()) + 450.0) % 360.0).toFloat()

                            var acc = 0f
                            var found = -1
                            expenseByCategory.forEachIndexed { i, (_, _, amount) ->
                                val sweep = (amount.toFloat() / totalExpense.toFloat()) * 360f
                                if (angleFromTopCw >= acc && angleFromTopCw < acc + sweep) {
                                    found = i
                                    return@forEachIndexed
                                }
                                acc += sweep
                            }
                            onSelectedIndexChanged(if (found == selectedIndex) -1 else found)
                        }
                    }
                }
        ) {
            val canvasSize = minOf(size.width, size.height)
            val radius = canvasSize / 2f * 0.75f
            val center = Offset(size.width / 2f, size.height / 2f)

            var startAngle = -90f // tepe

            expenseByCategory.forEachIndexed { index, (_, _, amount) ->
                val sweepAngle = (amount.toFloat() / totalExpense.toFloat()) * 360f
                val color = colors[index % colors.size]
                val isSelected = selectedIndex == index

                // 1) Seçiliyse arka glow
                if (isSelected) {
                    drawArc(
                        color = color.copy(alpha = 0.25f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius - 6.dp.toPx(), center.y - radius - 6.dp.toPx()),
                        size = Size((radius + 6.dp.toPx()) * 2, (radius + 6.dp.toPx()) * 2),
                        style = Fill
                    )
                }

                // 2) Ana dilim (daima DOLU çiz)
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Fill
                )

                // 3) Seçiliyse üstüne ince kontur (boşluk görünmez)
                if (isSelected) {
                    drawArc(
                        color = color.copy(alpha = 0.9f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                startAngle += sweepAngle
            }

            // Donut iç boşluk
            drawCircle(
                color = bgColor,
                radius = radius * 0.55f,
                center = center,
                style = Fill
            )
        }
    }
}

@Composable
private fun ExpenseBarChart(
    data: List<Triple<Long?, String, Long>>,
    selectedIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxValue = (data.maxOfOrNull { it.third } ?: 1).toFloat()
    val barColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.outline
    )

    Row(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, (_, label, value) ->
            val isSelected = selectedIndex == index
            val barHeightFraction = if (maxValue > 0f) value / maxValue else 0f
            val barColor = barColors[index % barColors.size]

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectedIndexChanged(if (isSelected) -1 else index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(barHeightFraction.coerceIn(0f, 1f))
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(10.dp))
                            .background(barColor.copy(alpha = if (isSelected) 1f else 0.9f))
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = barColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExpenseLegend(
    expenseByCategory: List<Triple<Long?, String, Long>>,
    totalExpense: Long,
    selectedIndex: Int = -1,
    onSelectedIndexChanged: (Int) -> Unit   // ← eklendi
) {
    val colors = listOf(
        Color(0xFF4C5EE6), // Primary blue
        Color(0xFF10B981), // Green
        Color(0xFFF59E0B), // Orange
        Color(0xFFEF4444), // Red
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFF84CC16), // Lime
        Color(0xFFF97316), // Orange-500
        Color(0xFFEC4899), // Pink
        Color(0xFF6366F1)  // Indigo
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        expenseByCategory.forEachIndexed { index, (_, categoryName, amount) ->
            val percentage = if (totalExpense > 0) (amount.toFloat() / totalExpense.toFloat() * 100) else 0f
            val color = colors[index % colors.size]
            val isSelected = selectedIndex == index

            // Kart zemini rengi (track için de bunu kullanacağız)
            val containerColor =
                if (isSelected) color.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isSelected) 12.dp else 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = if (isSelected) color.copy(alpha = 0.4f) else Color.Transparent,
                        spotColor = if (isSelected) color.copy(alpha = 0.4f) else Color.Transparent
                    )
                    .clickable { onSelectedIndexChanged(if (isSelected) -1 else index) }, // ← eklendi
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) {
                    BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color.copy(alpha = 0.4f)
                            )
                        )
                    )
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Renk göstergesi
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            color,
                                            color.copy(alpha = 0.8f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Column {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Progress bar track — seçiliyken kart zemini ile aynı
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(6.dp)
                                        .background(containerColor, shape = RoundedCornerShape(3.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width((70.dp * (percentage / 100f)).coerceAtLeast(6.dp))
                                            .background(color, shape = RoundedCornerShape(3.dp))
                                    )
                                }
                                Text(
                                    text = "${percentage.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = color,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatCurrency(amount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Long): String {
    val sign = if (amount < 0) "-" else ""
    val abs = kotlin.math.abs(amount)
    val major = abs / 100
    val cents = abs % 100

    val majorFormatted = major.toString().reversed().chunked(3).joinToString(".").reversed()

    return if (cents > 0) {
        "$sign₺$majorFormatted,${cents.toString().padStart(2, '0')}"
    } else {
        "$sign₺$majorFormatted"
    }
}
