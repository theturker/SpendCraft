package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.AnalyticsEvent
import com.alperen.spendcraft.shared.domain.repository.AnalyticsRepository

class TrackEventUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(event: AnalyticsEvent) {
        analyticsRepository.trackEvent(event)
    }
    
    suspend operator fun invoke(eventName: String, eventData: Map<String, String> = emptyMap()) {
        analyticsRepository.trackEvent(eventName, eventData)
    }
}


