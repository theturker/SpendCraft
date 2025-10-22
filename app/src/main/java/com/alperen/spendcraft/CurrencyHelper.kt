package com.alperen.spendcraft

import android.content.Context
import com.alperen.spendcraft.shared.domain.formatter.CurrencyFormatter as SharedCurrencyFormatter

/**
 * Android CurrencyHelper - Now uses shared KMP formatter
 * 
 * This is a thin wrapper that provides Android-specific Context-based API
 * but delegates to shared KMP CurrencyFormatter for actual formatting.
 * 
 * Benefits:
 * - iOS formatting features now available in Android (locale-aware, compact, etc.)
 * - Zero code duplication
 * - Consistent formatting across platforms
 * - Easy to maintain
 */
object CurrencyHelper {
    
    private const val CURRENCY_PREF = "currency_preference"
    private const val SELECTED_CURRENCY = "selected_currency"
    
    // Delegate to shared formatter
    val currencies get() = SharedCurrencyFormatter.getSupportedCurrencies()
    
    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(CURRENCY_PREF, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_CURRENCY, "TRY") ?: "TRY"
    }
    
    fun setCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(CURRENCY_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_CURRENCY, currency).apply()
    }
    
    fun getCurrencySymbol(context: Context): String {
        val currency = getCurrency(context)
        // Delegate to shared formatter
        return SharedCurrencyFormatter.getCurrencySymbol(currency)
    }
    
    /**
     * Format amount using shared KMP formatter
     * Now supports iOS features: locale-aware, grouping separators, etc.
     */
    fun formatAmount(context: Context, amount: Long): String {
        val currency = getCurrency(context)
        
        // Delegate to shared formatter (iOS gelişmiş özellikleri ile!)
        return SharedCurrencyFormatter.format(
            minorUnits = amount,
            currencyCode = currency,
            showSign = amount < 0,
            isIncome = false
        )
    }
    
    /**
     * Format with sign (+ for income, - for expense)
     * NEW FEATURE: Now available thanks to iOS pattern
     */
    fun formatAmountWithSign(context: Context, amount: Long, isIncome: Boolean): String {
        val currency = getCurrency(context)
        
        return SharedCurrencyFormatter.format(
            minorUnits = amount,
            currencyCode = currency,
            showSign = true,
            isIncome = isIncome
        )
    }
    
    /**
     * Format in compact form (1.5K ₺)
     * NEW FEATURE: Available from shared formatter
     */
    fun formatAmountCompact(context: Context, amount: Long): String {
        val currency = getCurrency(context)
        
        return SharedCurrencyFormatter.formatCompact(
            minorUnits = amount,
            currencyCode = currency
        )
    }
}
