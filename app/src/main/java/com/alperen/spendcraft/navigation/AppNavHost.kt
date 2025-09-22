package com.alperen.spendcraft.navigation

import androidx.compose.runtime.Composable
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

object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
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
                onAddIncome = { navController.navigate(Routes.ADD) },
                onAddExpense = { navController.navigate(Routes.ADD) },
                onReports = { navController.navigate(Routes.REPORTS) },
                onSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.ADD) {
            AddTransactionScreen(
                categories = vm.categories,
                onSave = { amountMinor, note, categoryId, isIncome ->
                    vm.saveTransaction(amountMinor, note, categoryId, isIncome)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.REPORTS) {
            ReportsScreen(
                transactionsFlow = vm.items
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}




