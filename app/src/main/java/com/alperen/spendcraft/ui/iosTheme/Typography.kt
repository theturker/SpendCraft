package com.alperen.spendcraft.ui.iosTheme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * iOS Typography System
 * Matches iOS San Francisco text styles exactly
 * 
 * iOS Text Style → Android Material3 mapping:
 * - largeTitle → displayLarge
 * - title → displayMedium
 * - title2 → displaySmall
 * - title3 → headlineLarge
 * - headline → headlineMedium
 * - body → bodyLarge
 * - callout → bodyMedium
 * - subheadline → bodySmall
 * - footnote → labelLarge
 * - caption → labelMedium
 * - caption2 → labelSmall
 */
val IOSTypography = Typography(
    // Display (Large titles)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,  // System font (Roboto on Android, San Francisco feel)
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,                 // Custom for balance display
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,                 // .title
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,                 // .title2
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,                 // .largeTitle (iOS uses this for nav titles)
        lineHeight = 41.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,                 // .title3
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,                 // .headline
        lineHeight = 22.sp,
        letterSpacing = (-0.4).sp
    ),
    
    // Title
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,                 // iOS headline style
        lineHeight = 22.sp,
        letterSpacing = (-0.4).sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,                 // .subheadline with medium weight
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp
    ),
    
    // Body
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,                 // .body
        lineHeight = 22.sp,
        letterSpacing = (-0.4).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,                 // .callout
        lineHeight = 21.sp,
        letterSpacing = (-0.3).sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,                 // .subheadline
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp
    ),
    
    // Label
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,                 // .footnote
        lineHeight = 18.sp,
        letterSpacing = (-0.1).sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,                 // .caption
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,                 // .caption2
        lineHeight = 13.sp,
        letterSpacing = 0.sp
    )
)

/**
 * iOS Font Weights (for manual overrides)
 */
object IOSFontWeights {
    val Bold = FontWeight.Bold              // .bold
    val SemiBold = FontWeight.SemiBold      // .semibold
    val Medium = FontWeight.Medium          // .medium
    val Regular = FontWeight.Normal         // .regular
    val Light = FontWeight.Light            // .light
}

