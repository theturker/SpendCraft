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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperen.spendcraft.navigation.AppNavHost
import com.alperen.spendcraft.core.ui.SplashScreen
import com.alperen.spendcraft.reminder.ReminderScheduler
import com.alperen.spendcraft.feature.welcome.ui.WelcomeScreen
import com.alperen.spendcraft.feature.onboarding.OnboardingScreen
import com.alperen.spendcraft.FirstLaunchHelper
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.auth.AuthState
import com.alperen.spendcraft.auth.ui.LoginScreen
import com.alperen.spendcraft.auth.ui.RegisterScreen
import com.alperen.spendcraft.auth.ui.ForgotPasswordScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.alperenturker.spendcraft.ui.theme.SpendCraftTheme
import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme as CoreTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var firstLaunchHelper: FirstLaunchHelper
    
    @Inject
    lateinit var googleAuthService: com.alperen.spendcraft.auth.GoogleAuthService
    
    private var googleSignInResult by mutableStateOf<GoogleSignInAccount?>(null)
    
    private val googleSignInLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            googleSignInResult = account
        } catch (e: ApiException) {
            println("Google Sign-In failed: ${e.message}")
            googleSignInResult = null
        }
    }
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ReminderScheduler.scheduleDaily(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Splash screen'i hemen başlat - Android native splash'i bypass et
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = false)
            val isFirstLaunch by firstLaunchHelper.isFirstLaunch.collectAsState(initial = true)
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.authState.collectAsState()
            
            var currentAuthScreen by remember { mutableStateOf("login") }
            var showOnboarding by remember { mutableStateOf(false) }
            var showSplash by remember { mutableStateOf(true) }
            
            // Google Auth Service'i initialize et
            LaunchedEffect(Unit) {
                googleAuthService.initialize(context)
            }
            
            // Google Sign-In result'u handle et
            LaunchedEffect(googleSignInResult) {
                googleSignInResult?.let { account ->
                    authViewModel.signInWithGoogle(account)
                    googleSignInResult = null
                }
            }

            SpendCraftTheme(darkTheme = isDarkMode) {
                Surface(color = CoreTheme.colors.background) {
                    when {
                        showSplash -> {
                            SplashScreen(
                                onLoadingComplete = {
                                    showSplash = false
                                }
                            )
                        }
                        isFirstLaunch -> {
                            WelcomeScreen(
                                onStart = { 
                                    showOnboarding = true
                                }
                            )
                        }
                        showOnboarding -> {
                            OnboardingScreen(
                                onFinish = {
                                    showOnboarding = false
                                    // Mark first launch as completed
                                    scope.launch {
                                        firstLaunchHelper.setFirstLaunchCompleted()
                                    }
                                }
                            )
                        }
                        authState is AuthState.Unauthenticated -> {
                            when (currentAuthScreen) {
                                "login" -> {
                                    LoginScreen(
                                        onLoginSuccess = { /* Auth state will handle navigation */ },
                                        onNavigateToRegister = { currentAuthScreen = "register" },
                                        onNavigateToForgotPassword = { currentAuthScreen = "forgot" },
                                        onGoogleSignInResult = { account ->
                                            googleSignInResult = account
                                        }
                                    )
                                }
                                "register" -> {
                                    RegisterScreen(
                                        onRegisterSuccess = { /* Auth state will handle navigation */ },
                                        onNavigateToLogin = { currentAuthScreen = "login" }
                                    )
                                }
                                "forgot" -> {
                                    ForgotPasswordScreen(
                                        onNavigateToLogin = { currentAuthScreen = "login" }
                                    )
                                }
                            }
                        }
                        authState is AuthState.Authenticated -> {
                            AppNavHost()
                        }
                    }
                }
            }
            
            // Request notification permission on Android 13+ (splash sonrası)
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        ReminderScheduler.scheduleDaily(context as MainActivity)
                    }
                } else {
                    ReminderScheduler.scheduleDaily(context as MainActivity)
                }
            }
        }
    }
}