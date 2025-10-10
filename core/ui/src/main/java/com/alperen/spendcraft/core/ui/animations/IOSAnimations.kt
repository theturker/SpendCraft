package com.alperen.spendcraft.core.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * iOS-style animations ve transitions
 */

/**
 * iOS-style scale animation on press (button bounce)
 */
fun Modifier.iosPressAnimation(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * iOS-style fade in animation
 */
fun fadeInAnimation(
    duration: Int = 300
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    )
}

/**
 * iOS-style fade out animation
 */
fun fadeOutAnimation(
    duration: Int = 300
): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    )
}

/**
 * iOS-style slide from right animation
 */
fun slideFromRightAnimation(
    duration: Int = 350
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration / 2,
            easing = LinearEasing
        )
    )
}

/**
 * iOS-style slide to left animation (exit)
 */
fun slideToLeftAnimation(
    duration: Int = 350
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration / 2,
            easing = LinearEasing
        )
    )
}

/**
 * iOS-style slide from bottom animation (modal)
 */
fun slideFromBottomAnimation(
    duration: Int = 400
): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    )
}

/**
 * iOS-style slide to bottom animation (modal dismiss)
 */
fun slideToBottomAnimation(
    duration: Int = 400
): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    )
}

/**
 * iOS-style scale animation (for dialogs)
 */
fun scaleInAnimation(
    duration: Int = 300
): EnterTransition {
    return scaleIn(
        initialScale = 0.9f,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = LinearEasing
        )
    )
}

/**
 * iOS-style scale out animation
 */
fun scaleOutAnimation(
    duration: Int = 300
): ExitTransition {
    return scaleOut(
        targetScale = 0.9f,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = LinearEasing
        )
    )
}

/**
 * iOS-style list item appear animation
 */
@Composable
fun IOSListItemAnimation(
    index: Int,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }
    
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "list_item_offset"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "list_item_alpha"
    )
    
    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY.toPx()
                this.alpha = alpha
            }
    ) {
        content()
    }
}

/**
 * Bounce animation (iOS spring effect)
 */
fun Modifier.iosBounceAnimation(enabled: Boolean): Modifier = composed {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

