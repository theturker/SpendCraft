package com.alperen.spendcraft.domain.repo

import com.alperen.spendcraft.core.model.AnalyticsEvent
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    suspend fun trackEvent(event: AnalyticsEvent)
    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap())
    
    fun observeAllEvents(): Flow<List<AnalyticsEvent>>
    fun observeEventsByType(eventName: String): Flow<List<AnalyticsEvent>>
    fun observeEventsByTimeRange(startTime: Long, endTime: Long): Flow<List<AnalyticsEvent>>
    
    suspend fun getEventCount(eventName: String): Int
    suspend fun getEventCountInRange(eventName: String, startTime: Long, endTime: Long): Int
    suspend fun getAllEventNames(): List<String>
    suspend fun getEventCounts(): List<Pair<String, Int>>
    
    suspend fun cleanupOldEvents(keepDays: Int = 30)
    suspend fun deleteAllEvents()
}
