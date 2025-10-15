package com.alperen.spendcraft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.components.IOSBottomNavigationBar
import com.alperen.spendcraft.ui.components.bottomNavItems

/**
 * Test Screen for iOS-style Bottom Navigation Bar
 * This screen demonstrates the exact implementation matching the provided screenshot
 */
@Composable
fun BottomNavigationTestScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)), // Dark background like in screenshot
        bottomBar = {
            // Transparent container so content shows through bottom bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                IOSBottomNavigationBar(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF000000)), // Dark background
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "iOS Bottom Navigation Test",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Selected Tab: ${bottomNavItems[selectedTabIndex].title}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "This implementation matches the iOS design exactly:\n" +
                            "• Rounded background with blur effect\n" +
                            "• 5 tabs: Ana Sayfa, İşlemler, Raporlar, Kategoriler, Ayarlar\n" +
                            "• Blue color for selected item (#007AFF)\n" +
                            "• White color for unselected items\n" +
                            "• Proper spacing and typography\n" +
                            "• Accessibility support",
                    color = Color(0xFF8E8E93), // iOS secondary text color
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
