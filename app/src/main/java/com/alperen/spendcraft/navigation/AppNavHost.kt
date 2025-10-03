package com.alperen.spendcraft.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alperen.spendcraft.feature.transactions.ui.AddTransactionScreen
import com.alperen.spendcraft.feature.transactions.ui.TransactionsScreen
import com.alperen.spendcraft.feature.transactions.TransactionsViewModel
import com.alperen.spendcraft.feature.reports.ReportsScreen
import com.alperen.spendcraft.feature.reports.ExportReportScreen
import com.alperen.spendcraft.feature.settings.ui.SettingsScreen
import com.alperen.spendcraft.feature.settings.ui.CategoryManagementScreen
import com.alperen.spendcraft.feature.paywall.PaywallScreen
import com.alperen.spendcraft.feature.paywall.navigation.PaywallNavigation
import com.alperen.spendcraft.feature.premiumdebug.PremiumDebugScreen
import com.alperen.spendcraft.feature.ai.AISuggestionsScreen
import com.alperen.spendcraft.feature.settings.AISettingsScreen
import com.alperen.spendcraft.feature.accounts.AccountsScreen
import com.alperen.spendcraft.feature.recurrence.RecurringListScreen
import com.alperen.spendcraft.feature.recurrence.AddRecurringRuleScreen
import com.alperen.spendcraft.feature.recurrence.RecurringEditorScreen
import com.alperen.spendcraft.feature.sharing.SharingScreen
import com.alperen.spendcraft.feature.notifications.NotificationsScreen
import com.alperen.spendcraft.feature.onboarding.OnboardingScreen
import com.alperen.spendcraft.feature.achievements.AchievementsScreen
import com.alperen.spendcraft.navigation.DeepLinkHandler
import kotlinx.coroutines.flow.map

