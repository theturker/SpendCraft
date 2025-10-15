package com.alperen.spendcraft.ui.iosTheme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * iOS Shadow System
 * iOS uses shadow() modifier with color, radius, x, y parameters
 * Android uses elevation which approximates shadows
 * 
 * Note: For exact iOS parity, use androidx.compose.ui.draw.shadow() with custom parameters
 */
object IOSElevation {
    val none = 0.dp
    val small = 2.dp        // Subtle shadow (0, 4, blur 8)
    val medium = 4.dp       // Standard shadow (0, 4, blur 12)
    val large = 8.dp        // Prominent shadow (0, 8, blur 30)
    val button = 4.dp       // Button shadow (0, 8, blur 15)
}

/**
 * iOS Shadow Specs
 * For exact iOS shadow replication, use these parameters with Modifier.shadow()
 */
data class IOSShadow(
    val color: Color,
    val offsetX: Float = 0f,
    val offsetY: Float,
    val blurRadius: Float
)

object IOSShadows {
    val small = IOSShadow(
        color = Color.Black.copy(alpha = 0.05f),
        offsetY = 4f,
        blurRadius = 8f
    )
    
    val medium = IOSShadow(
        color = Color.Black.copy(alpha = 0.1f),
        offsetY = 4f,
        blurRadius = 12f
    )
    
    val large = IOSShadow(
        color = Color.Black.copy(alpha = 0.1f),
        offsetY = 8f,
        blurRadius = 30f
    )
    
    // Colored shadows for specific components
    fun buttonShadow(color: Color) = IOSShadow(
        color = color.copy(alpha = 0.3f),
        offsetY = 8f,
        blurRadius = 15f
    )
    
    fun cardShadow(color: Color = Color.Black) = IOSShadow(
        color = color.copy(alpha = 0.08f),
        offsetY = 2f,
        blurRadius = 8f
    )
}

