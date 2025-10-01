package com.alperen.spendcraft.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Google Play Billing Manager
 * 
 * Sorumluluklar:
 * - BillingClient kurulum/bağlantı
 * - ProductDetails sorgulama (SUBS & INAPP)
 * - Satın alma akışını başlatma (BillingFlow)
 * - onPurchasesUpdated yakalama
 * - Aktif abonelikleri ve tek seferlik satın alımı doğrulayıp acknowledge etme
 * - (MVP için) local doğrulama: Google Play Developer API kullanılmıyorsa, 
 *   satın alma isAcknowledged + purchaseState==PURCHASED ise Premium'u aç.
 * 
 * TODO: Server-side doğrulama akışı için Google Play Developer API entegrasyonu
 */
interface BillingManager {
    val productDetailsFlow: StateFlow<List<ProductDetails>>
    val purchasesFlow: StateFlow<List<Purchase>>
    val isConnected: StateFlow<Boolean>
    
    suspend fun connect()
    suspend fun queryProducts(productIds: List<String>)
    fun launchPurchase(activity: Activity, productDetails: ProductDetails, offerToken: String?)
    suspend fun refreshPurchases(): Result<List<Purchase>>
    suspend fun acknowledgePurchase(purchase: Purchase): Result<Unit>
    fun setPurchaseResultListener(listener: (BillingResult, List<Purchase>?) -> Unit)
    fun endConnection()
}

