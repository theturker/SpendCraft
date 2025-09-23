package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_entry")
data class DailyEntryEntity(
    @PrimaryKey
    val dateEpochDay: Int // Days since epoch (1970-01-01) in UTC
)

