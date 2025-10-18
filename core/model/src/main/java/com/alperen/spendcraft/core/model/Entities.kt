package com.alperen.spendcraft.core.model

data class Category(
    val id: Long?,
    val name: String,
    val color: String,
    val icon: String? = null,
    val isIncome: Boolean = false  // iOS pattern: Categories are type-specific
)

data class Account(
    val id: Long?,
    val name: String
)

data class Streak(
    val current: Int,
    val best: Int
)

data class Budget(
    val categoryId: String,
    val monthlyLimitMinor: Long
)

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Long?,
    val amount: Money,
    val timestampUtcMillis: Long,
    val note: String?,
    val categoryId: Long?,
    val accountId: Long?,
    val type: TransactionType
)




