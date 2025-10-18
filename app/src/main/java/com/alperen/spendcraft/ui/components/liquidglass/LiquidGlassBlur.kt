package com.alperen.spendcraft.ui.components.liquidglass

import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.random.Random

/**
 * Liquid Glass Blur Effect
 * - Android 12+ (API 31+): Real backdrop blur using RenderEffect
 * - Android 5-11 (API 21-30): Translucent glass with noise, saturation boost, gradient
 */

@Composable
fun LiquidGlassBlurSurface(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // API 31+ Real backdrop blur
        RealBackdropBlur(
            modifier = modifier,
            isDarkMode = isDarkMode,
            content = content
        )
    } else {
        // API < 31 Fallback: Translucent glass
        FallbackGlassEffect(
            modifier = modifier,
            isDarkMode = isDarkMode,
            content = content
        )
    }
}

/**
 * Real Backdrop Blur (API 31+)
 * Uses RenderEffect to blur content behind the bar
 */
@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun RealBackdropBlur(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val backgroundColor = if (isDarkMode) {
        LiquidGlassThemeTokens.Dark.glassBackground
    } else {
        LiquidGlassThemeTokens.Light.glassBackground
    }
    
    // Create blur effect
    val blurEffect = remember {
        RenderEffect.createBlurEffect(
            LiquidGlassThemeTokens.blurRadius,
            LiquidGlassThemeTokens.blurRadius,
            Shader.TileMode.CLAMP
        ).asComposeRenderEffect()
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                renderEffect = blurEffect
            }
            .background(backgroundColor)
    ) {
        // Noise overlay (2–3% alpha)
        ProceduralNoiseOverlay(
            modifier = Modifier.fillMaxSize(),
            alpha = LiquidGlassThemeTokens.noiseAlpha
        )
        
        // Content on top
        content()
    }
}

/**
 * Fallback Glass Effect (API < 31)
 * Translucent background + noise + subtle saturation + gradient
 */
@Composable
private fun FallbackGlassEffect(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val backgroundColor = if (isDarkMode) {
        LiquidGlassThemeTokens.Fallback.Dark.glassBackground
    } else {
        LiquidGlassThemeTokens.Fallback.Light.glassBackground
    }
    
    Box(
        modifier = modifier
            .background(backgroundColor)
    ) {
        // Top highlight gradient (specular rim)
        TopHighlightGradient(
            modifier = Modifier.fillMaxSize(),
            isDarkMode = isDarkMode
        )
        
        // Noise overlay
        ProceduralNoiseOverlay(
            modifier = Modifier.fillMaxSize(),
            alpha = LiquidGlassThemeTokens.noiseAlpha
        )
        
        // Content
        content()
    }
}

/**
 * Top highlight gradient for fallback mode
 * Simulates specular light reflection
 */
@Composable
private fun TopHighlightGradient(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    Canvas(modifier = modifier) {
        val gradientHeight = size.height * 0.3f
        val colors = if (isDarkMode) {
            listOf(
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.6f),
                Color.Transparent
            )
        }
        
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = colors,
                startY = 0f,
                endY = gradientHeight
            )
        )
    }
}

/**
 * Procedural Noise Overlay (2–3% alpha)
 * Generates a subtle texture for glass realism
 */
@Composable
private fun ProceduralNoiseOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.025f
) {
    val noisePattern = remember { generateNoisePattern(64, 64) }
    
    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                this.alpha = (alpha * 255).toInt()
            }
            
            // Tile the noise pattern
            val tileWidth = size.width / 8
            val tileHeight = size.height / 8
            
            for (y in 0..7) {
                for (x in 0..7) {
                    canvas.nativeCanvas.drawBitmap(
                        noisePattern,
                        x * tileWidth,
                        y * tileHeight,
                        paint
                    )
                }
            }
        }
    }
}

/**
 * Generate a small noise texture bitmap
 */
private fun generateNoisePattern(width: Int, height: Int): android.graphics.Bitmap {
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ALPHA_8)
    val random = Random(12345) // Fixed seed for consistency
    
    for (y in 0 until height) {
        for (x in 0 until width) {
            // Simple random noise
            val noise = random.nextFloat()
            val gray = (noise * 255).toInt()
            val color = android.graphics.Color.argb(gray, 255, 255, 255)
            bitmap.setPixel(x, y, color)
        }
    }
    
    return bitmap
}

/**
 * Vibrancy Effect (iOS-style color adjustment)
 * Increases saturation and contrast for content over blur
 */
@Composable
fun rememberVibrancyColorFilter(isDarkMode: Boolean): androidx.compose.ui.graphics.ColorFilter? {
    return remember(isDarkMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val saturation = if (isDarkMode) {
                LiquidGlassThemeTokens.Fallback.Dark.saturationBoost
            } else {
                LiquidGlassThemeTokens.Fallback.Light.saturationBoost
            }
            // Simple saturation boost (can be enhanced with ColorMatrix)
            null // For now, handled via color choices in ThemeTokens
        } else {
            null
        }
    }
}


