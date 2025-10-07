package com.alperen.spendcraft.shared.platform

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

actual class AnalyticsImpl(context: Context) : Analytics {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    
    actual override fun logEvent(eventName: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
    
    actual override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }
    
    actual override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}

