package com.alperen.spendcraft.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.IOSColors
import com.alperen.spendcraft.ui.icons.SFSymbolsTabBar
import com.alperen.spendcraft.ui.components.IOSBottomNavigationBar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alperen.spendcraft.feature.dashboard.DashboardViewModel
import com.alperen.spendcraft.feature.dashboard.ui.DashboardScreen
import com.alperen.spendcraft.feature.transactions.ui.TransactionsScreen
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.feature.reports.ReportsScreen
import com.alperen.spendcraft.feature.settings.ui.SettingsScreen

/**
 * iOS-style Tab Navigation
 * 
 * 5 Tab:
 * 1. Ana Sayfa (Dashboard)
 * 2. İşlemler
 * 3. Raporlar
 * 4. Kategoriler
 * 5. Ayarlar
 */

sealed class TabScreen(
    val route: String,
    val title: String,
    val icon: Int,
    val selectedIcon: Int
) {
    object Dashboard : TabScreen(
        route = "dashboard_tab",
        title = "Ana Sayfa",
        icon = com.alperen.spendcraft.core.ui.R.drawable.ic_house_outline,
        selectedIcon = com.alperen.spendcraft.core.ui.R.drawable.ic_house_fill
    )
    
    object Transactions : TabScreen(
        route = "transactions_tab",
        title = "İşlemler",
        icon = com.alperen.spendcraft.core.ui.R.drawable.ic_list_bullet_outline,
        selectedIcon = com.alperen.spendcraft.core.ui.R.drawable.ic_list_bullet
    )
    
    object Reports : TabScreen(
        route = "reports_tab",
        title = "Raporlar",
        icon = com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_outline,
        selectedIcon = com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill
    )
    
    object Categories : TabScreen(
        route = "categories_tab",
        title = "Kategoriler",
        icon = com.alperen.spendcraft.core.ui.R.drawable.ic_folder_outline,
        selectedIcon = com.alperen.spendcraft.core.ui.R.drawable.ic_folder_fill
    )
    
    object Settings : TabScreen(
        route = "settings_tab",
        title = "Ayarlar",
        icon = com.alperen.spendcraft.core.ui.R.drawable.ic_gearshape_outline,
        selectedIcon = com.alperen.spendcraft.core.ui.R.drawable.ic_gearshape_fill
    )
}

private val tabScreens = listOf(
    TabScreen.Dashboard,
    TabScreen.Transactions,
    TabScreen.Reports,
    TabScreen.Categories,
    TabScreen.Settings
)

