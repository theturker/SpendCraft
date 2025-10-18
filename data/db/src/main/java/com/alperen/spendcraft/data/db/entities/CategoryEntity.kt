package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: String,
    val icon: String?,
    val isIncome: Boolean = false  // iOS pattern: Categories are type-specific
)




