package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["isDefault"]),
        Index(value = ["archived"])
    ]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // CASH, CARD, BANK
    val currency: String = "TRY",
    val isDefault: Boolean = false,
    val archived: Boolean = false
)
