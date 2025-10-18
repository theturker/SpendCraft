package com.alperen.spendcraft.ui.components.liquidglass

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity

/**
 * Liquid Glass Scroll & IME Behavior
 * 
 * Utilities for handling:
 * - IME (keyboard) show/hide → auto-hide bar
 * - Scroll events → fade out on scroll down, fade in on scroll up
 */

/**
 * Remember whether bottom bar should be visible based on IME state
 * When keyboard is open, hide the bar to save screen space
 */
@Composable
fun rememberBottomBarVisibility(): State<Boolean> {
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    
    return remember {
        derivedStateOf {
            with(density) {
                // If IME height > 100dp, consider keyboard as open
                imeInsets.getBottom(density).toDp().value < 100f
            }
        }
    }
}

/**
 * Scroll behavior for hiding/showing bottom bar
 * Usage in LazyColumn/LazyVerticalGrid with scrollState
 */
@Stable
class LiquidGlassScrollBehavior {
    var isVisible by mutableStateOf(true)
        private set
    
    private var previousScrollOffset = 0
    
    fun updateScrollPosition(currentScrollOffset: Int) {
        val delta = currentScrollOffset - previousScrollOffset
        
        // Threshold to avoid jittery behavior
        if (kotlin.math.abs(delta) > 5) {
            isVisible = delta < 0 // Scrolling up shows bar, scrolling down hides it
        }
        
        previousScrollOffset = currentScrollOffset
    }
}

/**
 * Remember scroll behavior instance
 */
@Composable
fun rememberLiquidGlassScrollBehavior(): LiquidGlassScrollBehavior {
    return remember { LiquidGlassScrollBehavior() }
}

/**
 * Combined visibility state:
 * - Hidden if IME is open
 * - Hidden/shown based on scroll behavior
 */
@Composable
fun rememberCombinedBottomBarVisibility(
    scrollBehavior: LiquidGlassScrollBehavior? = null
): Boolean {
    val imeVisible = rememberBottomBarVisibility()
    
    return if (scrollBehavior != null) {
        imeVisible.value && scrollBehavior.isVisible
    } else {
        imeVisible.value
    }
}


