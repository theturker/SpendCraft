package com.alperen.spendcraft.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long?,
    val name: String,
    val color: String,
    val icon: String? = null
)

@Serializable
data class Account(
    val id: Long?,
    val name: String
)

@Serializable
data class Streak(
    val current: Int,
    val best: Int
)

@Serializable
data class Budget(
    val categoryId: String,
    val monthlyLimitMinor: Long
)

@Serializable
enum class TransactionType { INCOME, EXPENSE }

@Serializable
data class Transaction(
    val id: Long?,
    val amount: Money,
    val timestampUtcMillis: Long,
    val note: String?,
    val categoryId: Long?,
    val accountId: Long?,
    val type: TransactionType
)

