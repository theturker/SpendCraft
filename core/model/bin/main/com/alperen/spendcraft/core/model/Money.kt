package com.alperen.spendcraft.core.model

@JvmInline
value class Money(val minorUnits: Long) {
    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits)
}




