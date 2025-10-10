package com.alperen.spendcraft.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// iOS-inspired Color Palette
object IOSColors {
    // System Colors
    val Blue = Color(0xFF007AFF)
    val Green = Color(0xFF34C759)
    val Red = Color(0xFFFF3B30)
    val Orange = Color(0xFFFF9500)
    val Yellow = Color(0xFFFFCC00)
    val Purple = Color(0xFFAF52DE)
    val Pink = Color(0xFFFF2D55)
    val Teal = Color(0xFF5AC8FA)
    val Indigo = Color(0xFF5856D6)
    val Cyan = Color(0xFF32ADE6)
    val Mint = Color(0xFF00C7BE)
    
    // Income/Expense
    val IncomeGreen = Color(0xFF10B981)
    val ExpenseRed = Color(0xFFEF4444)
    
    // Achievement Colors
    val AchievementYellow = Color(0xFFFBBF24)
    val StreakOrange = Color(0xFFF59E0B)
    
    // Light Theme
    val LightBackground = Color(0xFFF8FAFC)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSecondaryBackground = Color(0xFFF1F5F9)
    
    // Dark Theme
    val DarkBackground = Color(0xFF0F172A)
    val DarkSurface = Color(0xFF1E293B)
    val DarkSecondaryBackground = Color(0xFF334155)
}

// iOS-style Corner Radius
object IOSShapes {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(20.dp)
    val Card = RoundedCornerShape(15.dp)
    val Button = RoundedCornerShape(12.dp)
}

// Custom theme extensions
data class ExtendedColors(
    val income: Color,
    val expense: Color,
    val achievement: Color,
    val streak: Color,
    val cardBackground: Color,
    val secondaryBackground: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        income = IOSColors.IncomeGreen,
        expense = IOSColors.ExpenseRed,
        achievement = IOSColors.AchievementYellow,
        streak = IOSColors.StreakOrange,
        cardBackground = IOSColors.LightSurface,
        secondaryBackground = IOSColors.LightSecondaryBackground
    )
}

// Modern Light Theme Colors (iOS-inspired)
private val LightColors = lightColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    
    secondary = IOSColors.Green,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    
    tertiary = IOSColors.Purple,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF4C1D95),
    
    error = IOSColors.Red,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    
    background = IOSColors.LightBackground,
    onBackground = Color(0xFF1E293B),
    surface = IOSColors.LightSurface,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = IOSColors.LightSecondaryBackground,
    onSurfaceVariant = Color(0xFF64748B),
    
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),
    scrim = Color(0xFF000000)
)

// Modern Dark Theme Colors (iOS-inspired)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF0A84FF), // iOS Blue Dark Mode
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF4338CA),
    onPrimaryContainer = Color(0xFFE0E7FF),
    
    secondary = Color(0xFF32D74B), // iOS Green Dark Mode
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF047857),
    onSecondaryContainer = Color(0xFFD1FAE5),
    
    tertiary = Color(0xFFBF5AF2), // iOS Purple Dark Mode
    onTertiary = Color(0xFF4C1D95),
    tertiaryContainer = Color(0xFF7C3AED),
    onTertiaryContainer = Color(0xFFEDE9FE),
    
    error = Color(0xFFFF453A), // iOS Red Dark Mode
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFFDC2626),
    onErrorContainer = Color(0xFFFEE2E2),
    
    background = IOSColors.DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = IOSColors.DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = IOSColors.DarkSecondaryBackground,
    onSurfaceVariant = Color(0xFF94A3B8),
    
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    scrim = Color(0xFF000000)
)

// Material 3 Shapes (iOS-inspired)
private val IOSAppShapes = Shapes(
    extraSmall = IOSShapes.Small,
    small = IOSShapes.Medium,
    medium = IOSShapes.Large,
    large = IOSShapes.ExtraLarge,
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SpendCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    val extendedColors = ExtendedColors(
        income = if (darkTheme) Color(0xFF32D74B) else IOSColors.IncomeGreen,
        expense = if (darkTheme) Color(0xFFFF453A) else IOSColors.ExpenseRed,
        achievement = if (darkTheme) Color(0xFFFFD60A) else IOSColors.AchievementYellow,
        streak = if (darkTheme) Color(0xFFFF9F0A) else IOSColors.StreakOrange,
        cardBackground = if (darkTheme) IOSColors.DarkSurface else IOSColors.LightSurface,
        secondaryBackground = if (darkTheme) IOSColors.DarkSecondaryBackground else IOSColors.LightSecondaryBackground
    )
    
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SpendCraftTypography,
            shapes = IOSAppShapes,
            content = content
        )
    }
}

// Extension property for easy access to extended colors
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current




