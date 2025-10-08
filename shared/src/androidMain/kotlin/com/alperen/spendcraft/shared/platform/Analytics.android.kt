package com.alperen.spendcraft.shared.platform

import android.content.Context
import android.util.Log

class AnalyticsImpl(context: Context) : Analytics {
    private val TAG = "Analytics"
    
    override fun logEvent(eventName: String, params: Map<String, Any>) {
        // TODO: Firebase Analytics entegrasyonu
        // Şimdilik console logging yapıyor
        Log.d(TAG, "Event: $eventName with params: $params")
    }
    
    override fun setUserId(userId: String) {
        // TODO: Firebase Analytics entegrasyonu
        Log.d(TAG, "Set user ID: $userId")
    }
    
    override fun setUserProperty(name: String, value: String) {
        // TODO: Firebase Analytics entegrasyonu
        Log.d(TAG, "Set user property: $name = $value")
    }
}

