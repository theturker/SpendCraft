package com.alperen.spendcraft.shared.platform

// iOS için Analytics implementation
// Şimdilik basit console logging yapıyor

class AnalyticsImpl : Analytics {
    
    override fun logEvent(eventName: String, params: Map<String, Any>) {
        // TODO: Firebase iOS SDK entegrasyonu
        println("iOS Analytics: $eventName with params: $params")
    }
    
    override fun setUserId(userId: String) {
        // TODO: Firebase iOS SDK entegrasyonu
        println("iOS Analytics: Set user ID: $userId")
    }
    
    override fun setUserProperty(name: String, value: String) {
        // TODO: Firebase iOS SDK entegrasyonu
        println("iOS Analytics: Set user property: $name = $value")
    }
}

