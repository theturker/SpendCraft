package com.alperen.spendcraft.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.alperen.spendcraft.R

object DeepLinkHandler {
    
    fun handleDeepLink(context: Context, uri: Uri?): Intent? {
        if (uri == null) return null
        
        return when (uri.scheme) {
            "app" -> handleAppScheme(uri)
            "https" -> handleHttpsScheme(uri)
            else -> null
        }
    }
    
    private fun handleAppScheme(uri: Uri): Intent? {
        return when (uri.host) {
            "add" -> {
                val type = uri.getQueryParameter("type")
                when (type) {
                    "expense" -> createAddExpenseIntent()
                    "income" -> createAddIncomeIntent()
                    else -> createAddTransactionIntent()
                }
            }
            "reports" -> createReportsIntent()
            "quickadd" -> createQuickAddIntent()
            "settings" -> createSettingsIntent()
            else -> null
        }
    }
    
    private fun handleHttpsScheme(uri: Uri): Intent? {
        // Handle web deep links if needed
        return when {
            uri.host?.contains("spendcraft") == true -> {
                // Handle web deep links
                createMainIntent()
            }
            else -> null
        }
    }
    
    private fun createAddExpenseIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://add?type=expense")
            putExtra("transaction_type", "expense")
        }
    }
    
    private fun createAddIncomeIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://add?type=income")
            putExtra("transaction_type", "income")
        }
    }
    
    private fun createAddTransactionIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://add")
        }
    }
    
    private fun createReportsIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://reports")
        }
    }
    
    private fun createQuickAddIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://quickadd")
        }
    }
    
    private fun createSettingsIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://settings")
        }
    }
    
    private fun createMainIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
    }
}

object DeepLinkRoutes {
    const val ADD_EXPENSE = "app://add?type=expense"
    const val ADD_INCOME = "app://add?type=income"
    const val ADD_TRANSACTION = "app://add"
    const val REPORTS = "app://reports"
    const val QUICK_ADD = "app://quickadd"
    const val SETTINGS = "app://settings"
}
