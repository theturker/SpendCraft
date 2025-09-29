package com.alperen.spendcraft.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Long,
    val categoryId: Long,
    val accountId: Long,
    val type: com.alperen.spendcraft.core.model.TransactionType,
    val frequency: RecurringFrequency,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val lastExecuted: Long? = null,
    val nextExecution: Long,
    val note: String? = null
)

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
