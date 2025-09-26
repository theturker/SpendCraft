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
    
    override suspend fun connect() {
        suspendCancellableCoroutine { continuation ->
            if (!billingClient.isReady) {
                billingClient.startConnection(this)
            } else {
                continuation.resume(Unit)
                return@suspendCancellableCoroutine
            }
            
            continuation.invokeOnCancellation {
                billingClient.endConnection()
            }
        }
    }
    
    override suspend fun queryProducts(productIds: List<String>) {
        suspendCancellableCoroutine { continuation ->
            if (!_isConnected.value) {
                continuation.resume(Unit)
                return@suspendCancellableCoroutine
            }
            
            val productList = productIds.map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(
                        if (productId.contains("lifetime")) BillingClient.ProductType.INAPP
                        else BillingClient.ProductType.SUBS
                    )
                    .build()
            }
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _productDetailsFlow.value = productDetailsList ?: emptyList()
                    continuation.resume(Unit)
                } else {
                    continuation.resume(Unit)
                }
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
        if (!_isConnected.value) {
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
        
        billingClient.launchBillingFlow(activity, billingFlowParams)
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
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _isConnected.value = true
        }
    }
    
    override fun onBillingServiceDisconnected() {
        _isConnected.value = false
    }
    
    override fun endConnection() {
        billingClient.endConnection()
    }
}
