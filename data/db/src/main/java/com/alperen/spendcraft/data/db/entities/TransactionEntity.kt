package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["timestampUtcMillis"]),
        Index(value = ["categoryId"]),
        Index(value = ["accountId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMinor: Long,
    val timestampUtcMillis: Long,
    val note: String?,
    val categoryId: Long?,
    val accountId: Long?,
    val isIncome: Boolean
)




