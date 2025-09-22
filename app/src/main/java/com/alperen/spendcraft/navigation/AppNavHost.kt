package com.alperen.spendcraft.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val ADD_INCOME = "add_income"
    const val ADD_EXPENSE = "add_expense"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val CATEGORY_MANAGEMENT = "category_management"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val vm: TransactionsViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            TransactionsScreen(
                viewModel = vm,
                onAdd = { navController.navigate(Routes.ADD) },
                onAddIncome = { navController.navigate(Routes.ADD_INCOME) },
                onAddExpense = { navController.navigate(Routes.ADD_EXPENSE) },
                onReports = { navController.navigate(Routes.REPORTS) },
                onSettings = { navController.navigate(Routes.SETTINGS) }
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
    }
}




