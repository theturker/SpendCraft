package com.alperen.spendcraft.core.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun SlideInFromRightTransition(
    targetState: Any,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300)) togetherWith
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(300))
        },
        label = "slide_transition"
    ) {
        content()
    }
}

@Composable
fun SlideInFromBottomTransition(
    targetState: Any,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(300)) togetherWith
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        label = "slide_bottom_transition"
    ) {
        content()
    }
}

@Composable
fun ScaleInTransition(
    targetState: Any,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(300)) togetherWith
            scaleOut(
                targetScale = 1.1f,
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        },
        label = "scale_transition"
    ) {
        content()
    }
}

@Composable
fun FadeInTransition(
    targetState: Any,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith
            fadeOut(
                animationSpec = tween(200)
            )
        },
        label = "fade_transition"
    ) {
        content()
    }
}

@Composable
fun StaggeredListTransition(
    targetState: List<Any>,
    content: @Composable AnimatedVisibilityScope.(item: Any) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(300, delayMillis = 100)
            ) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith
            fadeOut(
                animationSpec = tween(200)
            ) + slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(300)
            )
        },
        label = "staggered_transition"
    ) { items ->
        items.forEachIndexed { index, item ->
            content(item)
        }
    }
}
