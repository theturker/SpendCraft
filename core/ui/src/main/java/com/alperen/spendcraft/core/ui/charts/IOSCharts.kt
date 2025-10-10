package com.alperen.spendcraft.core.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.IOSColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * iOS Charts Framework benzeri chart bileşenleri
 * - TrendLineChart (iOS LineChart/AreaMark)
 * - CategoryPieChart (iOS SectorMark)  
 * - ComparisonBarChart (iOS BarMark)
 */

/**
 * iOS-style Line Chart with Area Fill
 */
@Composable
fun IOSTrendLineChart(
    incomeData: List<Pair<String, Double>>,
    expenseData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val maxValue = (incomeData.maxOfOrNull { it.second } ?: 0.0)
        .coerceAtLeast(expenseData.maxOfOrNull { it.second } ?: 0.0)
        .coerceAtLeast(1.0)
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chart Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (incomeData.size - 1).coerceAtLeast(1)
            
            // Draw Income Line and Area
            if (incomeData.isNotEmpty()) {
                val incomePath = Path()
                val incomeAreaPath = Path()
                
                incomeData.forEachIndexed { index, (_, value) ->
                    val x = index * spacing
                    val y = height - (value / maxValue * height).toFloat()
                    
                    if (index == 0) {
                        incomePath.moveTo(x, y)
                        incomeAreaPath.moveTo(x, height)
                        incomeAreaPath.lineTo(x, y)
                    } else {
                        incomePath.lineTo(x, y)
                        incomeAreaPath.lineTo(x, y)
                    }
                }
                
                incomeAreaPath.lineTo((incomeData.size - 1) * spacing, height)
                incomeAreaPath.close()
                
                // Draw area fill
                drawPath(
                    path = incomeAreaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            IOSColors.Green.copy(alpha = 0.3f),
                            IOSColors.Green.copy(alpha = 0.0f)
                        )
                    )
                )
                
                // Draw line
                drawPath(
                    path = incomePath,
                    color = IOSColors.Green,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            
            // Draw Expense Line and Area
            if (expenseData.isNotEmpty()) {
                val expensePath = Path()
                val expenseAreaPath = Path()
                
                expenseData.forEachIndexed { index, (_, value) ->
                    val x = index * spacing
                    val y = height - (value / maxValue * height).toFloat()
                    
                    if (index == 0) {
                        expensePath.moveTo(x, y)
                        expenseAreaPath.moveTo(x, height)
                        expenseAreaPath.lineTo(x, y)
                    } else {
                        expensePath.lineTo(x, y)
                        expenseAreaPath.lineTo(x, y)
                    }
                }
                
                expenseAreaPath.lineTo((expenseData.size - 1) * spacing, height)
                expenseAreaPath.close()
                
                // Draw area fill
                drawPath(
                    path = expenseAreaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            IOSColors.Red.copy(alpha = 0.3f),
                            IOSColors.Red.copy(alpha = 0.0f)
                        )
                    )
                )
                
                // Draw line
                drawPath(
                    path = expensePath,
                    color = IOSColors.Red,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(IOSColors.Green)
                )
                Text(
                    text = "Gelir",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(IOSColors.Red)
                )
                Text(
                    text = "Gider",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * iOS-style Pie Chart (Donut Chart)
 */
@Composable
fun IOSCategoryPieChart(
    categoryData: List<Pair<String, Double>>,
    categoryColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = categoryData.sumOf { it.second }.coerceAtLeast(1.0)
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = min(centerX, centerY) * 0.7f
            val innerRadius = radius * 0.5f // Donut hole
            
            var startAngle = -90f
            
            categoryData.forEachIndexed { index, (_, value) ->
                val sweepAngle = ((value / total) * 360).toFloat()
                val color = categoryColors.getOrElse(index) { IOSColors.Blue }
                
                // Draw pie slice
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )
                
                // Draw inner circle (donut hole)
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White,
                    radius = innerRadius,
                    center = Offset(centerX, centerY)
                )
                
                startAngle += sweepAngle
            }
        }
    }
}

/**
 * iOS-style Bar Chart
 */
@Composable
fun IOSComparisonBarChart(
    categoryData: List<Triple<String, Double, Color>>, // name, amount, color
    modifier: Modifier = Modifier
) {
    val maxValue = categoryData.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categoryData.forEach { (name, value, color) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Name
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(80.dp)
                )
                
                // Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((value / maxValue).toFloat())
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(color.copy(alpha = 0.8f), color)
                                )
                            )
                    )
                }
                
                // Value
                Text(
                    text = "${value.toInt()}₺",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}

