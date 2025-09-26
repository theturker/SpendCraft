package com.alperen.spendcraft.feature.premiumdebug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Premium Debug ViewModel
 * 
 * QA/Debug için premium durumunu simüle eder
 */
@HiltViewModel
class PremiumDebugViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val premiumStateDataStore: PremiumStateDataStore
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val isPremium = premiumStateDataStore.isPremium.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = false
    )
    
    fun setPremium(enabled: Boolean) {
        viewModelScope.launch {
            premiumStateDataStore.setPremium(enabled)
        }
    }
    
    fun simulatePurchaseFlow() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Simulate purchase flow delay
                delay(2000)
                
                // Set premium to true
                premiumStateDataStore.setPremium(true)
                
                // Simulate success
                delay(1000)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetPremium() {
        viewModelScope.launch {
            premiumStateDataStore.setPremium(false)
        }
    }
    
    fun refreshBilling() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                billingRepository.refreshPurchases()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