@Composable
fun MainTabNavigation(
    onNavigateToAddTransaction: (Boolean?) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToAISettings: () -> Unit = {},
    onNavigateToCategoryManagement: () -> Unit = {},
    onNavigateToBudgetManagement: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToRecurring: () -> Unit = {},
    onNavigateToSharing: () -> Unit = {},
    onNavigateToExport: () -> Unit = {},
    onNavigateToUserProfiling: () -> Unit = {},  // iOS UserProfilingView
    onNavigateToAccountInfo: () -> Unit = {},  // iOS AccountInfoView
    onNavigateToNotificationSettings: () -> Unit = {},  // iOS NotificationSettingsView
    onNavigateToCurrencyPicker: () -> Unit = {},  // iOS CurrencyPickerView
    isPremium: Boolean = false
) {
    val navController = rememberNavController()
    
    // Tab index state
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // iOS VStack pattern: Column { content + banner + tabBar }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding() // Safe area için
    ) {
        // Content area - takes remaining space
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = TabScreen.Dashboard.route,
                modifier = Modifier.fillMaxSize()
            ) {
            // 1. Dashboard Tab
            composable(TabScreen.Dashboard.route) {
                val context = LocalContext.current
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                val transactions by dashboardViewModel.transactions.collectAsState()
                val currentBalance by dashboardViewModel.currentBalance.collectAsState()
                val totalIncome by dashboardViewModel.totalIncome.collectAsState()
                val totalExpense by dashboardViewModel.totalExpense.collectAsState()
                val currentStreak by dashboardViewModel.currentStreak.collectAsState()
                val longestStreak by dashboardViewModel.longestStreak.collectAsState()
                val achievementsCount by dashboardViewModel.achievementsCount.collectAsState()
                val totalPoints by dashboardViewModel.totalPoints.collectAsState()
                val achievements by dashboardViewModel.achievements.collectAsState() // Gerçek achievement verisi
                val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val unreadCount by notificationsViewModel.unreadCount.collectAsState()
                
                DashboardScreen(
                    transactions = transactions,
                    currentBalance = currentBalance,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    achievementsCount = achievementsCount,
                    totalPoints = totalPoints,
                    achievements = achievements, // Gerçek achievement verisi
                    profilingCompleted = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                        .getBoolean("profilingCompleted", false),
                    unreadCount = unreadCount, // iOS'taki badge
                    onAddIncome = { onNavigateToAddTransaction(true) },
                    onAddExpense = { onNavigateToAddTransaction(false) },
                    onNotifications = onNavigateToNotifications,
                    onAchievements = onNavigateToAchievements,
                    onUserProfiling = onNavigateToUserProfiling  // iOS: .sheet(isPresented: $showUserProfiling)
                )
            }
            
            // 2. Transactions Tab
            composable(TabScreen.Transactions.route) {
                val transactionsViewModel: TransactionsViewModel = hiltViewModel()
                val transactions by transactionsViewModel.items.collectAsState()
                val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val unreadCount by notificationsViewModel.unreadCount.collectAsState()
                
                com.alperen.spendcraft.feature.transactions.ui.TransactionsListScreen(
                    transactions = transactions,
                    onAddTransaction = { onNavigateToAddTransaction(null) },
                    onDeleteTransaction = { transaction ->
                        // TODO: Implement delete functionality
                    },
                    onNotifications = onNavigateToNotifications, // iOS'taki notification button
                    unreadCount = unreadCount // iOS'taki badge
                )
            }
            
            // 3. Reports Tab
            composable(TabScreen.Reports.route) {
                val transactionsViewModel: TransactionsViewModel = hiltViewModel()
                val transactions by transactionsViewModel.items.collectAsState()
                val categories by transactionsViewModel.categories.collectAsState()
                val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val unreadCount by notificationsViewModel.unreadCount.collectAsState()
                
                val totalIncome = transactions
                    .filter { it.type == com.alperen.spendcraft.core.model.TransactionType.INCOME }
                    .sumOf { it.amount.minorUnits / 100.0 }
                val totalExpense = transactions
                    .filter { it.type == com.alperen.spendcraft.core.model.TransactionType.EXPENSE }
                    .sumOf { it.amount.minorUnits / 100.0 }
                
                com.alperen.spendcraft.feature.reports.IOSReportsScreen(
                    transactions = transactions,
                    categories = categories,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    onNavigateToAISuggestions = onNavigateToAISettings,
                    onNotifications = onNavigateToNotifications, // iOS'taki notification button
                    unreadCount = unreadCount // iOS'taki badge
                )
            }
            
            // 4. Categories Tab
            composable(TabScreen.Categories.route) {
                val transactionsViewModel: TransactionsViewModel = hiltViewModel()
                val categories by transactionsViewModel.categories.collectAsState()
                val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val unreadCount by notificationsViewModel.unreadCount.collectAsState()
                val budgetViewModel: com.alperen.spendcraft.feature.budget.BudgetViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val budgets by budgetViewModel.budgets.collectAsState()
                val spentAmountsRaw by budgetViewModel.spentAmounts.collectAsState()
                
                // iOS: Load spent amounts on appear
                LaunchedEffect(Unit) {
                    budgetViewModel.calculateSpentAmounts()
                }
                
                // Convert budget list to map: categoryId (Long) -> budget limit (Double)
                val budgetMap: Map<Long, Double> = budgets.associate { budget ->
                    (budget.categoryId.toLongOrNull() ?: 0L) to (budget.monthlyLimitMinor / 100.0)
                }
                
                // Convert spent amounts: categoryId (String) -> spent (Long) to categoryId (Long) -> spent (Double)
                val spentAmounts: Map<Long, Double> = spentAmountsRaw.mapKeys { (key, _) ->
                    key.toLongOrNull() ?: 0L
                }.mapValues { (_, value) ->
                    value / 100.0
                }
                
                com.alperen.spendcraft.feature.dashboard.ui.IOSCategoriesScreen(
                    categories = categories,
                    budgets = budgetMap,
                    spent = spentAmounts,
                    onAddCategory = { name, icon, color ->
                        transactionsViewModel.addCategory(name, icon, color)
                    },
                    onCategoryClick = { category ->
                        // iOS: Show AddBudgetView - already handled in IOSCategoriesScreen
                    },
                    onDeleteCategory = { category ->
                        // iOS: deleteCategory(category) - CategoriesView.swift:97-99
                        category.id?.let { transactionsViewModel.removeCategory(it) }
                    },
                    onSaveBudget = { categoryId, amount ->
                        // iOS: budgetViewModel.upsertBudget - CategoriesView.swift:274
                        val budget = com.alperen.spendcraft.core.model.Budget(
                            categoryId = categoryId.toString(),
                            monthlyLimitMinor = (amount * 100).toLong()
                        )
                        budgetViewModel.addBudget(budget)
                    },
                    onNotifications = onNavigateToNotifications, // iOS'taki notification button
                    unreadCount = unreadCount // iOS'taki badge
                )
            }
            
            // 5. Settings Tab
            composable(TabScreen.Settings.route) {
                val ctx = androidx.compose.ui.platform.LocalContext.current
                val transactionsViewModel: TransactionsViewModel = hiltViewModel()
                val transactions by transactionsViewModel.items.collectAsState()
                val categories by transactionsViewModel.categories.collectAsState()
                val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val unreadCount by notificationsViewModel.unreadCount.collectAsState()
                val dashboardViewModel: com.alperen.spendcraft.feature.dashboard.DashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val totalPoints by dashboardViewModel.totalPoints.collectAsState()
                val authViewModel: com.alperen.spendcraft.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val authState by authViewModel.authState.collectAsState()
                
                // iOS'ta authViewModel'den user bilgileri
                val userName: String = when (val state = authState) {
                    is com.alperen.spendcraft.auth.AuthState.Authenticated -> state.user.displayName ?: "Kullanıcı"
                    else -> "Kullanıcı"
                }
                val userEmail: String = when (val state = authState) {
                    is com.alperen.spendcraft.auth.AuthState.Authenticated -> state.user.email ?: ""
                    else -> ""
                }
                
                // Para birimi - iOS @AppStorage("selectedCurrency")
                val prefs = ctx.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                val selectedCurrency: String = prefs.getString("selectedCurrency", "TRY") ?: "TRY"
                
                com.alperen.spendcraft.feature.settings.ui.IOSSettingsScreen(
                    onNavigateToAccounts = onNavigateToAccounts,
                    onNavigateToAISuggestions = onNavigateToAISettings,
                    onNavigateToRecurring = onNavigateToRecurring,
                    onNavigateToAchievements = onNavigateToAchievements,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToNotificationSettings = onNavigateToNotificationSettings,  // iOS NotificationSettingsView
                    onNavigateToExport = onNavigateToExport,
                    onNavigateToCurrencySettings = onNavigateToCurrencyPicker,  // iOS CurrencyPickerView
                    onNavigateToAccountInfo = onNavigateToAccountInfo,  // iOS AccountInfoView
                    onSignOut = {
                        // iOS'taki sign out - AuthViewModel.signOut()
                        authViewModel.signOut()
                    },
                    userName = userName,
                    userEmail = userEmail,
                    selectedCurrency = selectedCurrency,
                    totalTransactions = transactions.size,
                    totalCategories = categories.size,
                    totalPoints = totalPoints,
                    unreadCount = unreadCount
                )
            }
        } // NavHost end
        } // Box end
        
        // iOS: AdaptiveBannerAdView() - Bottom bar'ın ÜSTÜNDE sabit
        // Reklam banner - scroll edilmez, her zaman görünür
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Shadow effect - iOS: .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                thickness = 1.dp
            )
            
            com.alperen.spendcraft.core.ui.AdMobBanner(
                modifier = Modifier.fillMaxWidth(),
                isPremium = isPremium
            )
        }
        
        // iOS UITabBar - En altta
        IOSBottomNavigationBar(
            selectedIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
                val route = when (index) {
                    0 -> TabScreen.Dashboard.route
                    1 -> TabScreen.Transactions.route
                    2 -> TabScreen.Reports.route
                    3 -> TabScreen.Categories.route
                    4 -> TabScreen.Settings.route
                    else -> TabScreen.Dashboard.route
                }
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    } // Column end
}

