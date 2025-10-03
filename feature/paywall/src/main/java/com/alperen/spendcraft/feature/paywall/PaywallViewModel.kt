package com.alperen.spendcraft.feature.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.core.billing.BillingResultMapper
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Paywall ViewModel
 * 
 * BillingRepository'yi kullanır, products akışını toplar,
 * kullanıcı seçimlerine göre BillingRepository çağırır.
 */
@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val premiumStateDataStore: PremiumStateDataStore
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    val products = billingRepository.products.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = emptyList()
    )
    
    val isPremium = billingRepository.isPremium.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = false
    )
    
    init {
        // Initialize billing when ViewModel is created
        viewModelScope.launch {
            try {
                billingRepository.initialize()
            } catch (e: Exception) {
                _errorMessage.value = "Billing başlatılamadı: ${e.message}"
            }
        }
    }
    
    fun buyMonthly(activity: Activity) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = billingRepository.buyMonthly(activity)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Aylık abonelik başlatıldı!"
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Satın alma başarısız"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen hata: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun buyYearly(activity: Activity) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = billingRepository.buyYearly(activity)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Yıllık abonelik başlatıldı!"
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Satın alma başarısız"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen hata: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun buyLifetime(activity: Activity) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = billingRepository.buyLifetime(activity)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Yaşam boyu premium aktif!"
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Satın alma başarısız"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen hata: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun buyAIWeekly(activity: Activity) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = billingRepository.buyAIWeekly(activity)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "AI Haftalık Raporlar başlatıldı!"
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Satın alma başarısız"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Beklenmeyen hata: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSuccess() {
        _successMessage.value = null
    }
    
    fun getProductPrice(productId: String): String {
        return when (productId) {
            "premium_monthly" -> "TRY 35,99/ay"
            "premium_yearly" -> "TRY 99,00/yıl"
            "premium_lifetime" -> "TRY 169,00"
            "ai_weekly" -> "TRY 10,99/hafta"
            else -> "Fiyat yükleniyor..."
        }
    }
    
    fun getProductTitle(productId: String): String {
        return when (productId) {
            "premium_monthly" -> "Aylık Premium"
            "premium_yearly" -> "Yıllık Premium"
            "premium_lifetime" -> "Yaşam Boyu Premium"
            "ai_weekly" -> "AI Haftalık Raporlar"
            else -> "Premium"
        }
    }
    
    fun getProductDescription(productId: String): String {
        return when (productId) {
            "premium_monthly" -> "Tüm premium özellikler"
            "premium_yearly" -> "En iyi değer - %50 tasarruf"
            "premium_lifetime" -> "Tek seferlik ödeme – ömür boyu premium"
            "ai_weekly" -> "Haftalık AI analiz raporları"
            else -> "Premium özellikler"
        }
    }
}
