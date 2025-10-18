package com.alperen.spendcraft.core.ui

import android.content.Context
import android.content.SharedPreferences

/**
 * iOS AdsManager.swift'in birebir Android karşılığı
 * 
 * Singleton pattern ile reklam yönetimi
 * Premium durumunu kontrol eder (şu an her zaman false)
 */
class AdsManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: AdsManager? = null
        
        fun getInstance(context: Context): AdsManager {
            return instance ?: synchronized(this) {
                instance ?: AdsManager(context.applicationContext).also { instance = it }
            }
        }
        
        fun shared(context: Context): AdsManager = getInstance(context)
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("ads_prefs", Context.MODE_PRIVATE)
    }
    
    /**
     * Premium durumu - iOS'taki ile birebir aynı
     * @Published var isPremium: Bool = false
     */
    var isPremium: Boolean
        get() = prefs.getBoolean("isPremium", false)
        set(value) {
            prefs.edit().putBoolean("isPremium", value).apply()
        }
    
    /**
     * Reklam gösterilmeli mi?
     * iOS: func shouldShowAds() -> Bool { return !isPremium }
     */
    fun shouldShowAds(): Boolean {
        return !isPremium
    }
    
    /**
     * Premium durumunu güncelle
     * iOS: func updatePremiumState(_ premium: Bool)
     */
    fun updatePremiumState(premium: Boolean) {
        isPremium = premium
    }
}

/**
 * Compose için isPremium state
 */
@androidx.compose.runtime.Composable
fun rememberIsPremium(context: Context = androidx.compose.ui.platform.LocalContext.current): Boolean {
    val adsManager = androidx.compose.runtime.remember { AdsManager.getInstance(context) }
    return adsManager.isPremium
}


