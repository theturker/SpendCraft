package com.alperen.spendcraft

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperen.spendcraft.navigation.AppNavHost
import com.alperen.spendcraft.core.ui.SpendCraftTheme
import com.alperen.spendcraft.reminder.ReminderScheduler
import com.alperen.spendcraft.feature.welcome.ui.WelcomeScreen
import com.alperen.spendcraft.FirstLaunchHelper
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.auth.AuthState
import com.alperen.spendcraft.auth.ui.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var firstLaunchHelper: FirstLaunchHelper
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ReminderScheduler.scheduleDaily(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ReminderScheduler.scheduleDaily(this)
            }
        } else {
            ReminderScheduler.scheduleDaily(this)
        }
        
        setContent {
            val context = LocalContext.current
            val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = false)
            val isFirstLaunch by firstLaunchHelper.isFirstLaunch.collectAsState(initial = true)
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.authState.collectAsState()

            SpendCraftTheme(darkTheme = isDarkMode) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    when {
                        isFirstLaunch -> {
                            WelcomeScreen(
                                onStart = { 
                                    // Mark first launch as completed
                                    // This will be handled in a coroutine
                                }
                            )
                        }
                        authState is AuthState.Unauthenticated -> {
                            LoginScreen(
                                onLoginSuccess = { /* Navigation will be handled by auth state change */ },
                                onNavigateToRegister = { /* TODO: Navigate to register */ },
                                onNavigateToForgotPassword = { /* TODO: Navigate to forgot password */ }
                            )
                        }
                        authState is AuthState.Authenticated -> {
                            AppNavHost(
                                deepLinkUri = intent.data
                            )
                        }
                        else -> {
                            // Loading state
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)))
    }
}




