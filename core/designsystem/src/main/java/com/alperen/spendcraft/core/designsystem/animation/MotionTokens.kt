package com.alperen.spendcraft.core.designsystem.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SpendCraftCardPressSpec(
    val restScale: Float,
    val pressedScale: Float,
    val restElevation: Dp,
    val pressedElevation: Dp,
    val ambientAlpha: Float,
    val spotAlpha: Float
)

@Immutable
data class SpendCraftMotionSpec(
    val quickSpring: SpringSpec<Float>,
    val expressiveSpring: SpringSpec<Float>,
    val fadeIn: FiniteAnimationSpec<Float>,
    val fadeOut: FiniteAnimationSpec<Float>,
    val shimmerDurationMillis: Int,
    val cardPress: SpendCraftCardPressSpec
)

@Stable
data class CardPressAnimationState(
    val scale: Float,
    val elevation: Dp,
    val ambientAlpha: Float,
    val spotAlpha: Float
)

internal fun spendCraftMotionSpec(reduceMotion: Boolean): SpendCraftMotionSpec {
    val quickSpring = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val expressiveSpring = spring<Float>(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
    val fadeIn = if (reduceMotion) tween(durationMillis = 120) else tween(durationMillis = 220)
    val fadeOut = if (reduceMotion) tween(durationMillis = 100) else tween(durationMillis = 180)

    val cardSpec = if (reduceMotion) {
        SpendCraftCardPressSpec(
            restScale = 1f,
            pressedScale = 0.99f,
            restElevation = 6.dp,
            pressedElevation = 10.dp,
            ambientAlpha = 0.08f,
            spotAlpha = 0.12f
        )
    } else {
        SpendCraftCardPressSpec(
            restScale = 1f,
            pressedScale = 0.96f,
            restElevation = 4.dp,
            pressedElevation = 18.dp,
            ambientAlpha = 0.12f,
            spotAlpha = 0.18f
        )
    }

    return SpendCraftMotionSpec(
        quickSpring = quickSpring,
        expressiveSpring = expressiveSpring,
        fadeIn = fadeIn,
        fadeOut = fadeOut,
        shimmerDurationMillis = if (reduceMotion) 900 else 1400,
        cardPress = cardSpec
    )
}

@Composable
fun rememberCardPressAnimationState(
    pressed: Boolean,
    spec: SpendCraftCardPressSpec,
    spring: SpringSpec<Float>
): CardPressAnimationState {
    val scale by animateFloatAsState(
        targetValue = if (pressed) spec.pressedScale else spec.restScale,
        animationSpec = spring,
        label = "cardPressScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (pressed) spec.pressedElevation else spec.restElevation,
        animationSpec = spring,
        label = "cardPressElevation"
    )
    return CardPressAnimationState(
        scale = scale,
        elevation = elevation,
        ambientAlpha = spec.ambientAlpha,
        spotAlpha = spec.spotAlpha
    )
}

@Composable
fun rememberPulseAnimation(
    initialValue: Float = 1f,
    targetValue: Float = 1.05f,
    durationMillis: Int = 1400
): State<Float> {
    val transition = rememberInfiniteTransition(label = "pulseTransition")
    return transition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseValue"
    )
}
