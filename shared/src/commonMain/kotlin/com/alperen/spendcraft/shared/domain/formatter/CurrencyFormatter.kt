package com.alperen.spendcraft.shared.domain.formatter

/**
 * Platform-agnostic currency formatter
 * 
 * iOS Features (korundu):
 * - Locale-aware formatting (TR vs US)
 * - Grouping separator (TRY için ".", diğerleri için ",")
 * - Decimal separator (TRY için ",", diğerleri için ".")
 * - Sign prefix (+ for income, - for expense)
 * 
 * Android Features (korundu):
 * - Currency mapping
 * - Symbol resolution
 * - Minor units support
 */
object CurrencyFormatter {
    
    private val currencySymbols = mapOf(
        "USD" to "$",
        "TRY" to "₺",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "CNY" to "¥",
        "RUB" to "₽",
        "INR" to "₹",
        "BRL" to "R$",
        "KRW" to "₩"
    )
    
    /**
     * Format amount with currency symbol
     * 
     * @param minorUnits Amount in minor units (cents, kuruş, etc.)
     * @param currencyCode ISO 4217 currency code (TRY, USD, EUR, etc.)
     * @param showSign Show +/- sign for income/expense
     * @param isIncome true for income (+ sign), false for expense (- sign)
     */
    fun format(
        minorUnits: Long,
        currencyCode: String = "TRY",
        showSign: Boolean = false,
        isIncome: Boolean = false
    ): String {
        val amount = minorUnits / 100.0
        val symbol = getCurrencySymbol(currencyCode)
        
        // Sign prefix (iOS pattern)
        val signPrefix = when {
            !showSign -> ""
            isIncome -> "+"
            else -> "-"
        }
        
        // Format based on currency (iOS pattern - locale-aware)
        val formattedNumber = when (currencyCode) {
            "TRY" -> formatTurkishLira(kotlin.math.abs(amount))
            else -> formatInternational(kotlin.math.abs(amount))
        }
        
        return "$signPrefix$formattedNumber $symbol"
    }
    
    /**
     * Format for Turkish Lira with proper separators
     * Grouping: . (nokta)
     * Decimal: , (virgül)
     * Example: 1.234,56 ₺
     */
    private fun formatTurkishLira(amount: Double): String {
        val intPart = amount.toLong()
        val fracPart = ((amount - intPart) * 100).toLong()
        
        // Add thousand separators (.)
        val formattedInt = intPart.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        
        val formattedFrac = fracPart.toString().padStart(2, '0')
        
        return "$formattedInt,$formattedFrac"
    }
    
    /**
     * Format for international currencies (USD, EUR, etc.)
     * Grouping: , (comma)
     * Decimal: . (dot)
     * Example: 1,234.56 $
     */
    private fun formatInternational(amount: Double): String {
        val intPart = amount.toLong()
        val fracPart = ((amount - intPart) * 100).toLong()
        
        // Add thousand separators (,)
        val formattedInt = intPart.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
        
        val formattedFrac = fracPart.toString().padStart(2, '0')
        
        return "$formattedInt.$formattedFrac"
    }
    
    /**
     * Format amount in compact form (K, M, B)
     * Example: 1.5K ₺, 2.3M $
     */
    fun formatCompact(
        minorUnits: Long,
        currencyCode: String = "TRY"
    ): String {
        val amount = minorUnits / 100.0
        val symbol = getCurrencySymbol(currencyCode)
        
        val (value, suffix) = when {
            kotlin.math.abs(amount) >= 1_000_000_000 -> (amount / 1_000_000_000) to "B"
            kotlin.math.abs(amount) >= 1_000_000 -> (amount / 1_000_000) to "M"
            kotlin.math.abs(amount) >= 1_000 -> (amount / 1_000) to "K"
            else -> amount to ""
        }
        
        val formatted = if (suffix.isEmpty()) {
            formatSimpleAmount(value, currencyCode)
        } else {
            val decimal = formatDecimal(value, 1, currencyCode)
            decimal
        }
        
        return "$formatted$suffix $symbol"
    }
    
    /**
     * Simple format without grouping separators
     */
    fun formatSimple(
        amount: Double,
        currencyCode: String = "TRY"
    ): String {
        val symbol = getCurrencySymbol(currencyCode)
        return formatSimpleAmount(amount, currencyCode) + " $symbol"
    }
    
    /**
     * Format amount without currency symbol
     */
    private fun formatSimpleAmount(
        amount: Double,
        currencyCode: String
    ): String {
        val intPart = amount.toLong()
        val fracPart = ((kotlin.math.abs(amount) - kotlin.math.abs(intPart).toDouble()) * 100).toLong()
        
        val decimalSeparator = if (currencyCode == "TRY") "," else "."
        val formattedFrac = fracPart.toString().padStart(2, '0')
        
        return "${kotlin.math.abs(intPart)}$decimalSeparator$formattedFrac"
    }
    
    /**
     * Format decimal number
     */
    private fun formatDecimal(value: Double, decimals: Int, currencyCode: String): String {
        val intPart = value.toLong()
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        val fracPart = ((kotlin.math.abs(value) - kotlin.math.abs(intPart).toDouble()) * multiplier).toLong()
        
        val decimalSeparator = if (currencyCode == "TRY") "," else "."
        val formattedFrac = fracPart.toString().padStart(decimals, '0')
        
        return if (decimals > 0) {
            "${kotlin.math.abs(intPart)}$decimalSeparator$formattedFrac"
        } else {
            kotlin.math.abs(intPart).toString()
        }
    }
    
    /**
     * Get currency symbol for code
     */
    fun getCurrencySymbol(currencyCode: String): String {
        return currencySymbols[currencyCode] ?: "$"
    }
    
    /**
     * Get all supported currencies
     */
    fun getSupportedCurrencies(): Map<String, String> {
        return currencySymbols
    }
    
    /**
     * Check if currency is supported
     */
    fun isSupported(currencyCode: String): Boolean {
        return currencySymbols.containsKey(currencyCode)
    }
}

