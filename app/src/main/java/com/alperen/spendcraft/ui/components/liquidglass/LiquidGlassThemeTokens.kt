package com.alperen.spendcraft.ui.components.liquidglass

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Liquid Glass Bottom Bar Theme Tokens
 * iOS parity for colors, animations, dimensions
 */
object LiquidGlassThemeTokens {
    
    // ============================================
    // Colors - Light Mode
    // ============================================
    object Light {
        // Background blur base (applied over blurred content)
        val glassBackground = Color(0xF5F5F5F5) // ~96% white
        val glassBorder = Color(0x40FFFFFF) // Subtle white rim
        
        // Vibrancy adjustments for icons/labels
        val iconDefault = Color(0xFF8E8E93) // iOS gray
        val iconSelected = Color(0xFF007AFF) // iOS blue
        val labelDefault = Color(0xFF8E8E93)
        val labelSelected = Color(0xFF007AFF)
        
        // Selected pill indicator
        val pillBackground = Color(0x26007AFF) // 15% blue
        val pillGlow = Color(0x40007AFF) // Outer glow
    }
    
    // ============================================
    // Colors - Dark Mode
    // ============================================
    object Dark {
        // Background blur base
        val glassBackground = Color(0xE62C2C2E) // ~90% dark
        val glassBorder = Color(0x33FFFFFF) // Subtle white rim
        
        // Vibrancy adjustments
        val iconDefault = Color(0xFFAEAEB2) // iOS light gray
        val iconSelected = Color(0xFF0A84FF) // iOS blue (dark variant)
        val labelDefault = Color(0xFFAEAEB2)
        val labelSelected = Color(0xFF0A84FF)
        
        // Selected pill
        val pillBackground = Color(0x330A84FF) // 20% blue
        val pillGlow = Color(0x4D0A84FF) // Outer glow
    }
    
    // ============================================
    // Dimensions & Shapes
    // ============================================
    val barHeight: Dp = 56.dp
    val barCornerRadius: Dp = 28.dp // Full capsule
    val barHorizontalPadding: Dp = 12.dp
    val barBottomOffset: Dp = 12.dp // Floating offset from screen bottom
    
    val pillCornerRadius: Dp = 20.dp
    val pillPaddingHorizontal: Dp = 10.dp
    val pillPaddingVertical: Dp = 6.dp
    
    val iconSize: Dp = 26.dp // Daha büyük icon
    val iconLabelSpacing: Dp = 1.dp // Daha az boşluk
    val labelFontSize = 8 // sp - Küçültüldü ki sığsın
    
    // Shadow & Blur
    val shadowElevation: Dp = 8.dp
    val blurRadius: Float = 30f // px for BlurView
    val noiseAlpha: Float = 0.025f // 2.5% procedural noise
    
    // ============================================
    // Animation Durations & Springs
    // ============================================
    const val selectionAnimationDuration = 200 // ms (180–220ms range)
    const val barShowHideDuration = 140 // ms (120–160ms range)
    
    // Spring configs for bouncy feel (iOS-like)
    val selectionSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy, // ~0.6
        stiffness = Spring.StiffnessMedium // ~1500
    )
    
    val iconScaleSpring = spring<Float>(
        dampingRatio = 0.65f,
        stiffness = 400f
    )
    
    // Icon scale animation: 0.92 → 1.0 on selection
    const val iconScaleUnselected = 0.92f
    const val iconScaleSelected = 1.0f
    
    // Label slide-up offset
    val labelSlideUpOffset: Dp = 8.dp
    
    // ============================================
    // Fallback (API < 31) - Translucent Glass
    // ============================================
    object Fallback {
        object Light {
            val glassBackground = Color(0xBFF5F5F5) // 75% alpha
            val saturationBoost = 1.1f // Slight saturation
        }
        
        object Dark {
            val glassBackground = Color(0xB32C2C2E) // 70% alpha
            val saturationBoost = 1.05f
        }
    }
    
    // ============================================
    // Accessibility & Contrast
    // ============================================
    // Ensure WCAG AA compliance: 4.5:1 for labels
    const val minimumContrastRatio = 4.5f
}


