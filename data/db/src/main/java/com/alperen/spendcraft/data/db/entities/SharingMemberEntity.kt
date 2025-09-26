package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sharing_members",
    indices = [
        Index(value = ["householdId"]),
        Index(value = ["userId"])
    ]
)
data class SharingMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val householdId: String, // Unique household identifier
    val userId: String, // User email or ID
    val name: String, // Display name
    val email: String?, // Email address
    val role: String, // OWNER, EDITOR, VIEWER
    val invitedAt: Long, // Timestamp when invited
    val joinedAt: Long? = null // Timestamp when joined (null if pending)
)
