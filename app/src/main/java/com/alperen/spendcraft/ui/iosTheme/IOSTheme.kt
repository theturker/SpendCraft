package com.alperen.spendcraft.ui.iosTheme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * iOS-like Theme for Android
 * 
 * This theme system replicates iOS design tokens exactly for pixel-perfect parity.
 * All color, typography, spacing, and radius values match iOS SwiftUI specifications.
 * 
 * Usage:
 * ```
 * IOSTheme {
 *     // Your composable content
 *     val colors = MaterialTheme.extendedColors
 *     Box(modifier = Modifier.background(colors.incomeBackground))
 * }
 * ```
 */

// Material3 ColorScheme for Light Mode (iOS equivalent)
private val IOSLightColorScheme = lightColorScheme(
    primary = IOSColors.Blue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.2f),
    onPrimaryContainer = IOSColors.Blue,
    
    secondary = IOSColors.Purple,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = IOSColors.Purple.copy(alpha = 0.2f),
    onSecondaryContainer = IOSColors.Purple,
    
    tertiary = IOSColors.Green,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = IOSColors.Green.copy(alpha = 0.2f),
    onTertiaryContainer = IOSColors.Green,
    
    error = IOSColors.Red,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = IOSColors.Red.copy(alpha = 0.2f),
    onErrorContainer = IOSColors.Red,
    
    background = IOSLightColors.background,
    onBackground = IOSLightColors.onBackground,
    
    surface = IOSLightColors.surface,
    onSurface = IOSLightColors.onSurface,
    surfaceVariant = IOSLightColors.surfaceVariant,
    onSurfaceVariant = IOSLightColors.onSurfaceVariant,
    
    outline = IOSLightColors.outline,
    outlineVariant = IOSLightColors.outlineVariant
)

// Material3 ColorScheme for Dark Mode (iOS equivalent)
private val IOSDarkColorScheme = darkColorScheme(
    primary = IOSColors.Blue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.3f),
    onPrimaryContainer = IOSColors.Blue,
    
    secondary = IOSColors.Purple,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = IOSColors.Purple.copy(alpha = 0.3f),
    onSecondaryContainer = IOSColors.Purple,
    
    tertiary = IOSColors.Green,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = IOSColors.Green.copy(alpha = 0.3f),
    onTertiaryContainer = IOSColors.Green,
    
    error = IOSColors.Red,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = IOSColors.Red.copy(alpha = 0.3f),
    onErrorContainer = IOSColors.Red,
    
    background = IOSDarkColors.background,
    onBackground = IOSDarkColors.onBackground,
    
    surface = IOSDarkColors.surface,
    onSurface = IOSDarkColors.onSurface,
    surfaceVariant = IOSDarkColors.surfaceVariant,
    onSurfaceVariant = IOSDarkColors.onSurfaceVariant,
    
    outline = IOSDarkColors.outline,
    outlineVariant = IOSDarkColors.outlineVariant
)

// CompositionLocal for extended colors (income, expense, streak, etc.)
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Access extended colors from MaterialTheme
 * 
 * Usage:
 * ```
 * val extendedColors = MaterialTheme.extendedColors
 * Box(modifier = Modifier.background(extendedColors.incomeBackground))
 * ```
 */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

@Composable
fun IOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) IOSDarkColorScheme else IOSLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    // Set status bar color to match iOS behavior
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? Activity)?.window
        window?.let {
            it.statusBarColor = colorScheme.background.toArgb()
            it.navigationBarColor = colorScheme.background.toArgb()
            
            // Make status bar icons dark in light mode, light in dark mode (iOS behavior)
            WindowCompat.getInsetsController(it, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IOSTypography,
            content = content
        )
    }
}

/**
 * iOS Navigation Transitions
 * Replicates iOS push/pop animations
 */
object IOSTransitions {
    const val DURATION = 300
    const val SHEET_DURATION = 500
    const val FADE_DURATION = 150
}

