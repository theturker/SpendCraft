package com.alperen.spendcraft.ui.components.liquidglass

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Liquid Glass Bottom Navigation Bar
 * 
 * Premium bottom bar with backdrop blur, floating capsule design,
 * smooth animations, and haptic feedback. Pixel-perfect iOS parity.
 * 
 * @param items List of navigation items
 * @param selectedIndex Currently selected tab index
 * @param onItemSelected Callback when tab is selected
 * @param modifier Modifier for customization
 * @param floating Whether to use floating offset from bottom
 * @param handleSafeInsets Whether to add safe area insets
 * @param visible Whether the bar is visible (for scroll/IME handling)
 */
@Composable
fun LiquidGlassBottomBar(
    items: List<LiquidGlassNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    floating: Boolean = true,
    handleSafeInsets: Boolean = true,
    visible: Boolean = true
) {
    val isDarkMode = isSystemInDarkTheme()
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = androidx.compose.animation.core.tween(
                LiquidGlassThemeTokens.barShowHideDuration
            )
        ) + slideInVertically(
            animationSpec = androidx.compose.animation.core.tween(
                LiquidGlassThemeTokens.barShowHideDuration
            )
        ) { it },
        exit = fadeOut(
            animationSpec = androidx.compose.animation.core.tween(
                LiquidGlassThemeTokens.barShowHideDuration
            )
        ) + slideOutVertically(
            animationSpec = androidx.compose.animation.core.tween(
                LiquidGlassThemeTokens.barShowHideDuration
            )
        ) { it }
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (handleSafeInsets) {
                        Modifier.navigationBarsPadding()
                    } else {
                        Modifier
                    }
                )
                .padding(
                    horizontal = LiquidGlassThemeTokens.barHorizontalPadding,
                    vertical = if (floating) LiquidGlassThemeTokens.barBottomOffset else 0.dp
                )
        ) {
            // Floating capsule with blur effect
            LiquidGlassBlurSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LiquidGlassThemeTokens.barHeight)
                    .shadow(
                        elevation = LiquidGlassThemeTokens.shadowElevation,
                        shape = RoundedCornerShape(LiquidGlassThemeTokens.barCornerRadius),
                        clip = false
                    )
                    .clip(RoundedCornerShape(LiquidGlassThemeTokens.barCornerRadius))
                    .drawBehind {
                        // Border rim
                        val borderColor = if (isDarkMode) {
                            LiquidGlassThemeTokens.Dark.glassBorder
                        } else {
                            LiquidGlassThemeTokens.Light.glassBorder
                        }
                        drawRect(
                            color = borderColor,
                            alpha = 0.5f
                        )
                    },
                isDarkMode = isDarkMode
            ) {
                // Navigation items
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        LiquidGlassTabItem(
                            item = item,
                            isSelected = selectedIndex == index,
                            onClick = { onItemSelected(index) },
                            isDarkMode = isDarkMode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Tab Item with selection animation
 */
@Composable
private fun LiquidGlassTabItem(
    item: LiquidGlassNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    
    // Colors
    val iconColor = if (isSelected) {
        if (isDarkMode) LiquidGlassThemeTokens.Dark.iconSelected
        else LiquidGlassThemeTokens.Light.iconSelected
    } else {
        if (isDarkMode) LiquidGlassThemeTokens.Dark.iconDefault
        else LiquidGlassThemeTokens.Light.iconDefault
    }
    
    val labelColor = if (isSelected) {
        if (isDarkMode) LiquidGlassThemeTokens.Dark.labelSelected
        else LiquidGlassThemeTokens.Light.labelSelected
    } else {
        if (isDarkMode) LiquidGlassThemeTokens.Dark.labelDefault
        else LiquidGlassThemeTokens.Light.labelDefault
    }
    
    val pillBackground = if (isDarkMode) {
        LiquidGlassThemeTokens.Dark.pillBackground
    } else {
        LiquidGlassThemeTokens.Light.pillBackground
    }
    
    // Animations - Sadece opacity değişiyor (icon ve text oynamıyor)
    val labelAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.75f,
        animationSpec = androidx.compose.animation.core.tween(
            LiquidGlassThemeTokens.selectionAnimationDuration
        ),
        label = "labelAlpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(LiquidGlassThemeTokens.pillCornerRadius))
            .then(
                if (isSelected) {
                    Modifier
                        .background(pillBackground)
                        .drawBehind {
                            // Pill glow effect
                            val glowColor = if (isDarkMode) {
                                LiquidGlassThemeTokens.Dark.pillGlow
                            } else {
                                LiquidGlassThemeTokens.Light.pillGlow
                            }
                            drawRect(color = glowColor, alpha = 0.3f)
                        }
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    color = iconColor.copy(alpha = 0.2f)
                ),
                onClick = {
                    // Haptic feedback
                    view.performHapticFeedback(
                        HapticFeedbackConstants.CONTEXT_CLICK,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                    )
                    onClick()
                }
            )
            .padding(
                horizontal = LiquidGlassThemeTokens.pillPaddingHorizontal,
                vertical = LiquidGlassThemeTokens.pillPaddingVertical
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon - HER ZAMAN AYNI (oynamıyor)
            Icon(
                imageVector = item.icon, // Seçilide de aynı icon
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(LiquidGlassThemeTokens.iconSize)
            )
            
            Spacer(modifier = Modifier.height(LiquidGlassThemeTokens.iconLabelSpacing))
            
            // Label - Sadece opacity değişiyor (oynamıyor)
            Text(
                text = item.title,
                fontSize = LiquidGlassThemeTokens.labelFontSize.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor.copy(alpha = labelAlpha),
                maxLines = 1 // Tek satırda sığsın
            )
        }
    }
}

/**
 * Navigation Item Data Class
 */
data class LiquidGlassNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

/**
 * Standard 5-tab setup for Paratik
 */
val paratikBottomNavItems = listOf(
    LiquidGlassNavItem(
        route = "home",
        title = "Ana Sayfa",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.HouseOutline,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.HouseFill
    ),
    LiquidGlassNavItem(
        route = "transactions",
        title = "İşlemler",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ListBullet,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ListBullet
    ),
    LiquidGlassNavItem(
        route = "reports",
        title = "Raporlar",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ChartBarOutline,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ChartBarFill
    ),
    LiquidGlassNavItem(
        route = "categories",
        title = "Kategoriler",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.FolderFill,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.FolderFill
    ),
    LiquidGlassNavItem(
        route = "settings",
        title = "Ayarlar",
        icon = androidx.compose.material.icons.Icons.Outlined.Settings,
        selectedIcon = androidx.compose.material.icons.Icons.Filled.Settings
    )
)

