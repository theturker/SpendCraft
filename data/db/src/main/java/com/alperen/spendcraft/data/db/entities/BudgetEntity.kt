package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetEntity(
    @PrimaryKey
    val categoryId: String, // Category name as primary key
    val monthlyLimitMinor: Long // Budget limit in minor currency units
)

