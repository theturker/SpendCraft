package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.AnalyticsEvent
import com.alperen.spendcraft.domain.repo.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnalyticsDataUseCase @Inject constructor(
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
