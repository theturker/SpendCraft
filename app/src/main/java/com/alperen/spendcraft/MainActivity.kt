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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import android.content.res.Configuration
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alperen.spendcraft.navigation.AppNavHost
import com.alperen.spendcraft.core.ui.SpendCraftTheme
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var firstLaunchHelper: FirstLaunchHelper
    
    @Inject
    lateinit var googleAuthService: com.alperen.spendcraft.auth.GoogleAuthService
    
    @Inject
    lateinit var billingRepository: com.alperen.spendcraft.core.billing.BillingRepository
    
    private var googleSignInResult by mutableStateOf<GoogleSignInAccount?>(null)
    
    // Native splash ekranı için ready flag
    private var isReady = false
    
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
        // Native splash screen'i 2 saniye tut
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }
        
        super.onCreate(savedInstanceState)
        
        // 2 saniye sonra splash'i kapat
        window.decorView.postDelayed({
            isReady = true
        }, 2000)
        
        // Modern edge-to-edge display için enableEdgeToEdge kullan
        enableEdgeToEdge()
        
        // Window ayarları - Safe area için
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Splash screen'i hemen başlat - Android native splash'i bypass et
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val themeMode by ThemeHelper.getThemeMode(context).collectAsState(initial = ThemeMode.SYSTEM)
            val isFirstLaunch by firstLaunchHelper.isFirstLaunch.collectAsState(initial = true)
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.authState.collectAsState()
            
            // Calculate isDarkMode based on theme mode
            val systemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val isDarkMode = when (themeMode) {
                ThemeMode.SYSTEM -> systemInDarkTheme
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            
            var currentAuthScreen by remember { mutableStateOf("login") }
            
            // Google Auth Service'i initialize et
            LaunchedEffect(Unit) {
                googleAuthService.initialize(context)
            }
            
            // BillingRepository'yi initialize et
            LaunchedEffect(Unit) {
                try {
                    billingRepository.initialize()
                    android.util.Log.d("MainActivity", "Billing initialized successfully")
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Billing initialization failed", e)
                }
            }
            
            // Google Sign-In result'u handle et
            LaunchedEffect(googleSignInResult) {
                googleSignInResult?.let { account ->
                    authViewModel.signInWithGoogle(account)
                    googleSignInResult = null
                }
            }

            SpendCraftTheme(darkTheme = isDarkMode) {
                // Sistem barlarının rengini ve ikon renklerini ayarla
                val view = LocalView.current
                val backgroundColor = MaterialTheme.colorScheme.background
                val isLightTheme = !isDarkMode
                
                // isLightTheme değiştiğinde sistem barlarını güncelle
                LaunchedEffect(isLightTheme, backgroundColor) {
                    val window = (view.context as ComponentActivity).window
                    
                    window.statusBarColor = backgroundColor.toArgb()
                    window.navigationBarColor = backgroundColor.toArgb()
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = isLightTheme
                        isAppearanceLightNavigationBars = isLightTheme
                    }
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Native splash sonrası uygulama akışı
                    when {
                        isFirstLaunch -> {
                            // iOS'taki OnboardingView
                            OnboardingScreen(
                                onFinish = {
                                    // Mark first launch as completed
                                    scope.launch {
                                        firstLaunchHelper.setFirstLaunchCompleted()
                                    }
                                }
                            )
                        }
                        authState is AuthState.Unauthenticated -> {
                            val isLoading by authViewModel.isLoading.collectAsState()
                            val errorMessage by authViewModel.errorMessage.collectAsState()
                            
                            when (currentAuthScreen) {
                                "login" -> {
                                    com.alperen.spendcraft.auth.ui.IOSLoginScreen(
                                        onLoginClick = { email, password ->
                                            scope.launch {
                                                authViewModel.signIn(email, password)
                                            }
                                        },
                                        onNavigateToRegister = { currentAuthScreen = "register" },
                                        onNavigateToForgotPassword = { currentAuthScreen = "forgot" },
                                        isLoading = isLoading,
                                        errorMessage = errorMessage
                                    )
                                }
                                "register" -> {
                                    com.alperen.spendcraft.auth.ui.IOSRegisterScreen(
                                        onRegisterClick = { name, email, password ->
                                            scope.launch {
                                                authViewModel.register(name, email, password, password)
                                            }
                                        },
                                        onNavigateToLogin = { currentAuthScreen = "login" },
                                        isLoading = isLoading,
                                        errorMessage = errorMessage
                                    )
                                }
                                "forgot" -> {
                                    com.alperen.spendcraft.auth.ui.IOSForgotPasswordScreen(
                                        onSendResetLink = { email ->
                                            scope.launch {
                                                authViewModel.sendPasswordReset(email)
                                            }
                                        },
                                        onNavigateToLogin = { currentAuthScreen = "login" },
                                        isLoading = isLoading,
                                        errorMessage = errorMessage
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
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Screen size değişikliklerini handle et
        when (newConfig.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> {
                // Küçük ekran
            }
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> {
                // Normal ekran
            }
            Configuration.SCREENLAYOUT_SIZE_LARGE -> {
                // Büyük ekran
            }
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> {
                // Çok büyük ekran
            }
        }
    }
}