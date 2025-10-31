package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

object NotificationCategoryLocalization {
    private val trToEnMap: Map<String, String> = mapOf(
        "Sabah" to "Morning",
        "Öğlen" to "Noon",
        "Akşam" to "Evening",
        "Haftalık" to "Weekly",
        "Aylık" to "Monthly",
        "Motivasyon" to "Motivation",
        "Özel" to "Special"
    )
    
    fun localize(context: Context, category: String): String {
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return category
        
        return trToEnMap[category] ?: category
    }
}

