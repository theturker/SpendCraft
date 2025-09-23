package com.alperen.spendcraft.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsService @Inject constructor(
    private val context: Context
) {
    private val analytics: FirebaseAnalytics = Firebase.analytics
    
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        analytics.logEvent(eventName) {
            parameters.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
    
    fun logTransactionAdded(amount: Long, category: String, isIncome: Boolean) {
        logEvent("transaction_added", mapOf(
            "amount" to amount,
            "category" to category,
            "type" to if (isIncome) "income" else "expense"
        ))
    }
    
    fun logTransactionDeleted(amount: Long, category: String) {
        logEvent("transaction_deleted", mapOf(
            "amount" to amount,
            "category" to category
        ))
    }
    
    fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf(
            "screen_name" to screenName
        ))
    }
    
    fun logUserEngagement(action: String, value: String? = null) {
        val params = mutableMapOf<String, Any>("action" to action)
        value?.let { params["value"] = it }
        logEvent("user_engagement", params)
    }
    
    fun setUserProperty(property: String, value: String) {
        analytics.setUserProperty(property, value)
    }
    
    fun setUserId(userId: String) {
        analytics.setUserId(userId)
    }
}
