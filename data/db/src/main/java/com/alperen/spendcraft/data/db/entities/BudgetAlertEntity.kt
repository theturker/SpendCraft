package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity

@Entity(
    tableName = "budget_alert",
    primaryKeys = ["categoryId", "level", "monthKey"]
)
data class BudgetAlertEntity(
    val categoryId: String,
    val level: Int, // 80 or 100 (percentage)
    val monthKey: String // Format: "YYYY-MM" in UTC
)

