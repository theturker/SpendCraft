package com.alperen.spendcraft.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.analytics.FirebaseAnalyticsService
import com.alperen.spendcraft.analytics.FirebaseCrashlyticsService
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val googleAuthService: GoogleAuthService,
    private val analyticsService: FirebaseAnalyticsService,
    private val crashlyticsService: FirebaseCrashlyticsService
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        _authState.value = if (authService.isUserSignedIn) {
            AuthState.Authenticated(authService.currentUser!!)
        } else {
            AuthState.Unauthenticated
        }
    }
    
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "E-posta ve şifre boş olamaz"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = authService.signInWithEmailAndPassword(email, password)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Authenticated(user)
                        analyticsService.logEvent("user_login_success")
                        analyticsService.setUserId(user.uid)
                        crashlyticsService.setUserId(user.uid)
                    },
                    onFailure = { exception ->
                        _errorMessage.value = getErrorMessage(exception)
                        analyticsService.logEvent("user_login_failed")
                        crashlyticsService.recordException(exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen bir hata oluştu"
                crashlyticsService.recordException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _errorMessage.value = "Tüm alanlar doldurulmalıdır"
            return
        }
        
        if (password != confirmPassword) {
            _errorMessage.value = "Şifreler eşleşmiyor"
            return
        }
        
        if (password.length < 6) {
            _errorMessage.value = "Şifre en az 6 karakter olmalıdır"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = authService.createUserWithEmailAndPassword(email, password)
                result.fold(
                    onSuccess = { user ->
                        // Update user profile with name
                        authService.updateUserProfile(name).fold(
                            onSuccess = {
                                _authState.value = AuthState.Authenticated(user)
                                analyticsService.logEvent("user_register_success")
                                analyticsService.setUserId(user.uid)
                                crashlyticsService.setUserId(user.uid)
                            },
                            onFailure = { exception ->
                                _errorMessage.value = "Profil güncellenirken hata oluştu"
                                crashlyticsService.recordException(exception)
                            }
                        )
                    },
                    onFailure = { exception ->
                        _errorMessage.value = getErrorMessage(exception)
                        analyticsService.logEvent("user_register_failed")
                        crashlyticsService.recordException(exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen bir hata oluştu"
                crashlyticsService.recordException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _authState.value = AuthState.Unauthenticated
                analyticsService.logEvent("user_logout")
            } catch (e: Exception) {
                crashlyticsService.recordException(e)
            }
        }
    }
    
    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "E-posta adresi boş olamaz"
            return
        }
        
        viewModelScope.launch {
            try {
                val result = authService.sendPasswordResetEmail(email)
                result.fold(
                    onSuccess = {
                        analyticsService.logEvent("password_reset_sent")
                    },
                    onFailure = { exception ->
                        _errorMessage.value = getErrorMessage(exception)
                        crashlyticsService.recordException(exception)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen bir hata oluştu"
                crashlyticsService.recordException(e)
            }
        }
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        println("Google Sign-In started with account: ${account.email}")
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                println("Calling googleAuthService.signInWithGoogle...")
                val result = googleAuthService.signInWithGoogle(account)
                result.fold(
                    onSuccess = { user ->
                        println("Google Sign-In successful: ${user.uid}")
                        _authState.value = AuthState.Authenticated(user)
                        analyticsService.logEvent("google_signin_success")
                        analyticsService.setUserId(user.uid)
                        crashlyticsService.setUserId(user.uid)
                    },
                    onFailure = { exception ->
                        println("Google Sign-In failed: ${exception.message}")
                        _errorMessage.value = getErrorMessage(exception)
                        analyticsService.logEvent("google_signin_failed")
                        crashlyticsService.recordException(exception)
                    }
                )
            } catch (e: Exception) {
                println("Google Sign-In exception: ${e.message}")
                _errorMessage.value = "Google ile giriş yapılamadı: ${e.message}"
                crashlyticsService.recordException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    private fun getErrorMessage(exception: Throwable): String {
        return when (exception.message) {
            "The email address is badly formatted." -> "Geçersiz e-posta adresi"
            "The password is invalid or the user does not have a password." -> "Geçersiz şifre"
            "There is no user record corresponding to this identifier." -> "Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı"
            "The email address is already in use by another account." -> "Bu e-posta adresi zaten kullanılıyor"
            "The given password is invalid." -> "Şifre çok zayıf"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Ağ hatası. İnternet bağlantınızı kontrol edin"
            else -> "Giriş yapılamadı. Lütfen tekrar deneyin"
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}
