package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_rules",
    indices = [
        Index(value = ["nextRunEpoch"]),
        Index(value = ["templateTransactionId"])
    ]
)
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateTransactionId: Long, // FK to transactions table
    val frequency: String, // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    val interval: Int = 1, // Every N days/weeks/months/years
    val nextRunEpoch: Long,
    val lastRunEpoch: Long? = null, // Last execution time
    val endEpoch: Long? = null, // Optional end date
    val isActive: Boolean = true
)
