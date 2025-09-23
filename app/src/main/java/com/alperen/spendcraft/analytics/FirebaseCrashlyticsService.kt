package com.alperen.spendcraft.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashlyticsService @Inject constructor() {
    private val crashlytics = FirebaseCrashlytics.getInstance()
    
    fun log(message: String) {
        crashlytics.log(message)
    }
    
    fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
    
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
    
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Long) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Float) {
        crashlytics.setCustomKey(key, value)
    }
    
    fun setCustomKey(key: String, value: Double) {
        crashlytics.setCustomKey(key, value)
    }
}
