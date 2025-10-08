package com.alperen.spendcraft.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Money(val minorUnits: Long) {
    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits)
    
    companion object {
        val ZERO = Money(0)
    }
}

