package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

/**
 * Basit kategori adı yerelleştirmesi.
 * Veritabanında TR saklanan bilinen isimleri cihaz dili TR değilse EN karşılıklarına çevirir.
 * Uzun vadede nameKey/slug ile kaynaktan okumak daha doğrudur; bu katman geçici bir koruma sağlar.
 */
object CategoryLocalization {
    private val trToEnMap: Map<String, String> = mapOf(
        "Ulaşım" to "Transportation",
        "Eğlence" to "Entertainment",
        "Alışveriş" to "Shopping",
        "Sağlık" to "Health",
        "Eğitim" to "Education",
        "Diğer" to "Other",
        "Gıda" to "Food",
        "Barınma" to "Housing",
        "Fatura" to "Bills",
        "Tasarruf" to "Savings",
        "Gelir" to "Income",
        "Maaş" to "Salary",
        "Yemek" to "Food",
        "İçecek" to "Beverages",
        "Uçak" to "Flights",
        "Yakıt" to "Fuel",
        "Giyim" to "Clothing",
        "Elektronik" to "Electronics",
        "Ev" to "Home",
        "Kira" to "Rent",
        "Hediye" to "Gifts",
        "Spor" to "Sports",
        "Oyun" to "Gaming",
        "Vergi" to "Taxes",
        "Sigorta" to "Insurance",
        "Abonelik" to "Subscriptions"
    )

    fun localize(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        return trToEnMap[original] ?: original
    }
}


