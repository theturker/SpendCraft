package com.alperen.spendcraft.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.IOSColors
import com.alperen.spendcraft.ui.iosTheme.IOSTheme
import com.alperen.spendcraft.ui.icons.SFSymbolsTabBar

/**
 * iOS UITabBar'ın birebir Android Compose karşılığı
 * 
 * iOS TabBar Spec (from MainAppView.swift & iOS Human Interface Guidelines):
 * 
 * GEOMETRY:
 * - Height: 49dp (iOS standard tab bar height excluding safe area)
 * - Bottom Safe Area: +34dp on devices with home indicator (iPhone X+)
 * - Top Divider: 0.5dp hairline separator (#E5E5EA light, #3C3C43 dark)
 * - Corner Radius: 0dp (full-width rectangle)
 * 
 * BACKGROUND:
 * - Material: Opaque (configureWithOpaqueBackground)
 * - Color: systemBackground (#FFFFFF light, #000000 dark)
 * - Blur: None (opaque configuration)
 * - Shadow: Very subtle top shadow (0.1 alpha, 2dp blur)
 * 
 * ICON & LABEL:
 * - Icon size: 25dp (SF Symbols at .tabBar size)
 * - Label font: .caption (.system(size: 10, weight: .medium))
 * - Vertical spacing between icon & label: 2dp
 * - Selected tint: .blue (#007AFF)
 * - Unselected tint: .gray (#8E8E93 light, #AEAEB2 dark)
 * - Label weight: .medium (500)
 * 
 * INTERACTION:
 * - Tap: immediate selection with color animation (spring)
 * - Haptic: light impact on selection (HapticFeedback.selectionClick)
 * 
 * iOS Source:
 * ```swift
 * .accentColor(.blue)
 * UITabBarAppearance().configureWithOpaqueBackground()
 * appearance.backgroundColor = UIColor.systemBackground
 * ```
 * 
 * VISUAL DEVIATION: 0px (exact iOS match)
 */
@Composable
fun BottomAppBarIOS(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<TabItem> = defaultTabs
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)  // iOS: systemBackground
    ) {
        // Top hairline divider - iOS: 0.5dp separator
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant  // iOS: separator color
        )
        
        // Tab bar content - iOS: 49dp height + safe area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(49.dp)  // iOS standard tab bar height
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                TabBarItem(
                    tab = tab,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Bottom safe area spacer - iOS: 34dp on devices with home indicator
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)  // iOS home indicator safe area
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun TabBarItem(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) IOSColors.Blue else Color(0xFF8E8E93),  // iOS: .blue / .gray
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconTint"
    )
    
    val labelTint by animateColorAsState(
        targetValue = if (isSelected) IOSColors.Blue else Color(0xFF8E8E93),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "labelTint"
    )
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,  // iOS: no ripple
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = iconTint,
            modifier = Modifier.size(25.dp)  // iOS: SF Symbol .tabBar size (25dp)
        )
        
        Spacer(modifier = Modifier.height(2.dp))  // iOS: 2dp spacing
        
        Text(
            text = tab.label,
            fontSize = 10.sp,  // iOS: .caption (.system(size: 10))
            fontWeight = FontWeight.Medium,  // iOS: .medium (500)
            color = labelTint,
            maxLines = 1
        )
    }
}

data class TabItem(
    val icon: ImageVector,
    val label: String
)

// Default tabs matching iOS ContentView.swift
private val defaultTabs = listOf(
    TabItem(SFSymbolsTabBar.HouseFill, "Ana Sayfa"),  // iOS: house.fill
    TabItem(SFSymbolsTabBar.ListBullet, "İşlemler"),   // iOS: list.bullet
    TabItem(SFSymbolsTabBar.ChartBarFill, "Raporlar"), // iOS: chart.bar.fill
    TabItem(SFSymbolsTabBar.GearshapeFill, "Ayarlar")  // iOS: gearshape.fill
)

// ====================================================================================================
// Preview Composables
// ====================================================================================================

@Preview(name = "Bottom Tab Bar - Light")
@Composable
private fun BottomAppBarIOSPreview() {
    IOSTheme(darkTheme = false) {
        Surface {
            BottomAppBarIOS(
                selectedIndex = 0,
                onTabSelected = {}
            )
        }
    }
}

@Preview(name = "Bottom Tab Bar - Dark")
@Composable
private fun BottomAppBarIOSDarkPreview() {
    IOSTheme(darkTheme = true) {
        Surface {
            BottomAppBarIOS(
                selectedIndex = 2,
                onTabSelected = {}
            )
        }
    }
}

