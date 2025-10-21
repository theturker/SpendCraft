package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.AnalyticsEvent
import com.alperen.spendcraft.shared.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow

class GetAnalyticsDataUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(): Flow<List<AnalyticsEvent>> {
        return analyticsRepository.observeAllEvents()
    }
    
    operator fun invoke(eventName: String): Flow<List<AnalyticsEvent>> {
        return analyticsRepository.observeEventsByType(eventName)
    }
    
    operator fun invoke(startTime: Long, endTime: Long): Flow<List<AnalyticsEvent>> {
        return analyticsRepository.observeEventsByTimeRange(startTime, endTime)
    }
}


