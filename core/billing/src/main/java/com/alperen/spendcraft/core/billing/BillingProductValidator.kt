package com.alperen.spendcraft.core.billing

import android.util.Log
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingProductValidator @Inject constructor() {
    
    companion object {
        private const val TAG = "BillingValidator"
        
        // Expected product IDs from Play Console
        private const val PREMIUM_MONTHLY = "premium_monthly"
        private const val PREMIUM_YEARLY = "premium_yearly"
        private const val PREMIUM_LIFETIME = "premium_lifetime"
        private const val AI_WEEKLY = "ai_weekly"
    }
    
    fun validateProducts(products: List<ProductDetails>): BillingValidationResult {
        val foundProducts = products.map { it.productId }.toSet()
        val expectedProducts = setOf(PREMIUM_MONTHLY, PREMIUM_YEARLY, PREMIUM_LIFETIME, AI_WEEKLY)
        
        val missingProducts = expectedProducts - foundProducts
        val extraProducts = foundProducts - expectedProducts
        
        return BillingValidationResult(
            isValid = missingProducts.isEmpty(),
            missingProducts = missingProducts,
            extraProducts = extraProducts,
            foundProducts = foundProducts
        )
    }
    
    fun logProductDetails(products: List<ProductDetails>) {
        Log.d(TAG, "=== Billing Product Details ===")
        products.forEach { product ->
            Log.d(TAG, "Product ID: ${product.productId}")
            Log.d(TAG, "Title: ${product.title}")
            Log.d(TAG, "Description: ${product.description}")
            Log.d(TAG, "Product Type: ${product.productType}")
            
            // For subscriptions, log base plans
            if (product.productType == com.android.billingclient.api.BillingClient.ProductType.SUBS) {
                product.subscriptionOfferDetails?.forEach { offer ->
                    Log.d(TAG, "  Base Plan: ${offer.basePlanId}")
                    Log.d(TAG, "  Offer ID: ${offer.offerId}")
                    offer.pricingPhases.pricingPhaseList.forEach { phase ->
                        Log.d(TAG, "    Price: ${phase.priceAmountMicros / 1_000_000} ${phase.priceCurrencyCode}")
                        Log.d(TAG, "    Billing Period: ${phase.billingPeriod}")
                    }
                }
            }
            
            // For in-app products, log price (ai_weekly, premium_lifetime vs.)
            if (product.productType == com.android.billingclient.api.BillingClient.ProductType.INAPP) {
                product.oneTimePurchaseOfferDetails?.let { offer ->
                    Log.d(TAG, "  Price: ${offer.priceAmountMicros / 1_000_000} ${offer.priceCurrencyCode}")
                }
            }
        }
        Log.d(TAG, "=== End Product Details ===")
    }
}

data class BillingValidationResult(
    val isValid: Boolean,
    val missingProducts: Set<String>,
    val extraProducts: Set<String>,
    val foundProducts: Set<String>
)
