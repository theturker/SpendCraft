package com.alperen.spendcraft.feature.reports

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import androidx.compose.ui.res.stringResource

@Composable
fun ReportsScreen(
    transactionsFlow: StateFlow<List<Transaction>>,
    categoriesFlow: StateFlow<List<com.alperen.spendcraft.core.model.Category>>,
    onBack: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val items by transactionsFlow.collectAsState()
    val categories by categoriesFlow.collectAsState()
    val totalExpense = items.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
    val totalIncome = items.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
    val netAmount = totalIncome - totalExpense
    
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
        title = "📊 ${stringResource(R.string.reports)}",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.net_balance),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                AdMobBannerWithPadding()
            }
            
            // Pie Chart - Harcama Dağılımı
            if (expenseByCategory.isNotEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊 ${stringResource(R.string.expense_distribution)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Pie Chart
                            ExpensePieChart(
                                expenseByCategory = expenseByCategory,
                                totalExpense = totalExpense,
                                selectedIndex = selectedIndex,
                                onSelectedIndexChanged = { selectedIndex = it },
                                modifier = Modifier.size(200.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Legend
                    ExpenseLegend(
                        expenseByCategory = expenseByCategory,
                        totalExpense = totalExpense,
                        selectedIndex = selectedIndex
                    )
                        }
                    }
                }
            }
            
            // Harcama Dağılımı
            if (expenseByCategory.isNotEmpty()) {
                item {
                    Text(
                        text = "📈 ${stringResource(R.string.expense_distribution)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📊 ${stringResource(R.string.transaction_statistics)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.total_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${items.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.income_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${items.count { it.type == TransactionType.INCOME }}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.expense_transactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
    
    // Animasyonlar
    val infiniteTransition = rememberInfiniteTransition(label = "chart_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
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
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 140f
                    ),
                    shape = CircleShape
                )
        )
        
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size(240.dp)
                .pointerInput(expenseByCategory) {
                    detectTapGestures { offset ->
                        val canvasSize = minOf(size.width, size.height)
                        val radius = canvasSize / 2f * 0.8f
                        val center = androidx.compose.ui.geometry.Offset(
                            size.width / 2f,
                            size.height / 2f
                        )
                        
                        val distance = kotlin.math.sqrt(
                            (offset.x - center.x) * (offset.x - center.x) + 
                            (offset.y - center.y) * (offset.y - center.y)
                        )
                        
                        if (distance <= radius) {
                            // Açıyı hesapla (Canvas -90 dereceden başlar, yani üstten)
                            val angle = kotlin.math.atan2(
                                offset.y - center.y,
                                offset.x - center.x
                            ) * 180f / kotlin.math.PI
                            
                            // Açıyı 0-360 aralığına normalize et (üstten başlayarak saat yönünde)
                            val normalizedAngle = (angle + 90f + 360f) % 360f
                            
                            var currentAngle = 0f
                            var foundIndex = -1
                            
                            expenseByCategory.forEachIndexed { index, (_, _, amount) ->
                                val sweepAngle = (amount.toFloat() / totalExpense.toFloat()) * 360f
                                
                                // Segment'in başlangıç ve bitiş açılarını kontrol et
                                if (normalizedAngle >= currentAngle && normalizedAngle < currentAngle + sweepAngle) {
                                    foundIndex = index
                                    return@forEachIndexed
                                }
                                currentAngle += sweepAngle
                            }
                            
                            onSelectedIndexChanged(if (foundIndex == selectedIndex) -1 else foundIndex)
                        }
                    }
                }
        ) {
            val canvasSize = minOf(size.width, size.height)
            val radius = canvasSize / 2f * 0.8f
            val center = androidx.compose.ui.geometry.Offset(
                size.width / 2f,
                size.height / 2f
            )

            var startAngle = -90f // Start from top

            expenseByCategory.forEachIndexed { index, (_, _, amount) ->
                val sweepAngle = (amount.toFloat() / totalExpense.toFloat()) * 360f
                val color = colors[index % colors.size]
                val isSelected = selectedIndex == index
                val strokeWidth = if (isSelected) 6.dp.toPx() else 0f
                val radiusOffset = if (isSelected) 15.dp.toPx() else 0f
                
                // Calculate offset for selected segment
                val midAngle = startAngle + sweepAngle / 2f
                val offsetX = kotlin.math.cos(midAngle * kotlin.math.PI / 180.0).toFloat() * radiusOffset
                val offsetY = kotlin.math.sin(midAngle * kotlin.math.PI / 180.0).toFloat() * radiusOffset
                val offsetCenter = androidx.compose.ui.geometry.Offset(
                    center.x + offsetX,
                    center.y + offsetY
                )
                
                // Gradient efekti için
                val gradientColors = listOf(
                    color,
                    color.copy(alpha = 0.8f)
                )
                
                // Ana segment
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        offsetCenter.x - radius,
                        offsetCenter.y - radius
                    ),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = if (strokeWidth > 0) Stroke(width = strokeWidth) else androidx.compose.ui.graphics.drawscope.Fill
                )
                
                // Seçili segment için glow efekti
                if (isSelected) {
                    drawArc(
                        color = color.copy(alpha = 0.3f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            offsetCenter.x - radius - 8.dp.toPx(),
                            offsetCenter.y - radius - 8.dp.toPx()
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            (radius + 8.dp.toPx()) * 2, 
                            (radius + 8.dp.toPx()) * 2
                        ),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )
                }
                
                startAngle += sweepAngle
            }
        }
        
    }
}

@Composable
private fun ExpenseLegend(
    expenseByCategory: List<Triple<Long?, String, Long>>,
    totalExpense: Long,
    selectedIndex: Int = -1
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        expenseByCategory.forEachIndexed { index, (_, categoryName, amount) ->
            val percentage = if (totalExpense > 0) (amount.toFloat() / totalExpense.toFloat() * 100) else 0f
            val color = colors[index % colors.size]
            val isSelected = selectedIndex == index
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isSelected) 12.dp else 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = if (isSelected) color.copy(alpha = 0.3f) else color.copy(alpha = 0.1f),
                        spotColor = if (isSelected) color.copy(alpha = 0.3f) else color.copy(alpha = 0.1f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) 
                        color.copy(alpha = 0.1f) 
                    else 
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Enhanced color indicator with gradient
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            color,
                                            color.copy(alpha = 0.8f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .shadow(
                                    elevation = 6.dp,
                                    shape = CircleShape,
                                    ambientColor = color.copy(alpha = 0.4f),
                                    spotColor = color.copy(alpha = 0.4f)
                                )
                        )

                        Column {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Progress bar
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(4.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width((60.dp * (percentage / 100f)).coerceAtLeast(4.dp))
                                            .background(
                                                color,
                                                shape = RoundedCornerShape(2.dp)
                                            )
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

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = formatCurrency(amount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₺${(amount / 100).toString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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




