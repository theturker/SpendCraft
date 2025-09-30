package com.alperen.spendcraft.core.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

data class PieSlice(
    val id: String,
    val value: Float,
    val label: String,
    val color: Color
)

@Composable
fun PieChart(
    data: List<PieSlice>,
    modifier: Modifier = Modifier,
    onSliceClick: (String) -> Unit = {},
    showLabels: Boolean = true,
    strokeWidth: Float = 0f,
    animationDuration: Int = 1000
) {
    if (data.isEmpty()) return
    
    val totalValue = data.sumOf { it.value.toDouble() }.toFloat()
    if (totalValue <= 0) return
    
    var selectedSlice by remember { mutableStateOf<String?>(null) }
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(animationDuration),
        label = "pie_chart_animation"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clipToBounds()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = minOf(size.width, size.height) / 2f
                        val angle = calculateAngleFromOffset(offset, center)
                        val clickedSlice = findSliceAtAngle(data, angle, totalValue)
                        clickedSlice?.let { slice ->
                            selectedSlice = if (selectedSlice == slice.id) null else slice.id
                            onSliceClick(slice.id)
                        }
                    }
                }
        ) {
            drawPieChart(
                data = data,
                totalValue = totalValue,
                progress = animatedProgress,
                selectedSlice = selectedSlice,
                strokeWidth = strokeWidth
            )
        }
        
        if (showLabels && data.size <= 8) {
            PieChartLegend(
                data = data,
                selectedSlice = selectedSlice,
                onSliceClick = { sliceId ->
                    selectedSlice = if (selectedSlice == sliceId) null else sliceId
                    onSliceClick(sliceId)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private fun DrawScope.drawPieChart(
    data: List<PieSlice>,
    totalValue: Float,
    progress: Float,
    selectedSlice: String?,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = minOf(size.width, size.height) / 2f - strokeWidth / 2f
    val innerRadius = radius * 0.6f // For donut chart effect
    
    var startAngle = -90f // Start from top
    
    data.forEach { slice ->
        val sliceAngle = (slice.value / totalValue) * 360f * progress
        val isSelected = selectedSlice == slice.id
        val sliceRadius = if (isSelected) radius * 1.1f else radius
        
        // Draw pie slice
        drawArc(
            color = slice.color,
            startAngle = startAngle,
            sweepAngle = sliceAngle,
            useCenter = true,
            topLeft = Offset(
                center.x - sliceRadius,
                center.y - sliceRadius
            ),
            size = Size(sliceRadius * 2f, sliceRadius * 2f)
        )
        
        // Draw stroke if needed
        if (strokeWidth > 0) {
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sliceAngle,
                useCenter = true,
                topLeft = Offset(
                    center.x - sliceRadius,
                    center.y - sliceRadius
                ),
                size = Size(sliceRadius * 2f, sliceRadius * 2f),
                style = Stroke(width = strokeWidth)
            )
        }
        
        startAngle += sliceAngle
    }
}

@Composable
private fun PieChartLegend(
    data: List<PieSlice>,
    selectedSlice: String?,
    onSliceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { slice ->
            val isSelected = selectedSlice == slice.id
            val alpha = if (isSelected) 1f else 0.6f
            
            Row(
                modifier = Modifier
                    .pointerInput(slice.id) {
                        detectTapGestures {
                            onSliceClick(slice.id)
                        }
                    }
                    .alpha(alpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(slice.color, CircleShape)
                )
                
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun calculateAngleFromOffset(offset: Offset, center: Offset): Double {
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    return (atan2(dy, dx) * 180f / PI + 90f + 360f) % 360f
}

private fun findSliceAtAngle(data: List<PieSlice>, angle: Double, totalValue: Float): PieSlice? {
    var currentAngle = 0f
    
    data.forEach { slice ->
        val sliceAngle = (slice.value / totalValue) * 360f
        if (angle >= currentAngle && angle < currentAngle + sliceAngle) {
            return slice
        }
        currentAngle += sliceAngle
    }
    
    return null
}
