package com.alperen.spendcraft.core.billing

import android.app.Activity
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val premiumStateDataStore: PremiumStateDataStore,
    private val productValidator: BillingProductValidator
) {
    
    val isPremium: Flow<Boolean> = premiumStateDataStore.isPremium
    
    val products: Flow<List<ProductDetails>> = billingManager.productDetailsFlow
    
    private val productIds = listOf(
        "premium_monthly",
        "premium_yearly",
        "premium_lifetime",
        "ai_weekly"
    )
    
    suspend fun initialize(): Result<Unit> {
        return try {
            android.util.Log.d("BillingRepository", "Initializing billing...")
            
            // Connect to billing
            android.util.Log.d("BillingRepository", "Connecting to billing service...")
            billingManager.connect()
            android.util.Log.d("BillingRepository", "Connected to billing service")
            
            // Query products
            android.util.Log.d("BillingRepository", "Querying products: $productIds")
            billingManager.queryProducts(productIds)
            android.util.Log.d("BillingRepository", "Products queried")
            
            // Validate products
            val products = billingManager.productDetailsFlow.first()
            android.util.Log.d("BillingRepository", "Received ${products.size} products from flow")
            
            val validationResult = productValidator.validateProducts(products)
            
            if (!validationResult.isValid) {
                android.util.Log.w("BillingRepository", "Product validation failed: $validationResult")
            } else {
                android.util.Log.d("BillingRepository", "Product validation successful")
            }
            
            // Log product details for debugging
            productValidator.logProductDetails(products)
            
            // Refresh purchases to check current state
            android.util.Log.d("BillingRepository", "Refreshing purchases...")
            refreshPurchases()
            
            android.util.Log.d("BillingRepository", "Billing initialization complete")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BillingRepository", "Billing initialization failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun buyMonthly(activity: Activity): Result<Unit> {
        android.util.Log.d("BillingRepository", "buyMonthly called")
        return performPurchase(activity, "premium_monthly")
    }
    
    suspend fun buyYearly(activity: Activity): Result<Unit> {
        android.util.Log.d("BillingRepository", "buyYearly called")
        return performPurchase(activity, "premium_yearly")
    }
    
    suspend fun buyLifetime(activity: Activity): Result<Unit> {
        android.util.Log.d("BillingRepository", "buyLifetime called")
        return performPurchase(activity, "premium_lifetime")
    }
    
    suspend fun buyAIWeekly(activity: Activity): Result<Unit> {
        android.util.Log.d("BillingRepository", "buyAIWeekly called")
        return performPurchase(activity, "ai_weekly")
    }
    
    suspend fun hasInappProduct(productId: String): Boolean {
        return billingManager.purchasesFlow.first().any { purchase ->
            purchase.products.contains(productId) && 
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
    }
    
    private suspend fun performPurchase(activity: Activity, productId: String): Result<Unit> {
        return try {
            android.util.Log.d("BillingRepository", "performPurchase called for: $productId")
            
            val products = billingManager.productDetailsFlow.first()
            android.util.Log.d("BillingRepository", "Available products: ${products.map { it.productId }}")
            
            val product = products.find { it.productId == productId }
            if (product == null) {
                android.util.Log.e("BillingRepository", "Product not found: $productId")
                return Result.failure(Exception("Ürün bulunamadı: $productId. Lütfen internet bağlantınızı kontrol edin."))
            }
            
            android.util.Log.d("BillingRepository", "Product found: ${product.productId}")
            
            // Get offer token for subscriptions
            val offerToken = if (productId.contains("monthly") || productId.contains("yearly") || productId.contains("weekly")) {
                product.subscriptionOfferDetails?.firstOrNull()?.offerToken
            } else {
                null
            }
            
            android.util.Log.d("BillingRepository", "Offer token: $offerToken")
            
            // Launch purchase flow
            billingManager.launchPurchase(activity, product, offerToken)
            
            android.util.Log.d("BillingRepository", "Purchase flow launched successfully")
            
            // Purchase flow started successfully
            // Result will be handled by onPurchasesUpdated callback
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BillingRepository", "Purchase error", e)
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
                    "premium_monthly", "premium_yearly", "premium_lifetime" -> {
                        // For subscriptions and lifetime, check if they're active
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

    suspend fun hasAIWeeklyEntitlement(): Boolean {
        return try {
            val purchases = billingManager.refreshPurchases().getOrThrow()
            purchases.any { purchase ->
                purchase.products.contains("ai_weekly") &&
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getProductDetails(productId: String): ProductDetails? {
        return billingManager.productDetailsFlow.first().find { it.productId == productId }
    }
    
    fun getCurrentPurchases(): Flow<List<Purchase>> = billingManager.purchasesFlow

    // Reactive AI weekly entitlement, so UI modules don't depend on Billing types
    val aiWeekly: Flow<Boolean> = getCurrentPurchases().map { purchases ->
        purchases.any { p ->
            p.products.contains("ai_weekly") &&
            p.purchaseState == Purchase.PurchaseState.PURCHASED
        }
    }
}
