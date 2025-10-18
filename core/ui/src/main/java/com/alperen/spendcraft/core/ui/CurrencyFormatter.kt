package com.alperen.spendcraft.core.ui

import android.content.Context
import java.text.NumberFormat
import java.util.Locale

/**
 * iOS CoreDataStack.swift'teki formatCurrency() fonksiyonunun birebir karşılığı
 * 
 * iOS Pattern (CoreDataStack.swift:248-275):
 * - TRY: groupingSeparator=".", decimalSeparator=",", format: "1.234,56 ₺"
 * - USD/EUR/GBP: Standard locale format, format: "$1,234.56"
 */
object CurrencyFormatter {
    // iOS: @AppStorage("selectedCurrency")
    private const val PREF_NAME = "app_prefs"  // iOS ile aynı SharedPreferences
    private const val SELECTED_CURRENCY = "selectedCurrency"  // iOS key
    private const val SELECTED_CURRENCY_SYMBOL = "selectedCurrencySymbol"  // iOS key
    
    // iOS: Currency.currencies - CurrencySettingsView.swift:17-30
    private val currencies = mapOf(
        "TRY" to "₺",
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "CHF" to "Fr",
        "CAD" to "C$",
        "AUD" to "A$",
        "CNY" to "¥",
        "RUB" to "₽",
        "SAR" to "﷼",
        "AED" to "د.إ"
    )

    fun getSelectedCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_CURRENCY, "TRY") ?: "TRY"  // Default TRY - iOS pattern
    }

    fun getCurrencySymbol(context: Context): String {
        val code = getSelectedCurrency(context)
        return currencies[code] ?: "₺"
    }
    
    /**
     * iOS formatCurrency() - CoreDataStack.swift:248-275
     * 
     * TRY Format: "1.234,56 ₺"
     * - groupingSeparator: "."
     * - decimalSeparator: ","
     * - locale: tr_TR
     * 
     * USD Format: "$1,234.56"
     * - locale: en_US
     */
    fun format(context: Context, amountMinor: Long): String {
        val selectedCurrency = getSelectedCurrency(context)
        val symbol = getCurrencySymbol(context)
        
        val amount = amountMinor / 100.0
        
        val formatter = NumberFormat.getInstance(
            if (selectedCurrency == "TRY") Locale("tr", "TR") else Locale.US
        ).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            
            // TRY için özel ayarlar - iOS: CoreDataStack.swift:258-261
            if (selectedCurrency == "TRY") {
                // tr_TR locale zaten doğru separatör'leri kullanıyor
                // grouping: "." decimal: ","
            }
        }
        
        val formattedAmount = formatter.format(amount)
        
        // iOS formatı: "1.234,56 ₺" (sembol sonda, boşluk ile)
        return if (selectedCurrency == "TRY") {
            "$formattedAmount $symbol"
        } else {
            // USD/EUR vb: "$1,234.56" (sembol başta, boşluk yok)
            "$symbol$formattedAmount"
        }
    }
}




