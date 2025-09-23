package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventName: String,
    val eventData: String, // JSON string olarak saklayacağız
    val timestamp: Long,
    val sessionId: String,
    val userId: String
)
