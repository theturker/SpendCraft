package com.alperen.spendcraft

import android.content.Context

object CurrencyHelper {
    
    private const val CURRENCY_PREF = "currency_preference"
    private const val SELECTED_CURRENCY = "selected_currency"
    
    // Para birimleri ve sembolleri
    val currencies = mapOf(
        "USD" to "$",
        "TRY" to "₺",
        "EUR" to "€",
        "GBP" to "£"
    )
    
    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(CURRENCY_PREF, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_CURRENCY, "USD") ?: "USD"
    }
    
    fun setCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(CURRENCY_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_CURRENCY, currency).apply()
    }
    
    fun getCurrencySymbol(context: Context): String {
        val currency = getCurrency(context)
        return currencies[currency] ?: "$"
    }
    
    fun formatAmount(context: Context, amount: Long): String {
        val symbol = getCurrencySymbol(context)
        val sign = if (amount < 0) "-" else ""
        val abs = kotlin.math.abs(amount)
        val major = abs / 100
        val cents = abs % 100
        return "$sign$symbol$major.${cents.toString().padStart(2, '0')}"
    }
}
