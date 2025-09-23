package com.alperen.spendcraft.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.alperen.spendcraft.feature.settings.ui.SettingsScreen
import com.alperen.spendcraft.feature.settings.ui.CategoryManagementScreen
import com.alperen.spendcraft.navigation.DeepLinkHandler

object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val ADD_INCOME = "add_income"
    const val ADD_EXPENSE = "add_expense"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val CATEGORY_MANAGEMENT = "category_management"
    const val BUDGET_MANAGEMENT = "budget_management"
    const val ALL_TRANSACTIONS = "all_transactions"
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
            TransactionsScreen(
                viewModel = vm,
                onAdd = { navController.navigate(Routes.ADD) },
                onAddIncome = { navController.navigate(Routes.ADD_INCOME) },
                onAddExpense = { navController.navigate(Routes.ADD_EXPENSE) },
                onReports = { navController.navigate(Routes.REPORTS) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onAllTransactions = { navController.navigate(Routes.ALL_TRANSACTIONS) }
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
            com.alperen.spendcraft.feature.budget.ui.BudgetManagementScreen(
                budgets = budgetViewModel.budgets.collectAsState().value,
                categories = budgetViewModel.categories.collectAsState().value,
                spentAmounts = budgetViewModel.spentAmounts.collectAsState().value,
                onAddBudget = { budget -> 
                    budgetViewModel.addBudget(budget)
                },
                onUpdateBudget = { budget -> 
                    budgetViewModel.updateBudget(budget)
                },
                onDeleteBudget = { categoryId -> 
                    budgetViewModel.deleteBudget(categoryId)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ALL_TRANSACTIONS) {
            com.alperen.spendcraft.feature.transactions.ui.AllTransactionsScreen(
                transactions = vm.items.collectAsState().value,
                onBack = { navController.popBackStack() }
            )
        }
    }
}




