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
    
    // Light Theme - iOS systemBackground
    val LightBackground = Color.White
    val LightSurface = Color(0xFFF2F2F7)          // iOS secondarySystemBackground
    val LightSecondaryBackground = Color.White    // iOS tertiarySystemBackground
    
    // Dark Theme - iOS systemBackground (dark mode)
    val DarkBackground = Color.Black              // iOS systemBackground (dark)
    val DarkSurface = Color(0xFF1C1C1E)          // iOS secondarySystemBackground (dark)
    val DarkSecondaryBackground = Color(0xFF2C2C2E) // iOS tertiarySystemBackground (dark)
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

// iOS Light Mode Colors - Exact match to iOS systemColors
private val LightColors = lightColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.2f),
    onPrimaryContainer = IOSColors.Blue,
    
    secondary = IOSColors.Green,
    onSecondary = Color.White,
    secondaryContainer = IOSColors.Green.copy(alpha = 0.2f),
    onSecondaryContainer = IOSColors.Green,
    
    tertiary = IOSColors.Purple,
    onTertiary = Color.White,
    tertiaryContainer = IOSColors.Purple.copy(alpha = 0.2f),
    onTertiaryContainer = IOSColors.Purple,
    
    error = IOSColors.Red,
    onError = Color.White,
    errorContainer = IOSColors.Red.copy(alpha = 0.2f),
    onErrorContainer = IOSColors.Red,
    
    background = IOSColors.LightBackground,       // White
    onBackground = Color.Black,
    surface = IOSColors.LightSurface,            // #F2F2F7
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = IOSColors.LightSecondaryBackground, // White
    onSurfaceVariant = Color(0xFF8E8E93),        // iOS secondaryLabel
    
    outline = Color(0xFFC7C7CC),                 // iOS separator
    outlineVariant = Color(0xFFE5E5EA),
    scrim = Color(0xFF000000)
)

// iOS Dark Mode Colors - Exact match to iOS systemColors (dark)
private val DarkColors = darkColorScheme(
    primary = IOSColors.Blue,                     // systemBlue
    onPrimary = Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.3f),
    onPrimaryContainer = IOSColors.Blue,
    
    secondary = IOSColors.Green,                  // systemGreen
    onSecondary = Color.White,
    secondaryContainer = IOSColors.Green.copy(alpha = 0.3f),
    onSecondaryContainer = IOSColors.Green,
    
    tertiary = IOSColors.Purple,                  // systemPurple
    onTertiary = Color.White,
    tertiaryContainer = IOSColors.Purple.copy(alpha = 0.3f),
    onTertiaryContainer = IOSColors.Purple,
    
    error = IOSColors.Red,                        // systemRed
    onError = Color.White,
    errorContainer = IOSColors.Red.copy(alpha = 0.3f),
    onErrorContainer = IOSColors.Red,
    
    background = IOSColors.DarkBackground,        // Black
    onBackground = Color.White,
    surface = IOSColors.DarkSurface,             // #1C1C1E
    onSurface = Color(0xFFEBEBF5),
    surfaceVariant = IOSColors.DarkSecondaryBackground, // #2C2C2E
    onSurfaceVariant = Color(0xFF8E8E93),        // iOS secondaryLabel
    
    outline = Color(0xFF38383A),                 // iOS separator (dark)
    outlineVariant = Color(0xFF48484A),
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
    
    // iOS system colors - same in both light and dark mode
    val extendedColors = ExtendedColors(
        income = IOSColors.Green,                   // #34C759
        expense = IOSColors.Red,                    // #FF3B30
        achievement = IOSColors.AchievementYellow,  // #FBBF24
        streak = IOSColors.StreakOrange,           // #F59E0B
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




