package com.alperen.spendcraft.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
data class SpendCraftBrandColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val neutral: Color,
    val accent: Color
)

@Immutable
data class SpendCraftStatusColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
    val danger: Color,
    val onDanger: Color
)

@Immutable
data class SpendCraftSurfaceColors(
    val surface1: Color,
    val surface2: Color,
    val surface3: Color,
    val surface4: Color,
    val surface5: Color,
    val overlay: Color,
    val outlineSoft: Color,
    val outlineStrong: Color,
    val shadowAmbient: Color,
    val shadowSpot: Color
)

@Immutable
data class SpendCraftGradientPalette(
    val primary: List<Color>,
    val success: List<Color>,
    val warning: List<Color>,
    val info: List<Color>
)

@Stable
class SpendCraftColorScheme internal constructor(
    internal val material: ColorScheme,
    val brand: SpendCraftBrandColors,
    val status: SpendCraftStatusColors,
    val surfaces: SpendCraftSurfaceColors,
    val gradients: SpendCraftGradientPalette,
    val focus: Color
) {
    val primary: Color get() = material.primary
    val onPrimary: Color get() = material.onPrimary
    val primaryContainer: Color get() = material.primaryContainer
    val onPrimaryContainer: Color get() = material.onPrimaryContainer
    val inversePrimary: Color get() = material.inversePrimary

    val secondary: Color get() = material.secondary
    val onSecondary: Color get() = material.onSecondary
    val secondaryContainer: Color get() = material.secondaryContainer
    val onSecondaryContainer: Color get() = material.onSecondaryContainer

    val tertiary: Color get() = material.tertiary
    val onTertiary: Color get() = material.onTertiary
    val tertiaryContainer: Color get() = material.tertiaryContainer
    val onTertiaryContainer: Color get() = material.onTertiaryContainer

    val background: Color get() = material.background
    val onBackground: Color get() = material.onBackground

    val surface: Color get() = material.surface
    val surfaceDim: Color get() = material.surfaceDim
    val surfaceBright: Color get() = material.surfaceBright
    val surfaceContainerLowest: Color get() = material.surfaceContainerLowest
    val surfaceContainerLow: Color get() = material.surfaceContainerLow
    val surfaceContainer: Color get() = material.surfaceContainer
    val surfaceContainerHigh: Color get() = material.surfaceContainerHigh
    val surfaceContainerHighest: Color get() = material.surfaceContainerHighest
    val onSurface: Color get() = material.onSurface
    val onSurfaceVariant: Color get() = material.onSurfaceVariant
    val surfaceVariant: Color get() = material.surfaceVariant
    val surfaceTint: Color get() = material.surfaceTint
    val inverseSurface: Color get() = material.inverseSurface
    val inverseOnSurface: Color get() = material.inverseOnSurface

    val error: Color get() = material.error
    val onError: Color get() = material.onError
    val errorContainer: Color get() = material.errorContainer
    val onErrorContainer: Color get() = material.onErrorContainer

    val outline: Color get() = material.outline
    val outlineVariant: Color get() = material.outlineVariant
    val scrim: Color get() = material.scrim
}

private val LightMaterialColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    inversePrimary = Color(0xFFBEC5FF),

    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),

    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF4C1D95),

    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF1E293B),

    surface = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFE2E8F0),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F5F9),
    surfaceContainer = Color(0xFFE2E8F0),
    surfaceContainerHigh = Color(0xFFD0D8E6),
    surfaceContainerHighest = Color(0xFFB8C2D8),
    onSurface = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF475569),
    surfaceVariant = Color(0xFFE2E8F0),
    surfaceTint = Color(0xFF6366F1),
    inverseSurface = Color(0xFF111827),
    inverseOnSurface = Color(0xFFE2E8F0),

    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),

    outline = Color(0xFFD0D8E6),
    outlineVariant = Color(0xFFCBD5F5),
    scrim = Color(0xFF020617)
)

private val DarkMaterialColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    inversePrimary = Color(0xFF4C51BF),

    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),

    tertiary = Color(0xFFA78BFA),
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF5B21B6),
    onTertiaryContainer = Color(0xFFEDE9FE),

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE2E8F0),

    surface = Color(0xFF111827),
    surfaceDim = Color(0xFF0B1120),
    surfaceBright = Color(0xFF1F2937),
    surfaceContainerLowest = Color(0xFF0B1120),
    surfaceContainerLow = Color(0xFF1E293B),
    surfaceContainer = Color(0xFF253046),
    surfaceContainerHigh = Color(0xFF303B53),
    surfaceContainerHighest = Color(0xFF3B4661),
    onSurface = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceVariant = Color(0xFF334155),
    surfaceTint = Color(0xFF818CF8),
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0B1120),

    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),

    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF1E293B),
    scrim = Color(0xFF000000)
)

