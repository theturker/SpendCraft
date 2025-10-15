package com.alperen.spendcraft.ui.iosTheme

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.composable

/**
 * iOS Navigation Transitions for Android Compose Navigation
 * 
 * iOS Source: MainAppView.swift transitions
 * iOS uses .asymmetric transitions with .move(edge:) and .easeInOut(duration: 0.5)
 * 
 * Usage in NavHost:
 * ```
 * composable(
 *     route = "screen",
 *     enterTransition = { IOSNavigationTransitions.pushEnter },
 *     exitTransition = { IOSNavigationTransitions.pushExit },
 *     popEnterTransition = { IOSNavigationTransitions.popEnter },
 *     popExitTransition = { IOSNavigationTransitions.popExit }
 * )
 * ```
 */
object IOSNavigationTransitions {
    private const val DURATION = 500  // iOS uses 0.5s
    private const val FADE_DURATION = 250
    
    /**
     * Push Enter - Slide in from right (trailing edge)
     * iOS: .transition(.asymmetric(insertion: .move(edge: .trailing)))
     */
    val pushEnter = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing  // Closest to iOS easeInOut
        )
    ) + fadeIn(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Push Exit - Slide out to left
     * iOS: .transition(.asymmetric(removal: .move(edge: .leading)))
     */
    val pushExit = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 3 },  // iOS keeps previous screen partially visible
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Pop Enter - Slide in from left (previous screen comes back)
     * iOS: Reverse of push exit
     */
    val popEnter = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Pop Exit - Slide out to right
     * iOS: .transition(.asymmetric(removal: .move(edge: .trailing)))
     */
    val popExit = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Modal Sheet Enter - Slide up from bottom
     * iOS: .sheet presentation
     */
    val sheetEnter = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Modal Sheet Exit - Slide down to bottom
     * iOS: Sheet dismissal
     */
    val sheetExit = slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(
            durationMillis = DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(durationMillis = FADE_DURATION)
    )
    
    /**
     * Fade Only - For tab switches
     * iOS: Tab bar switches use crossfade
     */
    val fadeIn = fadeIn(
        animationSpec = tween(
            durationMillis = IOSTransitions.FADE_DURATION,
            easing = FastOutSlowInEasing
        )
    )
    
    val fadeOut = fadeOut(
        animationSpec = tween(
            durationMillis = IOSTransitions.FADE_DURATION,
            easing = FastOutSlowInEasing
        )
    )
}

// Extension function removed - apply transitions directly in NavHost composable() calls
// Usage: See IOSNavigationTransitions object for enter/exit transition specs

