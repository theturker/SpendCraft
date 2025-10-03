package com.alperen.spendcraft.core.ui

import android.content.Context

object CurrencyFormatter {
    private const val CURRENCY_PREF = "currency_preference"
    private const val SELECTED_CURRENCY = "selected_currency"

    private val currencies = mapOf(
        "USD" to "$",
        "TRY" to "₺",
        "EUR" to "€",
        "GBP" to "£"
    )

    private fun getSelectedCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(CURRENCY_PREF, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_CURRENCY, "USD") ?: "USD"
    }

    fun getCurrencySymbol(context: Context): String {
        val code = getSelectedCurrency(context)
        return currencies[code] ?: "$"
    }

    fun format(context: Context, amountMinor: Long): String {
        val symbol = getCurrencySymbol(context)
        val sign = if (amountMinor < 0) "-" else ""
        val abs = kotlin.math.abs(amountMinor)
        val major = abs / 100
        val cents = abs % 100
        return "$sign$symbol$major.${cents.toString().padStart(2, '0')}"
    }
}



