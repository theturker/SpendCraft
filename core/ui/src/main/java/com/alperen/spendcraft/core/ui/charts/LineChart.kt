package com.alperen.spendcraft.core.ui.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.*

data class LineDataPoint(
    val x: Float,
    val y: Float,
    val label: String
)

data class LineDataSet(
    val id: String,
    val data: List<LineDataPoint>,
    val color: Color,
    val label: String,
    val strokeWidth: Float = 3f
)

@Composable
fun LineChart(
    dataSets: List<LineDataSet>,
    modifier: Modifier = Modifier,
    onPointClick: (String, Int) -> Unit = { _, _ -> },
    showGrid: Boolean = true,
    showPoints: Boolean = true,
    animationDuration: Int = 1000
) {
    if (dataSets.isEmpty()) return
    
    val allDataPoints = dataSets.flatMap { it.data }
    val maxX = allDataPoints.maxOfOrNull { it.x } ?: 1f
    val minX = allDataPoints.minOfOrNull { it.x } ?: 0f
    val maxY = allDataPoints.maxOfOrNull { it.y } ?: 1f
    val minY = allDataPoints.minOfOrNull { it.y } ?: 0f
    
    var selectedPoint by remember { mutableStateOf<Pair<String, Int>?>(null) }
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(animationDuration),
        label = "line_chart_animation"
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
                        val point = findNearestPoint(
                            offset, 
                            dataSets, 
                            maxX, minX, maxY, minY, 
                            size
                        )
                        point?.let { (dataSetId, pointIndex) ->
                            selectedPoint = if (selectedPoint == Pair(dataSetId, pointIndex)) null else Pair(dataSetId, pointIndex)
                            onPointClick(dataSetId, pointIndex)
                        }
                    }
                }
        ) {
            drawLineChart(
                dataSets = dataSets,
                maxX = maxX,
                minX = minX,
                maxY = maxY,
                minY = minY,
                progress = animatedProgress,
                selectedPoint = selectedPoint,
                showGrid = showGrid,
                showPoints = showPoints
            )
        }
        
        // Legend
        if (dataSets.size > 1) {
            LineChartLegend(
                dataSets = dataSets,
                selectedDataSet = selectedPoint?.first,
                onDataSetClick = { dataSetId ->
                    selectedPoint = if (selectedPoint?.first == dataSetId) null else Pair(dataSetId, 0)
                }
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    dataSets: List<LineDataSet>,
    maxX: Float,
    minX: Float,
    maxY: Float,
    minY: Float,
    progress: Float,
    selectedPoint: Pair<String, Int>?,
    showGrid: Boolean,
    showPoints: Boolean
) {
    val padding = 40f
    val chartWidth = size.width - (padding * 2)
    val chartHeight = size.height - (padding * 2)
    
    // Draw grid
    if (showGrid) {
        drawGrid(maxX, minX, maxY, minY, padding, chartWidth, chartHeight)
    }
    
    // Draw lines
    dataSets.forEach { dataSet ->
        if (dataSet.data.size < 2) return@forEach
        
        val path = Path()
        val points = dataSet.data.map { dataPoint ->
            val x = padding + ((dataPoint.x - minX) / (maxX - minX)) * chartWidth
            val y = padding + chartHeight - ((dataPoint.y - minY) / (maxY - minY)) * chartHeight
            Offset(x, y)
        }
        
        // Create path
        path.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            val currentPoint = points[i]
            val previousPoint = points[i - 1]
            
            // Smooth curve between points
            val controlPoint1 = Offset(
                previousPoint.x + (currentPoint.x - previousPoint.x) * 0.3f,
                previousPoint.y
            )
            val controlPoint2 = Offset(
                currentPoint.x - (currentPoint.x - previousPoint.x) * 0.3f,
                currentPoint.y
            )
            
            path.cubicTo(
                controlPoint1.x, controlPoint1.y,
                controlPoint2.x, controlPoint2.y,
                currentPoint.x, currentPoint.y
            )
        }
        
        // Draw line
        drawPath(
            path = path,
            color = dataSet.color,
            style = Stroke(width = dataSet.strokeWidth)
        )
        
        // Draw points
        if (showPoints) {
            points.forEachIndexed { index, point ->
                val isSelected = selectedPoint == Pair(dataSet.id, index)
                val pointColor = if (isSelected) {
                    dataSet.color.copy(alpha = 0.8f)
                } else {
                    dataSet.color
                }
                
                drawCircle(
                    color = pointColor,
                    radius = if (isSelected) 6f else 4f,
                    center = point
                )
                
                // Draw white center for better visibility
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 3f else 2f,
                    center = point
                )
            }
        }
    }
}

private fun DrawScope.drawGrid(
    maxX: Float,
    minX: Float,
    maxY: Float,
    minY: Float,
    padding: Float,
    chartWidth: Float,
    chartHeight: Float
) {
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val strokeWidth = 1.dp.toPx()
    
    // Vertical grid lines
    val xSteps = 5
    for (i in 0..xSteps) {
        val x = padding + (chartWidth / xSteps) * i
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + chartHeight),
            strokeWidth = strokeWidth
        )
    }
    
    // Horizontal grid lines
    val ySteps = 5
    for (i in 0..ySteps) {
        val y = padding + (chartHeight / ySteps) * i
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
private fun LineChartLegend(
    dataSets: List<LineDataSet>,
    selectedDataSet: String?,
    onDataSetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        dataSets.forEach { dataSet ->
            val isSelected = selectedDataSet == dataSet.id
            val alpha = if (isSelected) 1f else 0.6f
            
            Row(
                modifier = Modifier
                    .pointerInput(dataSet.id) {
                        detectTapGestures {
                            onDataSetClick(dataSet.id)
                        }
                    }
                    .alpha(alpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, 3.dp)
                        .background(dataSet.color, androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                )
                
                Text(
                    text = dataSet.label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun findNearestPoint(
    offset: Offset,
    dataSets: List<LineDataSet>,
    maxX: Float,
    minX: Float,
    maxY: Float,
    minY: Float,
    canvasSize: IntSize
): Pair<String, Int>? {
    val padding = 40f
    val chartWidth = canvasSize.width - (padding * 2)
    val chartHeight = canvasSize.height - (padding * 2)
    
    var nearestPoint: Pair<String, Int>? = null
    var minDistance = Float.MAX_VALUE
    val threshold = 20f // pixels
    
    dataSets.forEach { dataSet ->
        dataSet.data.forEachIndexed { index, dataPoint ->
            val x = padding + ((dataPoint.x - minX) / (maxX - minX)) * chartWidth
            val y = padding + chartHeight - ((dataPoint.y - minY) / (maxY - minY)) * chartHeight
            val point = Offset(x, y)
            
            val distance = sqrt((offset.x - point.x).pow(2) + (offset.y - point.y).pow(2))
            if (distance < threshold && distance < minDistance) {
                minDistance = distance
                nearestPoint = Pair(dataSet.id, index)
            }
        }
    }
    
    return nearestPoint
}