/**
 * iOS-style Bottom Navigation Bar - Exact match to iOS design
 * Features: Rounded background with blur effect, 5 tabs with proper spacing
 */
@Composable
private fun IOSStyleTabBar(
    navController: NavHostController,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main navigation bar with rounded background and blur effect
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C1C1E).copy(alpha = 0.8f)) // Dark background with opacity
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Ana Sayfa
            IOSTabItem(
                icon = SFSymbolsTabBar.HouseOutline,
                label = "Ana Sayfa",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            
            // Tab 2: İşlemler
            IOSTabItem(
                icon = SFSymbolsTabBar.ListBullet,
                label = "İşlemler",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
            
            // Tab 3: Raporlar
            IOSTabItem(
                icon = SFSymbolsTabBar.ChartBarOutline,
                label = "Raporlar",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
            
            // Tab 4: Kategoriler
            IOSTabItem(
                icon = SFSymbolsTabBar.FolderFill,
                label = "Kategoriler",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                modifier = Modifier.weight(1f)
            )
            
            // Tab 5: Ayarlar
            IOSTabItem(
                icon = SFSymbolsTabBar.GearshapeOutline,
                label = "Ayarlar",
                isSelected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun IOSTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF007AFF).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                } else {
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp) // Slightly smaller for better proportion
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp, // Smaller font size for iOS look
                fontWeight = FontWeight.Medium,
                color = textTint
            )
        }
    }
}

/**
 * OLD iOS-style Bottom Navigation Bar - Deprecated, use IOSStyleTabBar instead
 */
@Deprecated("Use IOSStyleTabBar instead")
@Composable
private fun TabNavigationBar(
navController: NavHostController,
    tabs: List<TabScreen>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp) // iOS tab bar height
    ) {
        tabs.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) screen.selectedIcon else screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp) // iOS icon size
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IOSColors.Blue, // iOS Blue
                    selectedTextColor = IOSColors.Blue,
                    unselectedIconColor = Color(0xFF8E8E93), // iOS gray
                    unselectedTextColor = Color(0xFF8E8E93),
                    indicatorColor = Color.Transparent // iOS'ta indicator yok
                )
            )
        }
    }
}

