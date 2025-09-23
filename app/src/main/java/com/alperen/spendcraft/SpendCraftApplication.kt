package com.alperen.spendcraft

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.alperen.spendcraft.LocaleHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpendCraftApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Firebase initialization
        FirebaseApp.initializeApp(this)
        
        // Initialize Firebase services
        FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance()
        FirebasePerformance.getInstance()
        
        // Set up theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(base, LocaleHelper.getLanguage(base)))
    }
}
