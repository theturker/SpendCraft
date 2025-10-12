package com.alperen.spendcraft.feature.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
import com.alperen.spendcraft.core.ui.charts.*
import com.alperen.spendcraft.core.ui.R as CoreR
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.groupBy

/**
 * iOS ReportsView'in birebir Android karşılığı
 * 
 * Sections:
 * 1. Period Selector (Segmented Control)
 * 2. Summary Cards (Income, Expense)
 * 3. Chart Type Selector
 * 4. Charts (Trend, Category, Comparison)
 * 5. AI Suggestions Button
 * 6. Category Breakdown
 * 7. Top Categories
 */

enum class ReportPeriod(val label: String) {
    WEEK("Hafta"),
    MONTH("Ay"),
    YEAR("Yıl")
}

enum class ChartType(val label: String) {
    TREND("Trend"),
    CATEGORY("Kategori"),
    COMPARISON("Karşılaştırma")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSReportsScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    totalIncome: Double,
    totalExpense: Double,
    onNavigateToAISuggestions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.MONTH) }
    var selectedChartType by remember { mutableStateOf(ChartType.TREND) }
    val context = LocalContext.current
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu ayarlayalım
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 32.sp,
        stop = 22.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Raporlar",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Period Selector
            item {
                PeriodSegmentedControl(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = { selectedPeriod = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 2. Summary Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        title = "Toplam Gelir",
                        amount = CurrencyFormatter.format(context, (totalIncome * 100).toLong()),
                        icon = CoreR.drawable.ic_arrow_down_circle_fill,
                        color = IOSColors.Green,
                        modifier = Modifier.weight(1f)
                    )
                    
                    SummaryCard(
                        title = "Toplam Gider",
                        amount = CurrencyFormatter.format(context, (totalExpense * 100).toLong()),
                        icon = CoreR.drawable.ic_arrow_up_circle_fill,
                        color = IOSColors.Red,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // 3. Chart Type Selector
            item {
                ChartTypeSegmentedControl(
                    selectedType = selectedChartType,
                    onTypeChange = { selectedChartType = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 4. Charts
            item {
                ChartContainer(
                    chartType = selectedChartType,
                    period = selectedPeriod,
                    transactions = transactions,
                    categories = categories,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 5. AI Suggestions Button
            item {
                AIsugggestionsButton(
                    onClick = onNavigateToAISuggestions,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // 6. Category Breakdown
            item {
                CategoryBreakdownSection(
                    transactions = transactions,
                    categories = categories,
                    totalExpense = totalExpense,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun PeriodSegmentedControl(
    selectedPeriod: ReportPeriod,
    onPeriodChange: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ReportPeriod.values().forEach { period ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (selectedPeriod == period) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onPeriodChange(period) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedPeriod == period) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedPeriod == period) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChartTypeSegmentedControl(
    selectedType: ChartType,
    onTypeChange: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ChartType.values().forEach { type ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (selectedType == type) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onTypeChange(type) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedType == type) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedType == type) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChartContainer(
    chartType: ChartType,
    period: ReportPeriod,
    transactions: List<Transaction>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Prepare data based on period
    val (incomeData, expenseData) = remember(transactions, period) {
        prepareChartData(transactions, period)
    }
    
    // Prepare category data
    val categorySpendingData = remember(transactions, categories) {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        categories.mapNotNull { category ->
            val spent = expenseTransactions
                .filter { it.categoryId == category.id }
                .sumOf { it.amount.minorUnits / 100.0 }
            
            if (spent > 0) {
                val categoryColor = try {
                    Color(android.graphics.Color.parseColor(category.color))
                } catch (e: Exception) {
                    IOSColors.Blue
                }
                Triple(category.name, spent, categoryColor)
            } else {
                null
            }
        }.sortedByDescending { it.second }.take(8)
    }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (chartType) {
                    ChartType.TREND -> "Gelir & Gider Trendi"
                    ChartType.CATEGORY -> "Kategori Dağılımı"
                    ChartType.COMPARISON -> "Kategori Karşılaştırması"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Actual Charts - iOS style
            when (chartType) {
                ChartType.TREND -> {
                    IOSTrendLineChart(
                        incomeData = incomeData,
                        expenseData = expenseData
                    )
                }
                ChartType.CATEGORY -> {
                    if (categorySpendingData.isNotEmpty()) {
                        IOSCategoryPieChart(
                            categoryData = categorySpendingData.map { it.first to it.second },
                            categoryColors = categorySpendingData.map { it.third }
                        )
                    } else {
                        EmptyChartState()
                    }
                }
                ChartType.COMPARISON -> {
                    if (categorySpendingData.isNotEmpty()) {
                        IOSComparisonBarChart(
                            categoryData = categorySpendingData
                        )
                    } else {
                        EmptyChartState()
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChartState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Henüz veri yok",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun prepareChartData(
    transactions: List<Transaction>,
    period: ReportPeriod
): Pair<List<Pair<String, Double>>, List<Pair<String, Double>>> {
    val now = System.currentTimeMillis()
    val daysBack = when (period) {
        ReportPeriod.WEEK -> 7
        ReportPeriod.MONTH -> 30
        ReportPeriod.YEAR -> 365
    }
    
    val dateFormat = SimpleDateFormat(
        when (period) {
            ReportPeriod.WEEK, ReportPeriod.MONTH -> "dd/MM"
            ReportPeriod.YEAR -> "MMM"
        },
        Locale("tr")
    )
    
    val incomeData = mutableListOf<Pair<String, Double>>()
    val expenseData = mutableListOf<Pair<String, Double>>()
    
    for (i in 0 until daysBack step if (period == ReportPeriod.YEAR) 30 else 1) {
        val dayMillis = now - (i * 24 * 60 * 60 * 1000L)
        val dayLabel = dateFormat.format(Date(dayMillis))
        
        val dayIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .filter { kotlin.math.abs(it.timestampUtcMillis - dayMillis) < 24 * 60 * 60 * 1000 }
            .sumOf { it.amount.minorUnits / 100.0 }
        
        val dayExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .filter { kotlin.math.abs(it.timestampUtcMillis - dayMillis) < 24 * 60 * 60 * 1000 }
            .sumOf { it.amount.minorUnits / 100.0 }
        
        incomeData.add(0, dayLabel to dayIncome)
        expenseData.add(0, dayLabel to dayExpense)
    }
    
    return incomeData to expenseData
}

@Composable
private fun AIsugggestionsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IOSColors.Purple.copy(alpha = 0.2f),
                            IOSColors.Blue.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_sparkles),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = IOSColors.Purple
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "AI Önerileri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Harcama alışkanlıklarınız hakkında öneriler alın",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownSection(
    transactions: List<Transaction>,
    categories: List<Category>,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Calculate category spending
    val categorySpending = remember(transactions, categories) {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        categories.mapNotNull { category ->
            val spent = expenseTransactions
                .filter { it.categoryId == category.id }
                .sumOf { it.amount.minorUnits / 100.0 }
            
            if (spent > 0) {
                category to spent
            } else {
                null
            }
        }.sortedByDescending { it.second }
    }
    
    if (categorySpending.isEmpty()) {
        return
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Kategoriye Göre Harcamalar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        categorySpending.forEach { (category, spent) ->
            CategorySpendingRow(
                category = category,
                spentAmount = spent,
                percentage = if (totalExpense > 0) spent / totalExpense else 0.0
            )
        }
    }
}

@Composable
private fun CategorySpendingRow(
    category: Category,
    spentAmount: Double,
    percentage: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_category_vector),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = CurrencyFormatter.format(context, (spentAmount * 100).toLong()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        LinearProgressIndicator(
            progress = { percentage.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = categoryColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

