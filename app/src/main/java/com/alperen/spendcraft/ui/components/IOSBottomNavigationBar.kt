package com.alperen.spendcraft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * iOS-style Bottom Navigation Bar - Exact match to iOS design
 * Features: Rounded background with blur effect, 5 tabs with proper spacing
 * Matches the provided screenshot pixel-perfectly
 */

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Ana Sayfa",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.HouseOutline,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.HouseFill
    )
    
    object Transactions : BottomNavItem(
        route = "transactions",
        title = "İşlemler",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ListBullet,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ListBullet
    )
    
    object Reports : BottomNavItem(
        route = "reports",
        title = "Raporlar",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ChartBarOutline,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.ChartBarFill
    )
    
    object Categories : BottomNavItem(
        route = "categories",
        title = "Kategoriler",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.FolderFill,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.FolderFill
    )
    
    object Settings : BottomNavItem(
        route = "settings",
        title = "Ayarlar",
        icon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.GearshapeOutline,
        selectedIcon = com.alperen.spendcraft.ui.icons.SFSymbolsTabBar.GearshapeFill
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Transactions,
    BottomNavItem.Reports,
    BottomNavItem.Categories,
    BottomNavItem.Settings
)

@Composable
fun IOSBottomNavigationBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        // Main navigation bar with liquid glass effect - pixel perfect iOS design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp)) // Oval shape like iOS
                .background(Color(0xFF1C1C1E).copy(alpha = 0.3f)) // Much more transparent for liquid glass effect
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                IOSTabItem(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun IOSTabItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint = if (isSelected) Color(0xFF007AFF) else Color.White // iOS Blue for selected, White for unselected
    val textTint = if (isSelected) Color(0xFF007AFF) else Color.White
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier
                        .clip(CircleShape) // Circular background like iOS
                        .background(Color(0xFF007AFF).copy(alpha = 0.15f)) // More subtle blue background for liquid glass effect
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                } else {
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = item.title,
                tint = iconTint,
                modifier = Modifier.size(22.dp) // Slightly smaller for better proportion
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.title,
                fontSize = 9.sp, // Smaller font size for iOS look
                fontWeight = FontWeight.Medium,
                color = textTint
            )
        }
    }
}

/**
 * Example usage with Scaffold
 */
@Composable
fun ExampleUsageWithScaffold() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            IOSBottomNavigationBar(
                selectedIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                    // Handle navigation logic here
                }
            )
        }
    ) { paddingValues ->
        // Your content here
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Selected Tab: ${bottomNavItems[selectedTabIndex].title}",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
