package com.alperen.spendcraft.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsLogger(private val analytics: FirebaseAnalytics) {
    fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle()
        params.forEach { (k, v) ->
            bundle.putString(k, v.take(100))
        }
        analytics.logEvent(name, bundle)
    }
}