private val LightBrandColors = SpendCraftBrandColors(
    primary = LightMaterialColorScheme.primary,
    onPrimary = LightMaterialColorScheme.onPrimary,
    secondary = LightMaterialColorScheme.secondary,
    onSecondary = LightMaterialColorScheme.onSecondary,
    tertiary = LightMaterialColorScheme.tertiary,
    neutral = Color(0xFFCBD5F5),
    accent = Color(0xFF38BDF8)
)

private val DarkBrandColors = SpendCraftBrandColors(
    primary = DarkMaterialColorScheme.primary,
    onPrimary = DarkMaterialColorScheme.onPrimary,
    secondary = DarkMaterialColorScheme.secondary,
    onSecondary = DarkMaterialColorScheme.onSecondary,
    tertiary = DarkMaterialColorScheme.tertiary,
    neutral = Color(0xFF1E293B),
    accent = Color(0xFF0EA5E9)
)

private val LightStatusColors = SpendCraftStatusColors(
    success = Color(0xFF16A34A),
    onSuccess = Color(0xFFFFFFFF),
    warning = Color(0xFFF59E0B),
    onWarning = Color(0xFF43290B),
    info = Color(0xFF0EA5E9),
    onInfo = Color(0xFF012638),
    danger = Color(0xFFDC2626),
    onDanger = Color(0xFFFFFFFF)
)

private val DarkStatusColors = SpendCraftStatusColors(
    success = Color(0xFF22C55E),
    onSuccess = Color(0xFF022C22),
    warning = Color(0xFFFBBF24),
    onWarning = Color(0xFF1F1302),
    info = Color(0xFF38BDF8),
    onInfo = Color(0xFF0F172A),
    danger = Color(0xFFF87171),
    onDanger = Color(0xFF450A0A)
)

private val LightSurfaces = SpendCraftSurfaceColors(
    surface1 = LightMaterialColorScheme.surface,
    surface2 = Color(0xFFF8FAFC),
    surface3 = Color(0xFFF1F5F9),
    surface4 = Color(0xFFE2E8F0),
    surface5 = Color(0xFFCBD5F5),
    overlay = Color(0x140F172A),
    outlineSoft = Color(0xFFE2E8F0),
    outlineStrong = Color(0xFFCBD5F5),
    shadowAmbient = Color(0x26000000),
    shadowSpot = Color(0x1A000000)
)

private val DarkSurfaces = SpendCraftSurfaceColors(
    surface1 = DarkMaterialColorScheme.surface,
    surface2 = Color(0xFF16213A),
    surface3 = Color(0xFF1F2A44),
    surface4 = Color(0xFF26324D),
    surface5 = Color(0xFF303B53),
    overlay = Color(0x66111827),
    outlineSoft = Color(0xFF1F2937),
    outlineStrong = Color(0xFF374151),
    shadowAmbient = Color(0x33000000),
    shadowSpot = Color(0x26000000)
)

private val LightGradients = SpendCraftGradientPalette(
    primary = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
    success = listOf(Color(0xFF10B981), Color(0xFF22C55E)),
    warning = listOf(Color(0xFFF59E0B), Color(0xFFF97316)),
    info = listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
)

private val DarkGradients = SpendCraftGradientPalette(
    primary = listOf(Color(0xFF818CF8), Color(0xFFA78BFA)),
    success = listOf(Color(0xFF34D399), Color(0xFF22C55E)),
    warning = listOf(Color(0xFFFACC15), Color(0xFFF97316)),
    info = listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
)

internal fun spendCraftLightColorScheme(): SpendCraftColorScheme =
    SpendCraftColorScheme(
        material = LightMaterialColorScheme,
        brand = LightBrandColors,
        status = LightStatusColors,
        surfaces = LightSurfaces,
        gradients = LightGradients,
        focus = Color(0xFF4C51BF)
    )

internal fun spendCraftDarkColorScheme(): SpendCraftColorScheme =
    SpendCraftColorScheme(
        material = DarkMaterialColorScheme,
        brand = DarkBrandColors,
        status = DarkStatusColors,
        surfaces = DarkSurfaces,
        gradients = DarkGradients,
        focus = Color(0xFF3730A3)
    )
