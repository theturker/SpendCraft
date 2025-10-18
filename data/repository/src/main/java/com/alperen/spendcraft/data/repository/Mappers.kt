package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.model.Account
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.Money
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.data.db.entities.AccountEntity
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.entities.TransactionEntity

object TransactionMapper {
    fun fromEntity(e: TransactionEntity): Transaction = Transaction(
        id = e.id,
        amount = Money(e.amountMinor),
        timestampUtcMillis = e.timestampUtcMillis,
        note = e.note,
        categoryId = e.categoryId,
        accountId = e.accountId,
        type = if (e.isIncome) TransactionType.INCOME else TransactionType.EXPENSE
    )

    fun toEntity(m: Transaction): TransactionEntity = TransactionEntity(
        id = m.id ?: 0,
        amountMinor = m.amount.minorUnits,
        timestampUtcMillis = m.timestampUtcMillis,
        note = m.note,
        categoryId = m.categoryId,
        accountId = m.accountId,
        isIncome = m.type == TransactionType.INCOME
    )
}

object CategoryMapper {
    fun fromEntity(e: CategoryEntity): Category {
        val category = Category(
            id = e.id,
            name = e.name,
            color = e.color,
            icon = e.icon,
            isIncome = e.isIncome  // iOS pattern: Categories are type-specific
        )
        android.util.Log.d("CategoryMapper", "🔵 Entity → Category: name=${e.name}, isIncome=${e.isIncome}")
        return category
    }
    
    fun toEntity(c: Category): CategoryEntity {
        val entity = CategoryEntity(
            id = c.id ?: 0,
            name = c.name,
            color = c.color,
            icon = c.icon,
            isIncome = c.isIncome  // iOS pattern: Categories are type-specific
        )
        android.util.Log.d("CategoryMapper", "🔵 Category → Entity: name=${c.name}, isIncome=${c.isIncome}")
        return entity
    }
}

object AccountMapper {
    fun fromEntity(e: AccountEntity): Account = Account(
        id = e.id,
        name = e.name
    )
    
    fun toEntity(a: Account, isDefault: Boolean = false): AccountEntity = AccountEntity(
        id = a.id ?: 0,
        name = a.name,
        type = "CASH",
        currency = "TRY",
        isDefault = isDefault
    )
}




