package com.alperen.spendcraft.core.model

data class Category(
    val id: Long?,
    val name: String,
    val icon: String? = null
)

data class Account(
    val id: Long?,
    val name: String
)

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Long?,
    val amount: Money,
    val timestampUtcMillis: Long,
    val note: String?,
    val categoryId: Long?,
    val type: TransactionType
)




