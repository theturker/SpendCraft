package com.alperen.spendcraft.shared.platform

/**
 * Platform-agnostic analytics interface
 */
interface Analytics {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
    fun setUserId(userId: String)
    fun setUserProperty(name: String, value: String)
}

expect class AnalyticsImpl() : Analytics

