package com.alperen.spendcraft.shared.domain.model

import kotlinx.serialization.Serializable

/**
 * Achievement model for shared business logic
 */
@Serializable
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val points: Int = 0
)
