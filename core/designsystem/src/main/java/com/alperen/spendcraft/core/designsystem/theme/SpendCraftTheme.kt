package com.alperen.spendcraft.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import com.alperen.spendcraft.core.designsystem.animation.SpendCraftMotionSpec
import com.alperen.spendcraft.core.designsystem.animation.rememberCardPressAnimationState
import com.alperen.spendcraft.core.designsystem.animation.rememberPulseAnimation
import com.alperen.spendcraft.core.designsystem.animation.spendCraftMotionSpec
import com.alperen.spendcraft.core.designsystem.icon.SpendCraftIcons

private val LocalColorScheme = staticCompositionLocalOf { spendCraftLightColorScheme() }
private val LocalTypography = staticCompositionLocalOf { spendCraftTypography() }
private val LocalShapes = staticCompositionLocalOf { spendCraftShapes() }
private val LocalMotion = staticCompositionLocalOf { spendCraftMotionSpec(reduceMotion = false) }

object SpendCraftTheme {
    val icons: SpendCraftIcons = SpendCraftIcons

    val colors: SpendCraftColorScheme
        @Composable get() = LocalColorScheme.current

    val typography: SpendCraftTypography
        @Composable get() = LocalTypography.current

    val shapes: SpendCraftShapes
        @Composable get() = LocalShapes.current

    val motion: SpendCraftMotionSpec
        @Composable get() = LocalMotion.current

    @Composable
    operator fun invoke(
        darkTheme: Boolean = isSystemInDarkTheme(),
        reduceMotion: Boolean = false,
        content: @Composable () -> Unit
    ) {
        val colors = remember(darkTheme) { if (darkTheme) spendCraftDarkColorScheme() else spendCraftLightColorScheme() }
        val typography = remember { spendCraftTypography() }
        val shapes = remember { spendCraftShapes() }
        val motion = remember(darkTheme, reduceMotion) { spendCraftMotionSpec(reduceMotion) }

        CompositionLocalProvider(
            LocalColorScheme provides colors,
            LocalTypography provides typography,
            LocalShapes provides shapes,
            LocalMotion provides motion
        ) {
            MaterialTheme(
                colorScheme = colors.material,
                typography = typography.material,
                shapes = shapes.material,
                content = content
            )
        }
    }
}

@Composable
fun SpendCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    SpendCraftTheme.invoke(
        darkTheme = darkTheme,
        reduceMotion = reduceMotion,
        content = content
    )
}

@Immutable
object SpendCraftMotionHelpers {
    @Composable
    fun cardPress(pressed: Boolean) = rememberCardPressAnimationState(
        pressed = pressed,
        spec = SpendCraftTheme.motion.cardPress,
        spring = SpendCraftTheme.motion.quickSpring
    )

    @Composable
    fun pulse(
        initialValue: Float = 1f,
        targetValue: Float = 1.05f,
        durationMillis: Int = SpendCraftTheme.motion.shimmerDurationMillis
    ) = rememberPulseAnimation(initialValue, targetValue, durationMillis)
}