@Singleton
class BillingManagerImpl @Inject constructor(
    private val context: Context
) : BillingManager, PurchasesUpdatedListener, BillingClientStateListener {
    
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    private val _productDetailsFlow = MutableStateFlow<List<ProductDetails>>(emptyList())
    override val productDetailsFlow: StateFlow<List<ProductDetails>> = _productDetailsFlow.asStateFlow()
    
    private val _purchasesFlow = MutableStateFlow<List<Purchase>>(emptyList())
    override val purchasesFlow: StateFlow<List<Purchase>> = _purchasesFlow.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private var onPurchaseResult: ((BillingResult, List<Purchase>?) -> Unit)? = null
    private var connectionContinuation: kotlin.coroutines.Continuation<Unit>? = null
    
    override suspend fun connect() {
        suspendCancellableCoroutine { continuation ->
            if (billingClient.isReady) {
                continuation.resume(Unit)
                return@suspendCancellableCoroutine
            }
            
            // Store continuation to resume when connection is ready
            connectionContinuation = continuation
            billingClient.startConnection(this)
            
            continuation.invokeOnCancellation {
                connectionContinuation = null
            }
        }
    }
    
    override suspend fun queryProducts(productIds: List<String>) {
        suspendCancellableCoroutine { continuation ->
            android.util.Log.d("BillingManager", "queryProducts called with: $productIds")
            android.util.Log.d("BillingManager", "isConnected: ${_isConnected.value}")
            
            if (!_isConnected.value) {
                android.util.Log.e("BillingManager", "Billing client not connected, cannot query products")
                continuation.resume(Unit)
                return@suspendCancellableCoroutine
            }
            
            // Ürünleri tipine göre ayır
            val subsProducts = mutableListOf<String>()
            val inappProducts = mutableListOf<String>()
            
            productIds.forEach { productId ->
                if (productId.contains("lifetime")) {
                    inappProducts.add(productId)
                } else {
                    subsProducts.add(productId)
                }
            }
            
            android.util.Log.d("BillingManager", "SUBS products: $subsProducts")
            android.util.Log.d("BillingManager", "INAPP products: $inappProducts")
            
            val allProducts = mutableListOf<ProductDetails>()
            var queriesCompleted = 0
            val totalQueries = (if (subsProducts.isNotEmpty()) 1 else 0) + (if (inappProducts.isNotEmpty()) 1 else 0)
            
            fun checkCompletion() {
                queriesCompleted++
                if (queriesCompleted == totalQueries) {
                    android.util.Log.d("BillingManager", "All queries completed. Total products: ${allProducts.size}")
                    allProducts.forEach { product ->
                        android.util.Log.d("BillingManager", "  - ${product.productId}: ${product.title}")
                    }
                    _productDetailsFlow.value = allProducts
                    continuation.resume(Unit)
                }
            }
            
            // Query SUBS products
            if (subsProducts.isNotEmpty()) {
                val subsList = subsProducts.map { productId ->
                    android.util.Log.d("BillingManager", "Adding SUBS product: $productId")
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
                
                val subsParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(subsList)
                    .build()
                
                android.util.Log.d("BillingManager", "Querying SUBS products...")
                billingClient.queryProductDetailsAsync(subsParams) { billingResult, productDetailsList ->
                    android.util.Log.d("BillingManager", "SUBS query result: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        android.util.Log.d("BillingManager", "SUBS products found: ${productDetailsList?.size ?: 0}")
                        productDetailsList?.let { allProducts.addAll(it) }
                    } else {
                        android.util.Log.e("BillingManager", "SUBS query failed: ${billingResult.debugMessage}")
                    }
                    checkCompletion()
                }
            }
            
            // Query INAPP products
            if (inappProducts.isNotEmpty()) {
                val inappList = inappProducts.map { productId ->
                    android.util.Log.d("BillingManager", "Adding INAPP product: $productId")
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
                
                val inappParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(inappList)
                    .build()
                
                android.util.Log.d("BillingManager", "Querying INAPP products...")
                billingClient.queryProductDetailsAsync(inappParams) { billingResult, productDetailsList ->
                    android.util.Log.d("BillingManager", "INAPP query result: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        android.util.Log.d("BillingManager", "INAPP products found: ${productDetailsList?.size ?: 0}")
                        productDetailsList?.let { allProducts.addAll(it) }
                    } else {
                        android.util.Log.e("BillingManager", "INAPP query failed: ${billingResult.debugMessage}")
                    }
                    checkCompletion()
                }
            }
            
            if (totalQueries == 0) {
                android.util.Log.w("BillingManager", "No products to query")
                continuation.resume(Unit)
            }
            
            continuation.invokeOnCancellation {
                // Cleanup if needed
            }
        }
    }
    
    override fun launchPurchase(
        activity: Activity, 
        productDetails: ProductDetails, 
        offerToken: String?
    ) {
        android.util.Log.d("BillingManager", "launchPurchase called for: ${productDetails.productId}")
        android.util.Log.d("BillingManager", "isConnected: ${_isConnected.value}")
        android.util.Log.d("BillingManager", "offerToken: $offerToken")
        
        if (!_isConnected.value) {
            android.util.Log.e("BillingManager", "Billing client not connected!")
            onPurchaseResult?.invoke(
                BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED).build(),
                null
            )
            return
        }
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply { offerToken?.let { setOfferToken(it) } }
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        android.util.Log.d("BillingManager", "Launching billing flow...")
        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        android.util.Log.d("BillingManager", "Billing flow result: ${result.responseCode} - ${result.debugMessage}")
    }
    
    override suspend fun refreshPurchases(): Result<List<Purchase>> {
        return suspendCancellableCoroutine { continuation ->
            if (!_isConnected.value) {
                continuation.resume(Result.failure(Exception("Billing client not connected")))
                return@suspendCancellableCoroutine
            }
            
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _purchasesFlow.value = purchases ?: emptyList()
                    continuation.resume(Result.success(purchases ?: emptyList()))
                } else {
                    continuation.resume(Result.failure(
                        Exception("Query purchases failed: ${billingResult.debugMessage}")
                    ))
                }
            }
            
            // Also query INAPP purchases
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val currentPurchases = _purchasesFlow.value.toMutableList()
                    currentPurchases.addAll(purchases ?: emptyList())
                    _purchasesFlow.value = currentPurchases
                }
            }
        }
    }
    
    override suspend fun acknowledgePurchase(purchase: Purchase): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            if (purchase.isAcknowledged) {
                continuation.resume(Result.success(Unit))
                return@suspendCancellableCoroutine
            }
            
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(
                        Exception("Acknowledge failed: ${billingResult.debugMessage}")
                    ))
                }
            }
        }
    }
    
    override fun setPurchaseResultListener(listener: (BillingResult, List<Purchase>?) -> Unit) {
        onPurchaseResult = listener
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        onPurchaseResult?.invoke(billingResult, purchases)
    }
    
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        android.util.Log.d("BillingManager", "onBillingSetupFinished: ${billingResult.responseCode} - ${billingResult.debugMessage}")
        
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            android.util.Log.d("BillingManager", "Billing client connected successfully")
            _isConnected.value = true
            connectionContinuation?.resume(Unit)
            connectionContinuation = null
        } else {
            android.util.Log.e("BillingManager", "Billing setup failed: ${billingResult.debugMessage}")
            connectionContinuation?.resumeWithException(
                Exception("Billing setup failed: ${billingResult.debugMessage}")
            )
            connectionContinuation = null
        }
    }
    
    override fun onBillingServiceDisconnected() {
        android.util.Log.d("BillingManager", "Billing service disconnected")
        _isConnected.value = false
    }
    
    override fun endConnection() {
        billingClient.endConnection()
    }
}
