package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Money
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.entities.TransactionEntity

object TransactionMapper {
    fun fromEntity(e: TransactionEntity): Transaction = Transaction(
        id = e.id,
        amount = Money(e.amountMinor),
        timestampUtcMillis = e.timestampUtcMillis,
        note = e.note,
        categoryId = e.categoryId,
        type = if (e.isIncome) TransactionType.INCOME else TransactionType.EXPENSE
    )

    fun toEntity(m: Transaction): TransactionEntity = TransactionEntity(
        id = m.id ?: 0,
        amountMinor = m.amount.minorUnits,
        timestampUtcMillis = m.timestampUtcMillis,
        note = m.note,
        categoryId = m.categoryId,
        isIncome = m.type == TransactionType.INCOME
    )
}

object CategoryMapper {
    fun fromEntity(e: CategoryEntity): Category = Category(
        id = e.id,
        name = e.name,
        icon = e.icon
    )
}




