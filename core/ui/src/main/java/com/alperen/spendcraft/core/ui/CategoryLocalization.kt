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
        // Expense Categories
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
        "Yemek" to "Food",
        "İçecek" to "Beverages",
        "Uçak" to "Flights",
        "Yakıt" to "Fuel",
        "Giyim" to "Clothing",
        "Elektronik" to "Electronics",
        "Ev" to "Home",
        "Hediye" to "Gifts",
        "Spor" to "Sports",
        "Oyun" to "Gaming",
        "Vergi" to "Taxes",
        "Sigorta" to "Insurance",
        "Abonelik" to "Subscriptions",
        "Kredi" to "Credit",
        "Diğer Gider" to "Other Expense",
        
        // Income Categories
        "Maaş" to "Salary",
        "Kira" to "Rent",
        "Prim" to "Bonus",
        "Yatırım" to "Investment",
        "İkramiye" to "Gift",
        "Serbest Çalışma" to "Freelance",
        "Kira Geliri" to "Rental Income",
        "Diğer Gelir" to "Other Income"
    )

    fun localize(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        return trToEnMap[original] ?: original
    }
}


