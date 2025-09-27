package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: Long = 0,
    val name: String,
    val description: String,
    val icon: String,
    val points: Int,
    val category: AchievementCategory,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

enum class AchievementCategory {
    SPENDING,
    SAVING,
    BUDGET,
    STREAK,
    CATEGORY,
    SPECIAL
}
