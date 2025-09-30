package com.alperen.spendcraft.core.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

data class BarData(
    val id: String,
    val value: Float,
    val label: String,
    val color: Color = Color.Blue
)

@Composable
fun BarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    onBarClick: (String) -> Unit = {},
    showValues: Boolean = true,
    showGrid: Boolean = true,
    animationDuration: Int = 1000,
    maxBars: Int = 10
) {
    if (data.isEmpty()) return
    
    val displayData = if (data.size > maxBars) {
        data.sortedByDescending { it.value }.take(maxBars)
    } else {
        data
    }
    
    val maxValue = displayData.maxOfOrNull { it.value } ?: 1f
    var selectedBar by remember { mutableStateOf<String?>(null) }
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(animationDuration),
        label = "bar_chart_animation"
    )
    
    Column(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val barWidth = size.width / displayData.size
                        val barIndex = (offset.x / barWidth).toInt()
                        if (barIndex in displayData.indices) {
                            val clickedBar = displayData[barIndex]
                            selectedBar = if (selectedBar == clickedBar.id) null else clickedBar.id
                            onBarClick(clickedBar.id)
                        }
                    }
                }
        ) {
            drawBarChart(
                data = displayData,
                maxValue = maxValue,
                progress = animatedProgress,
                selectedBar = selectedBar,
                showGrid = showGrid
            )
        }
        
        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            displayData.forEach { barData ->
                Text(
                    text = barData.label,
                    style = SpendCraftTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }
    }
}

private fun DrawScope.drawBarChart(
    data: List<BarData>,
    maxValue: Float,
    progress: Float,
    selectedBar: String?,
    showGrid: Boolean
) {
    val barWidth = size.width / data.size
    val chartHeight = size.height - 40f // Leave space for labels
    val barSpacing = barWidth * 0.1f
    val actualBarWidth = barWidth - barSpacing
    
    // Draw grid lines
    if (showGrid) {
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = (chartHeight / gridLines) * i
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
    
    // Draw bars
    data.forEachIndexed { index, barData ->
        val barHeight = (barData.value / maxValue) * chartHeight * progress
        val x = (barWidth * index) + (barSpacing / 2f)
        val y = chartHeight - barHeight
        
        val isSelected = selectedBar == barData.id
        val barColor = if (isSelected) {
            barData.color.copy(alpha = 0.8f)
        } else {
            barData.color
        }
        
        // Draw bar
        drawRect(
            color = barColor,
            topLeft = Offset(x, y),
            size = Size(actualBarWidth, barHeight)
        )
        
        // Draw value on top of bar
        if (barHeight > 20f) { // Only show value if bar is tall enough
            val valueText = "${barData.value.toInt()}"
            val textPaint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.BLACK
                textSize = 12.sp.toPx()
                isAntiAlias = true
            }
            
            val textBounds = android.graphics.Rect()
            textPaint.getTextBounds(valueText, 0, valueText.length, textBounds)
            
            val textX = x + (actualBarWidth / 2f) - (textBounds.width() / 2f)
            val textY = y - 5f
            
            drawContext.canvas.nativeCanvas.drawText(
                valueText,
                textX,
                textY,
                textPaint
            )
        }
    }
}

@Composable
fun HorizontalBarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    onBarClick: (String) -> Unit = {},
    showValues: Boolean = true,
    animationDuration: Int = 1000
) {
    if (data.isEmpty()) return
    
    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    var selectedBar by remember { mutableStateOf<String?>(null) }
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(animationDuration),
        label = "horizontal_bar_chart_animation"
    )
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { barData ->
            val isSelected = selectedBar == barData.id
            val barColor = if (isSelected) {
                barData.color.copy(alpha = 0.8f)
            } else {
                barData.color
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(barData.id) {
                        detectTapGestures {
                            selectedBar = if (selectedBar == barData.id) null else barData.id
                            onBarClick(barData.id)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label
                Text(
                    text = barData.label,
                    style = SpendCraftTheme.typography.bodyMedium,
                    modifier = Modifier.width(100.dp),
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val barWidth = (barData.value / maxValue) * size.width * animatedProgress
                        
                        drawRect(
                            color = barColor,
                            topLeft = Offset.Zero,
                            size = Size(barWidth, size.height)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Value
                if (showValues) {
                    Text(
                        text = "${barData.value.toInt()}",
                        style = SpendCraftTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
