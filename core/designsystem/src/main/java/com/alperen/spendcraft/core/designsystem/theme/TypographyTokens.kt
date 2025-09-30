package com.alperen.spendcraft.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SpendCraftSans = FontFamily.SansSerif

@Immutable
data class SpendCraftSupportingTypography(
    val capsuleLabel: TextStyle,
    val buttonCapsule: TextStyle,
    val tab: TextStyle,
    val numericEmphasis: TextStyle
)

@Stable
class SpendCraftTypography internal constructor(
    internal val material: Typography,
    val supporting: SpendCraftSupportingTypography
) {
    val displayLarge: TextStyle get() = material.displayLarge
    val displayMedium: TextStyle get() = material.displayMedium
    val displaySmall: TextStyle get() = material.displaySmall
    val headlineLarge: TextStyle get() = material.headlineLarge
    val headlineMedium: TextStyle get() = material.headlineMedium
    val headlineSmall: TextStyle get() = material.headlineSmall
    val titleLarge: TextStyle get() = material.titleLarge
    val titleMedium: TextStyle get() = material.titleMedium
    val titleSmall: TextStyle get() = material.titleSmall
    val bodyLarge: TextStyle get() = material.bodyLarge
    val bodyMedium: TextStyle get() = material.bodyMedium
    val bodySmall: TextStyle get() = material.bodySmall
    val labelLarge: TextStyle get() = material.labelLarge
    val labelMedium: TextStyle get() = material.labelMedium
    val labelSmall: TextStyle get() = material.labelSmall
}

private val BaseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

private val SupportingTypography = SpendCraftSupportingTypography(
    capsuleLabel = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    buttonCapsule = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    tab = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    numericEmphasis = TextStyle(
        fontFamily = SpendCraftSans,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    )
)

internal fun spendCraftTypography(): SpendCraftTypography =
    SpendCraftTypography(
        material = BaseTypography,
        supporting = SupportingTypography
    )
