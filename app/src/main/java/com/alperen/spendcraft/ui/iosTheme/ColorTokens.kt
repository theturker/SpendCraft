package com.alperen.spendcraft.ui.iosTheme

import androidx.compose.ui.graphics.Color

/**
 * iOS System Colors - Exact hex matches from iOS SwiftUI
 * Source of truth: iOS app colors
 */
object IOSColors {
    // Primary iOS System Colors
    val Blue = Color(0xFF007AFF)        // systemBlue
    val Green = Color(0xFF34C759)       // systemGreen
    val Red = Color(0xFFFF3B30)         // systemRed
    val Orange = Color(0xFFFF9500)      // systemOrange
    val Yellow = Color(0xFFFBBF24)      // systemYellow
    val Purple = Color(0xFFAF52DE)      // systemPurple
    val Pink = Color(0xFFFF2D55)        // systemPink
    val Cyan = Color(0xFF32ADE6)        // systemCyan
    val Mint = Color(0xFF00C7BE)        // systemMint
    
    // Semantic Colors
    val Income = Green
    val Expense = Red
    val Streak = Orange
    val Achievement = Yellow
    
    // Grayscale
    val Gray = Color(0xFF8E8E93)        // systemGray
    val Gray2 = Color(0xFFAEAEB2)       // systemGray2
    val Gray3 = Color(0xFFC7C7CC)       // systemGray3
    val Gray4 = Color(0xFFD1D1D6)       // systemGray4
    val Gray5 = Color(0xFFE5E5EA)       // systemGray5
    val Gray6 = Color(0xFFF2F2F7)       // systemGray6
}

/**
 * iOS Light Mode Color Scheme
 */
object IOSLightColors {
    val background = Color.White                    // systemBackground
    val surface = Color(0xFFF2F2F7)                // secondarySystemBackground
    val surfaceVariant = Color(0xFFFFFFFF)         // tertiarySystemBackground
    
    val onBackground = Color.Black
    val onSurface = Color(0xFF1C1B1F)
    val onSurfaceVariant = Color(0xFF8E8E93)       // secondaryLabel
    
    val primary = IOSColors.Blue
    val onPrimary = Color.White
    
    val outline = Color(0xFFC7C7CC)                // separator
    val outlineVariant = Color(0xFFE5E5EA)
    
    // Semantic
    val income = IOSColors.Green
    val expense = IOSColors.Red
    val streak = IOSColors.Orange
    val achievement = IOSColors.Yellow
    
    // Card backgrounds (with alpha)
    val incomeBackground = IOSColors.Green.copy(alpha = 0.1f)
    val expenseBackground = IOSColors.Red.copy(alpha = 0.1f)
    val streakBackground = IOSColors.Orange.copy(alpha = 0.1f)
    val achievementUnlockedBackground = IOSColors.Yellow.copy(alpha = 0.1f)
    val achievementLockedBackground = IOSColors.Gray.copy(alpha = 0.1f)
    
    // Balance card gradient
    val balanceGradientStart = IOSColors.Blue.copy(alpha = 0.1f)
    val balanceGradientEnd = IOSColors.Purple.copy(alpha = 0.1f)
}

/**
 * iOS Dark Mode Color Scheme
 */
object IOSDarkColors {
    val background = Color.Black                    // systemBackground (dark)
    val surface = Color(0xFF1C1C1E)                // secondarySystemBackground (dark)
    val surfaceVariant = Color(0xFF2C2C2E)         // tertiarySystemBackground (dark)
    
    val onBackground = Color.White
    val onSurface = Color(0xFFEBEBF5)
    val onSurfaceVariant = Color(0xFF8E8E93)       // secondaryLabel (dark)
    
    val primary = IOSColors.Blue
    val onPrimary = Color.White
    
    val outline = Color(0xFF38383A)                // separator (dark)
    val outlineVariant = Color(0xFF48484A)
    
    // Semantic (same as light)
    val income = IOSColors.Green
    val expense = IOSColors.Red
    val streak = IOSColors.Orange
    val achievement = IOSColors.Yellow
    
    // Card backgrounds (with alpha) - slightly more visible in dark mode
    val incomeBackground = IOSColors.Green.copy(alpha = 0.15f)
    val expenseBackground = IOSColors.Red.copy(alpha = 0.15f)
    val streakBackground = IOSColors.Orange.copy(alpha = 0.15f)
    val achievementUnlockedBackground = IOSColors.Yellow.copy(alpha = 0.15f)
    val achievementLockedBackground = IOSColors.Gray.copy(alpha = 0.15f)
    
    // Balance card gradient
    val balanceGradientStart = IOSColors.Blue.copy(alpha = 0.15f)
    val balanceGradientEnd = IOSColors.Purple.copy(alpha = 0.15f)
}

/**
 * Extended color scheme for semantic colors not in Material3 ColorScheme
 */
data class ExtendedColors(
    val income: Color,
    val expense: Color,
    val streak: Color,
    val achievement: Color,
    val incomeBackground: Color,
    val expenseBackground: Color,
    val streakBackground: Color,
    val achievementUnlockedBackground: Color,
    val achievementLockedBackground: Color,
    val balanceGradientStart: Color,
    val balanceGradientEnd: Color
)

val LightExtendedColors = ExtendedColors(
    income = IOSLightColors.income,
    expense = IOSLightColors.expense,
    streak = IOSLightColors.streak,
    achievement = IOSLightColors.achievement,
    incomeBackground = IOSLightColors.incomeBackground,
    expenseBackground = IOSLightColors.expenseBackground,
    streakBackground = IOSLightColors.streakBackground,
    achievementUnlockedBackground = IOSLightColors.achievementUnlockedBackground,
    achievementLockedBackground = IOSLightColors.achievementLockedBackground,
    balanceGradientStart = IOSLightColors.balanceGradientStart,
    balanceGradientEnd = IOSLightColors.balanceGradientEnd
)

val DarkExtendedColors = ExtendedColors(
    income = IOSDarkColors.income,
    expense = IOSDarkColors.expense,
    streak = IOSDarkColors.streak,
    achievement = IOSDarkColors.achievement,
    incomeBackground = IOSDarkColors.incomeBackground,
    expenseBackground = IOSDarkColors.expenseBackground,
    streakBackground = IOSDarkColors.streakBackground,
    achievementUnlockedBackground = IOSDarkColors.achievementUnlockedBackground,
    achievementLockedBackground = IOSDarkColors.achievementLockedBackground,
    balanceGradientStart = IOSDarkColors.balanceGradientStart,
    balanceGradientEnd = IOSDarkColors.balanceGradientEnd
)