object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val ADD_INCOME = "add_income"
    const val ADD_EXPENSE = "add_expense"
    const val REPORTS = "reports"
    const val EXPORT_REPORT = "export_report"
    const val REPORT_PREVIEW = "report_preview/{uri}"
    const val SETTINGS = "settings"
    const val CATEGORY_MANAGEMENT = "category_management"
    const val BUDGET_MANAGEMENT = "budget_management"
    const val ALL_TRANSACTIONS = "all_transactions"
    const val AI_SUGGESTIONS = "ai_suggestions"
    // AI_SETTINGS removed - API key is hardcoded
    const val PAYWALL = PaywallNavigation.ROUTE_PAYWALL
    const val PREMIUM_DEBUG = "premium_debug"
    const val ACCOUNTS = "accounts"
    const val RECURRING = "recurring"
    const val ADD_RECURRING_RULE = "add_recurring_rule"
    const val EDIT_RECURRING_RULE = "edit_recurring_rule"
    const val SHARING = "sharing"
    const val NOTIFICATIONS = "notifications"
    const val ONBOARDING = "onboarding"
    const val ACHIEVEMENTS = "achievements"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    deepLinkUri: android.net.Uri? = null
) {
    val vm: TransactionsViewModel = hiltViewModel()
    
    // Handle deep links
    val context = LocalContext.current
    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { uri ->
            val intent = DeepLinkHandler.handleDeepLink(context, uri)
            intent?.let { deepLinkIntent ->
                when (deepLinkIntent.data?.toString()) {
                    "app://add?type=expense" -> navController.navigate(Routes.ADD_EXPENSE)
                    "app://add?type=income" -> navController.navigate(Routes.ADD_INCOME)
                    "app://add" -> navController.navigate(Routes.ADD)
                    "app://reports" -> navController.navigate(Routes.REPORTS)
                    "app://settings" -> navController.navigate(Routes.SETTINGS)
                    "app://quickadd" -> {
                        // Handle quick add - could show a dialog or navigate to quick add screen
                        navController.navigate(Routes.ADD)
                    }
                }
            }
        }
    }
    
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            val paywallVm: com.alperen.spendcraft.feature.paywall.PaywallViewModel = hiltViewModel()
            val isPremium by paywallVm.isPremium.collectAsState()
            TransactionsScreen(
                viewModel = vm,
                onAdd = { /* Bottom sheet will handle this */ },
                onAddIncome = { /* Bottom sheet will handle this */ },
                onAddExpense = { /* Bottom sheet will handle this */ },
                onReports = { navController.navigate(Routes.REPORTS) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onAllTransactions = { navController.navigate(Routes.ALL_TRANSACTIONS) },
                onNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
                isPremium = isPremium
            )
        }
        composable(Routes.ADD) {
            AddTransactionScreen(
                categories = vm.categories,
                initialTransactionType = null,
                onSave = { amountMinor, note, categoryId, isIncome ->
                    vm.saveTransaction(amountMinor, note, categoryId, null, isIncome) // null = default account
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ADD_INCOME) {
            AddTransactionScreen(
                categories = vm.categories,
                initialTransactionType = true,
                onSave = { amountMinor, note, categoryId, isIncome ->
                    vm.saveTransaction(amountMinor, note, categoryId, null, isIncome) // null = default account
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ADD_EXPENSE) {
            AddTransactionScreen(
                categories = vm.categories,
                initialTransactionType = false,
                onSave = { amountMinor, note, categoryId, isIncome ->
                    vm.saveTransaction(amountMinor, note, categoryId, null, isIncome) // null = default account
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.REPORTS) {
            ReportsScreen(
                transactionsFlow = vm.items,
                categoriesFlow = vm.categories,
                onBack = { navController.popBackStack() },
                onExport = { navController.navigate(Routes.EXPORT_REPORT) }
            )
        }
        composable(Routes.EXPORT_REPORT) {
            ExportReportScreen(
                transactions = vm.items.collectAsState().value,
                categories = vm.categories.collectAsState().value,
                onNavigateBack = { navController.popBackStack() },
                onExport = { format, dateRange, startDate, endDate ->
                    // TODO: Implement export functionality
                },
                onPreview = { uri ->
                    val encoded = java.net.URLEncoder.encode(uri.toString(), "UTF-8")
                    val route = Routes.REPORT_PREVIEW.replace("{uri}", encoded)
                    navController.navigate(route)
                }
            )
        }
        composable(Routes.REPORT_PREVIEW) { backStackEntry ->
            val uriArg = backStackEntry.arguments?.getString("uri") ?: ""
            com.alperen.spendcraft.feature.reports.ReportPreviewScreen(
                uriString = uriArg,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                categories = vm.categories.collectAsState().value,
                onAddCategory = { name -> 
                    vm.addCategory(name)
                },
                onDeleteCategory = { id -> 
                    vm.removeCategory(id)
                },
                onNavigateToCategories = { 
                    navController.navigate(Routes.CATEGORY_MANAGEMENT) 
                },
                onNavigateToBudgets = { 
                    navController.navigate(Routes.BUDGET_MANAGEMENT) 
                },
                onNavigateToPremiumDebug = {
                    navController.navigate(Routes.PREMIUM_DEBUG)
                },
                onNavigateToAISuggestions = {
                    navController.navigate(Routes.AI_SUGGESTIONS)
                },
                onNavigateToAccounts = {
                    navController.navigate(Routes.ACCOUNTS)
                },
                onNavigateToRecurring = {
                    navController.navigate(Routes.RECURRING)
                },
                onNavigateToSharing = {
                    navController.navigate(Routes.SHARING)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToAchievements = {
                    navController.navigate(Routes.ACHIEVEMENTS)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CATEGORY_MANAGEMENT) {
            CategoryManagementScreen(
                categories = vm.categories.collectAsState().value,
                onAddCategory = { name -> 
                    vm.addCategory(name)
                },
                onDeleteCategory = { id -> 
                    vm.removeCategory(id)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.BUDGET_MANAGEMENT) {
            val budgetViewModel: com.alperen.spendcraft.feature.budget.BudgetViewModel = hiltViewModel()
            val paywallViewModel: com.alperen.spendcraft.feature.paywall.PaywallViewModel = hiltViewModel()
            val isPremium by paywallViewModel.isPremium.collectAsState()
            
            android.util.Log.d("AppNavHost", "Budget Management - isPremium: $isPremium")
            
            com.alperen.spendcraft.feature.budget.ui.BudgetManagementScreen(
                budgets = budgetViewModel.budgets.collectAsState().value,
                categories = budgetViewModel.categories.collectAsState().value,
                spentAmounts = budgetViewModel.spentAmounts.collectAsState().value,
                isPremium = isPremium,
                onAddBudget = { budget -> budgetViewModel.addBudgetWithLimit(budget, isPremium) },
                onUpdateBudget = { budget -> budgetViewModel.updateBudget(budget) },
                onDeleteBudget = { categoryId -> budgetViewModel.deleteBudget(categoryId) },
                onBack = { navController.popBackStack() },
                onNavigateToPaywall = { 
                    android.util.Log.d("AppNavHost", "Navigating to paywall from Budget Management")
                    navController.navigate(Routes.PAYWALL) 
                },
                onCalculateSpentAmounts = { budgetViewModel.calculateSpentAmounts() }
            )
        }
        composable(Routes.ALL_TRANSACTIONS) {
            com.alperen.spendcraft.feature.transactions.ui.AllTransactionsScreen(
                transactions = vm.items.collectAsState(initial = emptyList()).value,
                categories = vm.categories.collectAsState(initial = emptyList()).value,
                onBack = { navController.popBackStack() },
                onAddCategoryToTransaction = { transactionId, categoryId ->
                    vm.updateTransactionCategory(transactionId, categoryId)
                }
            )
        }
        composable(Routes.PAYWALL) {
            PaywallScreen(
                onNavigateUp = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable(Routes.PREMIUM_DEBUG) {
            PremiumDebugScreen(
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable(Routes.AI_SUGGESTIONS) {
            val aiViewModel: com.alperen.spendcraft.feature.ai.AIViewModel = hiltViewModel()
            val isPremium = aiViewModel.billingRepository.isPremium.collectAsState(initial = false).value
            
            // Get transaction data for AI analysis
            val transactions = vm.items.collectAsState(initial = emptyList()).value
            val categories = vm.categories.collectAsState(initial = emptyList()).value
            
            // Calculate category breakdown
            val categoryBreakdown = transactions
                .filter { it.type == com.alperen.spendcraft.core.model.TransactionType.EXPENSE }
                .groupBy { it.categoryId }
                .mapValues { (categoryId, transactions) ->
                    transactions.sumOf { it.amount.minorUnits }
                }
                .mapKeys { (categoryId, _) ->
                    categories.find { it.id == categoryId }?.name ?: "Bilinmeyen"
                }
            
            val totalExpense = transactions.filter { it.type == com.alperen.spendcraft.core.model.TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
            val totalIncome = transactions.filter { it.type == com.alperen.spendcraft.core.model.TransactionType.INCOME }.sumOf { it.amount.minorUnits }
            val savings = totalIncome - totalExpense
            
            AISuggestionsScreen(
                aiRepository = aiViewModel.aiRepository,
                categoryBreakdown = categoryBreakdown,
                totalExpense = totalExpense,
                income = totalIncome,
                expenses = totalExpense,
                savings = savings,
                isPremium = isPremium,
                onUpgrade = { navController.navigate(Routes.PAYWALL) },
                onBack = { navController.popBackStack() }
            )
        }
        // AI Settings removed - API key is hardcoded
        composable(Routes.ACCOUNTS) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val accountsFlow = vm.accounts
                .map { list ->
                    list.map { acc ->
                        com.alperen.spendcraft.data.db.entities.AccountEntity(
                            id = acc.id ?: 0,
                            name = acc.name,
                            type = "CASH",
                            currency = com.alperen.spendcraft.core.ui.CurrencyFormatter.getCurrencySymbol(context),
                            isDefault = false,
                            archived = false
                        )
                    }
                }
            AccountsScreen(
                accountsFlow = accountsFlow,
                onAddAccount = { vm.addAccount("Yeni Hesap") },
                onEditAccount = { account -> vm.updateAccountName(account.id, account.name) },
                onArchiveAccount = { account -> vm.removeAccount(account.id) },
                onSetDefaultAccount = { _ -> /* TODO: implement set default when repo supports */ },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.RECURRING) {
            val recurringViewModel: com.alperen.spendcraft.feature.recurrence.RecurringViewModel = hiltViewModel()
            val isPremium = recurringViewModel.billingRepository.isPremium.collectAsState(initial = false).value
            
            RecurringListScreen(
                recurringTransactionsFlow = recurringViewModel.recurringTransactions,
                onAddRule = { 
                    // Navigate to add recurring rule screen
                    navController.navigate(Routes.ADD_RECURRING_RULE)
                },
                onEditRule = { transaction ->
                    // Navigate to edit recurring rule screen
                    navController.navigate("${Routes.EDIT_RECURRING_RULE}/${transaction.id}")
                },
                onDeleteRule = { transaction ->
                    // TODO: Implement delete functionality
                },
                isPremium = isPremium,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ADD_RECURRING_RULE) {
            val recurringViewModel: com.alperen.spendcraft.feature.recurrence.RecurringViewModel = hiltViewModel()
            AddRecurringRuleScreen(
                categories = vm.categories.collectAsState().value,
                onSave = { 
                    println("🔍 DEBUG: onSave callback çağrıldı, navigation geri dönüyor")
                    navController.popBackStack() 
                },
                onCancel = { navController.popBackStack() },
                viewModel = recurringViewModel
            )
        }
        composable("${Routes.EDIT_RECURRING_RULE}/{ruleId}") { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId")?.toLongOrNull() ?: 0L
            RecurringEditorScreen(
                templateTransaction = null, // TODO: Get transaction from ViewModel
                onSave = { /* TODO: Implement save functionality */ },
                onCancel = { navController.popBackStack() },
                isPremium = false // TODO: Get premium state from ViewModel
            )
        }
        composable(Routes.SHARING) {
            val sharingViewModel: com.alperen.spendcraft.feature.sharing.SharingViewModel = hiltViewModel()
            val isPremium = sharingViewModel.billingRepository.isPremium.collectAsState(initial = false).value
            
            android.util.Log.d("AppNavHost", "Sharing - isPremium: $isPremium")
            
            SharingScreen(
                membersFlow = kotlinx.coroutines.flow.flowOf(emptyList()),
                onInviteMember = { /* TODO: Implement */ },
                onUpdateRole = { _, _ -> /* TODO: Implement */ },
                onRemoveMember = { /* TODO: Implement */ },
                isPremium = isPremium,
                onUpgrade = { 
                    android.util.Log.d("AppNavHost", "Sharing - Premium'a Geç button clicked, navigating to paywall")
                    navController.navigate(Routes.PAYWALL) 
                },
                onBack = { navController.popBackStack() }
            )
        }
        // Dashboard route removed
        composable(Routes.NOTIFICATIONS) {
            val notificationsViewModel: com.alperen.spendcraft.feature.notifications.NotificationsViewModel = hiltViewModel()
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                viewModel = notificationsViewModel
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = { navController.popBackStack() }
            )
        }
        composable(Routes.ACHIEVEMENTS) {
            val achievementsViewModel: com.alperen.spendcraft.feature.achievements.AchievementsViewModel = hiltViewModel()
            AchievementsScreen(
                onBack = { navController.popBackStack() },
                viewModel = achievementsViewModel
            )
        }
    }
}




