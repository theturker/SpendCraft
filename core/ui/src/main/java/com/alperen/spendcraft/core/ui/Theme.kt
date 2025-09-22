package com.alperen.spendcraft.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern Light Theme Colors
private val LightColors = lightColorScheme(
    primary = Color(0xFF6366F1), // Indigo
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    
    secondary = Color(0xFF10B981), // Emerald
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    
    tertiary = Color(0xFF8B5CF6), // Violet
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF4C1D95),
    
    error = Color(0xFFEF4444), // Red
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    
    background = Color(0xFFF8FAFC), // Slate 50
    onBackground = Color(0xFF1E293B), // Slate 800
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onSurfaceVariant = Color(0xFF64748B), // Slate 500
    
    outline = Color(0xFFE2E8F0), // Slate 200
    outlineVariant = Color(0xFFF1F5F9), // Slate 100
    scrim = Color(0xFF000000)
)

// Modern Dark Theme Colors
private val DarkColors = darkColorScheme(
    primary = Color(0xFF818CF8), // Indigo 400
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF4338CA), // Indigo 600
    onPrimaryContainer = Color(0xFFE0E7FF),
    
    secondary = Color(0xFF34D399), // Emerald 400
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF047857), // Emerald 700
    onSecondaryContainer = Color(0xFFD1FAE5),
    
    tertiary = Color(0xFFA78BFA), // Violet 400
    onTertiary = Color(0xFF4C1D95),
    tertiaryContainer = Color(0xFF7C3AED), // Violet 600
    onTertiaryContainer = Color(0xFFEDE9FE),
    
    error = Color(0xFFF87171), // Red 400
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFFDC2626), // Red 600
    onErrorContainer = Color(0xFFFEE2E2),
    
    background = Color(0xFF0F172A), // Slate 900
    onBackground = Color(0xFFF1F5F9), // Slate 100
    surface = Color(0xFF1E293B), // Slate 800
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155), // Slate 700
    onSurfaceVariant = Color(0xFF94A3B8), // Slate 400
    
    outline = Color(0xFF475569), // Slate 600
    outlineVariant = Color(0xFF334155), // Slate 700
    scrim = Color(0xFF000000)
)

@Composable
fun SpendCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SpendCraftTypography,
        content = content
    )
}




