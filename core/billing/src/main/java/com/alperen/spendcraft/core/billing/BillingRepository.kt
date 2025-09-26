package com.alperen.spendcraft.core.billing

import android.app.Activity
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Billing Repository
 * 
 * BillingManager + PremiumStateDataStore'u kullanır.
 * Mantık:
 * - Satın alma başarı olduğunda PremiumStateDataStore.setPremium(true)
 * - Refund / iptal tespitinde setPremium(false) (MVP'de refreshPurchases() ile düzenli kontrol)
 */
@Singleton
class BillingRepository @Inject constructor(
    private val billingManager: BillingManager,
    private val premiumStateDataStore: PremiumStateDataStore
) {
    
    val isPremium: Flow<Boolean> = premiumStateDataStore.isPremium
    
    val products: Flow<List<ProductDetails>> = billingManager.productDetailsFlow
    
    private val productIds = listOf(
        "premium_monthly",
        "premium_yearly", 
        "ai_weekly"
    )
    
    suspend fun initialize(): Result<Unit> {
        return try {
            // Connect to billing
            billingManager.connect()
            
            // Query products
            billingManager.queryProducts(productIds)
            
            // Refresh purchases to check current state
            refreshPurchases()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun buyMonthly(activity: Activity): Result<Unit> {
        return performPurchase(activity, "premium_monthly")
    }
    
    suspend fun buyYearly(activity: Activity): Result<Unit> {
        return performPurchase(activity, "premium_yearly")
    }
    
    suspend fun buyAIWeekly(activity: Activity): Result<Unit> {
        return performPurchase(activity, "ai_weekly")
    }
    
    private suspend fun performPurchase(activity: Activity, productId: String): Result<Unit> {
        return try {
            val products = billingManager.productDetailsFlow.first()
            val product = products.find { it.productId == productId }
                ?: return Result.failure(Exception("Product not found: $productId"))
            
            // Set up purchase result listener
            var purchaseResult: Result<Unit>? = null
            
            billingManager.setPurchaseResultListener { billingResult, purchases ->
                purchaseResult = if (billingResult.responseCode == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                    // Handle successful purchase
                    purchases?.let { 
                        // Note: In real implementation, this should be handled in a coroutine
                        // For now, we'll just return success and handle premium status elsewhere
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(BillingResultMapper.mapErrorCode(billingResult.responseCode)))
                }
            }
            
            // Launch purchase flow
            billingManager.launchPurchase(activity, product, null)
            
            // Wait for result (in real implementation, this would be handled via callbacks)
            // For now, we'll return success and let the callback handle the actual result
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun handleSuccessfulPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                // Acknowledge the purchase
                billingManager.acknowledgePurchase(purchase).getOrThrow()
                
                // Set premium status
                premiumStateDataStore.setPremium(true)
            }
        }
    }
    
    suspend fun refreshPurchases(): Result<Unit> {
        return try {
            val purchases = billingManager.refreshPurchases().getOrThrow()
            
            // Check if user has any active premium purchases
            val hasActivePremium = purchases.any { purchase ->
                when (purchase.products.firstOrNull()) {
                    "premium_monthly", "premium_yearly", "ai_weekly" -> {
                        // For subscriptions, check if they're active
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    else -> false
                }
            }
            
            // Update premium status
            premiumStateDataStore.setPremium(hasActivePremium)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductDetails(productId: String): ProductDetails? {
        return billingManager.productDetailsFlow.first().find { it.productId == productId }
    }
    
    fun getCurrentPurchases(): Flow<List<Purchase>> = billingManager.purchasesFlow
}
