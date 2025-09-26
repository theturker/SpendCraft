package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_usage",
    indices = [
        Index(value = ["userId"])
    ]
)
data class AIUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local", // For local usage tracking
    val lastUsedEpoch: Long = 0,
    val weeklyQuota: Int = 1, // How many AI requests per week
    val usedThisWeek: Int = 0 // How many used this week
)
