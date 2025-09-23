package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.AnalyticsEvent
import com.alperen.spendcraft.domain.repo.AnalyticsRepository
import javax.inject.Inject

class TrackEventUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(event: AnalyticsEvent) {
        analyticsRepository.trackEvent(event)
    }
    
    suspend operator fun invoke(eventName: String, eventData: Map<String, String> = emptyMap()) {
        analyticsRepository.trackEvent(eventName, eventData)
    }
}
