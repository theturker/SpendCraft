package com.alperen.spendcraft.core.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    private val context: Context,
    private val premiumStateDataStore: com.alperen.spendcraft.core.premium.PremiumStateDataStore
) {
    
    private val _isPremium = MutableStateFlow(false)
    val isPremium: Flow<Boolean> = _isPremium.asStateFlow()
    
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    
    init {
        MobileAds.initialize(context)
        // Listen to premium state changes
        // premiumStateDataStore.isPremium.collect { _isPremium.value = it }
    }
    
    fun loadBannerAd(adView: AdView) {
        if (_isPremium.value) {
            // Hide ad for premium users
            adView.visibility = android.view.View.GONE
            return
        }
        
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    
    fun loadInterstitialAd(adUnitId: String) {
        if (_isPremium.value) return // Don't load ads for premium users
        
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }
    
    fun showInterstitialAd(onAdClosed: () -> Unit = {}) {
        if (_isPremium.value) {
            onAdClosed()
            return
        }
        
        interstitialAd?.let { ad ->
            ad.show(context as android.app.Activity) {
                onAdClosed()
            }
            interstitialAd = null
        } ?: run {
            onAdClosed()
        }
    }
    
    fun loadRewardedAd(adUnitId: String) {
        if (_isPremium.value) return // Don't load ads for premium users
        
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }
    
    fun showRewardedAd(
        onRewarded: () -> Unit = {},
        onAdClosed: () -> Unit = {}
    ) {
        if (_isPremium.value) {
            onRewarded()
            onAdClosed()
            return
        }
        
        rewardedAd?.let { ad ->
            ad.show(context as android.app.Activity) { rewardItem ->
                onRewarded()
            }
            rewardedAd = null
        } ?: run {
            onAdClosed()
        }
    }
    
    fun shouldShowAds(): Boolean {
        return !_isPremium.value
    }
}
