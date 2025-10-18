package com.alperen.spendcraft.core.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AdMob Interstitial Ad Manager for Android
 * iOS equivalent: iosApp/SpendCraftiOS/AdsManager.swift
 * 
 * Interstitial reklamları yükler ve gösterir.
 * Premium kullanıcılar için reklam gösterilmez.
 */
class AdMobInterstitialManager private constructor() {
    
    companion object {
        @Volatile
        private var instance: AdMobInterstitialManager? = null
        
        fun getInstance(): AdMobInterstitialManager {
            return instance ?: synchronized(this) {
                instance ?: AdMobInterstitialManager().also { instance = it }
            }
        }
        
        private const val TAG = "AdMobInterstitial"
        
        // Ad Unit IDs - iOS AdUnitIDs ile aynı yapı
        object AdUnitIDs {
            // Test IDs - Google'ın test ID'leri
            const val INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"
            
            // Production ID - AdMob console'dan alınacak
            // iOS'taki gibi DEBUG modda test ID kullan
            fun getInterstitialAdUnitId(): String {
                // iOS pattern: #if DEBUG ... #else ... #endif
                // Android: Her zaman test ID kullan (Production ID eklendiğinde buraya eklenecek)
                return INTERSTITIAL_TEST
            }
        }
    }
    
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    
    /**
     * AdMob'u başlat ve ilk interstitial reklamı yükle
     * iOS: initializeAds() ile aynı
     */
    fun initialize(context: Context, isPremium: Boolean = false) {
        if (isPremium) {
            Log.d(TAG, "User is premium, skipping ad initialization")
            return
        }
        
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
            // İlk reklamı yükle
            loadInterstitialAd(context)
        }
    }
    
    /**
     * Interstitial reklam yükle
     * iOS: loadInterstitialAd() ile aynı
     */
    fun loadInterstitialAd(context: Context, isPremium: Boolean = false) {
        if (isPremium) {
            Log.w(TAG, "⚠️ Interstitial ad not loaded - User is premium")
            return
        }
        
        if (isLoading) {
            Log.d(TAG, "⚠️ Already loading an interstitial ad")
            return
        }
        
        if (interstitialAd != null) {
            Log.d(TAG, "⚠️ Interstitial ad already loaded")
            return
        }
        
        isLoading = true
        val adUnitId = AdUnitIDs.getInterstitialAdUnitId()
        Log.d(TAG, "🔄 Loading interstitial ad with ID: $adUnitId")
        
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "✅ Interstitial ad loaded successfully!")
                    interstitialAd = ad
                    isLoading = false
                    
                    // Set fullscreen content callback - iOS'taki delegate ile aynı
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad dismissed")
                            interstitialAd = null
                            // Reklam kapandıktan sonra yeni reklam yükle - iOS'taki gibi
                            loadInterstitialAd(context, isPremium)
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "❌ Ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Ad showed fullscreen content")
                            interstitialAd = null
                        }
                        
                        override fun onAdImpression() {
                            Log.d(TAG, "📊 Ad recorded impression")
                        }
                        
                        override fun onAdClicked() {
                            Log.d(TAG, "Ad recorded a click")
                        }
                    }
                }
                
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "❌ Failed to load interstitial ad: ${adError.message}")
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }
    
    /**
     * Interstitial reklamı göster
     * iOS: showInterstitialAd(from:onAdClosed:) ile aynı
     * 
     * @param activity Activity to show the ad
     * @param isPremium Premium kullanıcı mı?
     * @param onAdClosed Reklam kapandıktan sonra çalışacak callback
     * @param maxRetries Maksimum deneme sayısı (iOS'taki gibi 3)
     * @param retryCount Mevcut deneme sayısı
     */
    fun showInterstitialAd(
        activity: Activity?,
        isPremium: Boolean = false,
        onAdClosed: () -> Unit = {},
        maxRetries: Int = 3,
        retryCount: Int = 0
    ) {
        if (isPremium) {
            Log.w(TAG, "⚠️ Interstitial ad not shown - User is premium")
            onAdClosed()
            return
        }
        
        if (interstitialAd == null) {
            Log.w(TAG, "⚠️ Interstitial ad not ready - Loading new ad...")
            onAdClosed()
            if (activity != null) {
                loadInterstitialAd(activity, isPremium)
            }
            return
        }
        
        if (activity == null) {
            Log.e(TAG, "❌ No activity to present interstitial ad")
            onAdClosed()
            return
        }
        
        // iOS'taki gibi activity'nin meşgul olup olmadığını kontrol et
        if (activity.isFinishing || activity.isDestroyed) {
            if (retryCount < maxRetries) {
                Log.w(TAG, "⚠️ Activity is finishing/destroyed (attempt ${retryCount + 1}/$maxRetries)")
                Log.d(TAG, "🔄 Retrying in 2 seconds...")
                
                // 2 saniye sonra tekrar dene - iOS'taki gibi
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    showInterstitialAd(activity, isPremium, onAdClosed, maxRetries, retryCount + 1)
                }
                return
            } else {
                Log.e(TAG, "❌ Max retries reached. Activity is still unavailable. Skipping interstitial ad.")
                onAdClosed()
                return
            }
        }
        
        Log.d(TAG, "🎬 Presenting interstitial ad...")
        interstitialAd?.show(activity)
        onAdClosed()
    }
    
    /**
     * Reklamın hazır olup olmadığını kontrol et
     */
    fun isAdReady(): Boolean {
        return interstitialAd != null
    }
    
    /**
     * Reklamı temizle
     */
    fun destroy() {
        interstitialAd = null
    }
}

/**
 * Composable function to show interstitial ad
 * iOS'taki AISuggestionsView.showInterstitialAd() ile aynı mantık
 * 
 * @param isPremium Premium kullanıcı mı?
 * @param delayMillis Ekran açıldıktan kaç milisaniye sonra reklam gösterilecek (iOS: 5000ms)
 * @param onAdClosed Reklam kapandıktan sonra çalışacak callback
 */
@Composable
fun rememberInterstitialAdLoader(
    isPremium: Boolean = false,
    delayMillis: Long = 5000L,
    onAdClosed: () -> Unit = {}
): AdMobInterstitialManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = remember { AdMobInterstitialManager.getInstance() }
    
    // Initialize on first composition
    LaunchedEffect(isPremium) {
        if (!isPremium) {
            manager.initialize(context, isPremium)
        }
    }
    
    return manager
}

/**
 * Show interstitial ad with delay
 * iOS'taki pattern: 
 * ```swift
 * DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
 *     showInterstitialAd()
 * }
 * ```
 */
@Composable
fun ShowInterstitialAdWithDelay(
    isPremium: Boolean = false,
    delayMillis: Long = 5000L,
    onAdClosed: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = rememberInterstitialAdLoader(isPremium, delayMillis, onAdClosed)
    
    LaunchedEffect(Unit) {
        if (!isPremium) {
            delay(delayMillis)
            // Get activity from context
            val activity = context as? Activity
            manager.showInterstitialAd(activity, isPremium, onAdClosed)
        }
    }
}


