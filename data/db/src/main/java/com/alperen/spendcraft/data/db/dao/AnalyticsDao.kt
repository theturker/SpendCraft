package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.AnalyticsEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Insert
    suspend fun insert(event: AnalyticsEventEntity): Long

    @Insert
    suspend fun insertAll(events: List<AnalyticsEventEntity>)

    @Query("SELECT * FROM analytics_events ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AnalyticsEventEntity>>

    @Query("SELECT * FROM analytics_events WHERE eventName = :eventName ORDER BY timestamp DESC")
    fun observeByEventName(eventName: String): Flow<List<AnalyticsEventEntity>>

    @Query("SELECT * FROM analytics_events WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun observeByTimeRange(startTime: Long, endTime: Long): Flow<List<AnalyticsEventEntity>>

    @Query("SELECT COUNT(*) FROM analytics_events WHERE eventName = :eventName")
    suspend fun getEventCount(eventName: String): Int

    @Query("SELECT COUNT(*) FROM analytics_events WHERE eventName = :eventName AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getEventCountInRange(eventName: String, startTime: Long, endTime: Long): Int

    @Query("SELECT * FROM analytics_events WHERE timestamp < :cutoffTime")
    suspend fun getOldEvents(cutoffTime: Long): List<AnalyticsEventEntity>

    @Query("DELETE FROM analytics_events WHERE timestamp < :cutoffTime")
    suspend fun deleteOldEvents(cutoffTime: Long)

    @Query("DELETE FROM analytics_events")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT eventName FROM analytics_events")
    suspend fun getAllEventNames(): List<String>

    @Query("SELECT eventName, COUNT(*) as count FROM analytics_events GROUP BY eventName ORDER BY count DESC")
    suspend fun getEventCounts(): List<EventCount>

    data class EventCount(
        val eventName: String,
        val count: Int
    )
}
