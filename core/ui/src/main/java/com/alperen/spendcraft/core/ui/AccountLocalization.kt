package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

/**
 * Basit hesap adı yerelleştirmesi.
 * Veritabanında TR saklanan bilinen isimleri cihaz dili TR değilse EN karşılıklarına çevirir.
 */
object AccountLocalization {
    private val trToEnMap: Map<String, String> = mapOf(
        "Ana Hesap" to "Main Account",
        "Nakit" to "Cash",
        "Banka" to "Bank"
    )

    fun localize(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        return trToEnMap[original] ?: original
    }
}

