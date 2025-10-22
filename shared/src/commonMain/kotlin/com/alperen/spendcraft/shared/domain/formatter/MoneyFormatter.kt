package com.alperen.spendcraft.shared.domain.formatter

import com.alperen.spendcraft.shared.domain.model.Money

/**
 * Money-specific formatter
 * Combines Android and iOS best practices
 */
object MoneyFormatter {
    
    /**
     * Format Money object with currency
     * Uses CurrencyFormatter internally
     */
    fun format(
        money: Money,
        currencyCode: String = "TRY",
        showSign: Boolean = false,
        isIncome: Boolean = false
    ): String {
        return CurrencyFormatter.format(
            minorUnits = money.minorUnits,
            currencyCode = currencyCode,
            showSign = showSign,
            isIncome = isIncome
        )
    }
    
    /**
     * Format in compact form (K, M, B)
     * Example: 1.5K ₺, 2.3M $
     */
    fun formatCompact(
        money: Money,
        currencyCode: String = "TRY"
    ): String {
        return CurrencyFormatter.formatCompact(
            minorUnits = money.minorUnits,
            currencyCode = currencyCode
        )
    }
    
    /**
     * Format without currency symbol (just amount)
     */
    fun formatAmountOnly(
        money: Money,
        currencyCode: String = "TRY"
    ): String {
        val amount = money.minorUnits / 100.0
        
        return when (currencyCode) {
            "TRY" -> formatTurkish(amount)
            else -> formatInternational(amount)
        }
    }
    
    /**
     * Turkish format: 1.234,56
     */
    private fun formatTurkish(amount: Double): String {
        val intPart = amount.toLong()
        val fracPart = ((kotlin.math.abs(amount) - kotlin.math.abs(intPart).toDouble()) * 100).toLong()
        
        val formattedInt = kotlin.math.abs(intPart).toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        
        val formattedFrac = fracPart.toString().padStart(2, '0')
        val sign = if (amount < 0) "-" else ""
        
        return "$sign$formattedInt,$formattedFrac"
    }
    
    /**
     * International format: 1,234.56
     */
    private fun formatInternational(amount: Double): String {
        val intPart = amount.toLong()
        val fracPart = ((kotlin.math.abs(amount) - kotlin.math.abs(intPart).toDouble()) * 100).toLong()
        
        val formattedInt = kotlin.math.abs(intPart).toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
        
        val formattedFrac = fracPart.toString().padStart(2, '0')
        val sign = if (amount < 0) "-" else ""
        
        return "$sign$formattedInt.$formattedFrac"
    }
    
    /**
     * Parse formatted string back to Money
     * Supports both Turkish and international formats
     */
    fun parse(formattedAmount: String, currencyCode: String = "TRY"): Money? {
        try {
            // Remove currency symbols and signs
            val cleaned = formattedAmount
                .replace(Regex("[₺$€£¥₽₹+\\s]"), "")
                .trim()
            
            // Handle Turkish format (1.234,56) or International (1,234.56)
            val normalized = when (currencyCode) {
                "TRY" -> cleaned.replace(".", "").replace(",", ".")
                else -> cleaned.replace(",", "")
            }
            
            val amount = normalized.toDoubleOrNull() ?: return null
            val minorUnits = (amount * 100).toLong()
            
            return Money(minorUnits)
        } catch (e: Exception) {
            return null
        }
    }
}




