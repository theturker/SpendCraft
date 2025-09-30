package com.alperen.spendcraft.core.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AnimatedCounter(
    targetValue: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
    color: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    durationMillis: Int = 1000,
    prefix: String = "",
    suffix: String = ""
) {
    var currentValue by remember { mutableStateOf(0L) }
    
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "counter"
    )
    
    LaunchedEffect(animatedValue) {
        currentValue = animatedValue.toLong()
    }
    
    Box(modifier = modifier) {
        Text(
            text = "$prefix${formatCurrency(currentValue)}$suffix",
            style = style,
            color = color
        )
    }
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "progress"
    )
    
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(backgroundColor)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(
                    color = color,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                )
        )
    }
}

@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String? = null,
    expanded: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        animationSpec = tween(300),
        label = "rotation"
    )
    
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .rotate(rotation)
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

private fun formatCurrency(amount: Long): String {
    val major = amount / 100
    val minor = amount % 100
    return "$major,${minor.toString().padStart(2, '0')}"
}
