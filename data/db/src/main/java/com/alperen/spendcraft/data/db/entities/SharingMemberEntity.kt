package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sharing_members")
data class SharingMemberEntity(
    @PrimaryKey
    val id: Long = 0,
    val userId: String,
    val householdId: String,
    val role: String, // OWNER, EDITOR, VIEWER
    val invitedAt: Long,
    val joinedAt: Long? = null,
    val isActive: Boolean = true
)