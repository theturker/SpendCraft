package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

object NotificationLocalization {
    private val titleTrToEnMap: Map<String, String> = mapOf(
        "🎉 İlk İşleminiz!" to "🎉 Your First Transaction!",
        "⚠️ Bütçe Uyarısı" to "⚠️ Budget Alert",
        "⚠️ Genel Bütçe Uyarısı" to "⚠️ General Budget Alert",
        "💰 Harcama Hatırlatıcısı" to "💰 Spending Reminder"
    )
    
    private val messageTrToEnMap: Map<String, String> = mapOf(
        "SpendCraft'e hoş geldiniz! İlk işleminiz başarıyla kaydedildi." to "Welcome to SpendCraft! Your first transaction has been successfully recorded.",
        "bütçenizi 100% aştınız!" to "budget by 100%!",
        "bütçenizin" to "of your",
        "%'ini kullandınız!" to "budget!",
        "Toplam açığınız:" to "Your total deficit:",
        "Bugün henüz hiç işlem eklemediniz. Harcamalarınızı takip etmeyi unutmayın!" to "You haven't added any transactions today. Don't forget to track your expenses!"
    )
    
    fun localizeTitle(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        
        // Exact match first
        titleTrToEnMap[original]?.let { return it }
        
        // Pattern matching for dynamic messages
        when {
            original.contains("Bütçe Uyarısı") -> {
                return original.replace("Bütçe Uyarısı", "Budget Alert")
            }
            original.contains("Genel Bütçe Uyarısı") -> {
                return original.replace("Genel Bütçe Uyarısı", "General Budget Alert")
            }
            original.contains("İlk İşleminiz") -> {
                return original.replace("İlk İşleminiz", "Your First Transaction")
            }
            original.contains("Harcama Hatırlatıcısı") -> {
                return original.replace("Harcama Hatırlatıcısı", "Spending Reminder")
            }
        }
        
        return original
    }
    
    fun localizeMessage(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        
        // Exact match first
        messageTrToEnMap[original]?.let { return it }
        
        // Pattern matching for dynamic messages
        var localized = original
        
        // Budget messages with category name
        // Pattern: "KategoriAdı bütçenizi 100% aştınız!"
        if (localized.contains(" bütçenizi 100% aştınız") || localized.contains("bütçenizi 100% aştınız!")) {
            val categoryName = localized.substringBefore(" bütçenizi").trim()
            if (categoryName.isNotEmpty()) {
                val localizedCategory = CategoryLocalization.localize(context, categoryName)
                localized = "You have exceeded your $localizedCategory budget by 100%!"
            } else {
                localized = "You have exceeded your budget by 100%!"
            }
        } 
        // Pattern: "KategoriAdı bütçenizin %80'ini kullandınız!"
        else if (localized.contains(" bütçenizin") && localized.contains("%'ini kullandınız")) {
            val categoryName = localized.substringBefore(" bütçenizin").trim()
            // Extract percentage - could be like "%80'ini" or "80%'ini"
            val percentageMatch = Regex("""%?(\d+)%?'ini""").find(localized)
            val percentage = percentageMatch?.groupValues?.get(1)?.toIntOrNull() ?: 80
            if (categoryName.isNotEmpty()) {
                val localizedCategory = CategoryLocalization.localize(context, categoryName)
                localized = "You have used $percentage% of your $localizedCategory budget!"
            } else {
                localized = "You have used $percentage% of your budget!"
            }
        } 
        // Pattern: "Toplam açığınız: 123.45 TL"
        else if (localized.contains("Toplam açığınız:")) {
            val amount = localized.substringAfter("Toplam açığınız:").trim()
            localized = "Your total deficit: $amount"
        }
        // Welcome message
        else if (localized.contains("SpendCraft'e hoş geldiniz") || 
                 (localized.contains("hoş geldiniz") && localized.contains("İlk işleminiz başarıyla kaydedildi"))) {
            localized = "Welcome to SpendCraft! Your first transaction has been successfully recorded."
        }
        // Spending reminder
        else if (localized.contains("Bugün henüz hiç işlem eklemediniz") || 
                 localized.contains("Harcamalarınızı takip etmeyi unutmayın")) {
            localized = "You haven't added any transactions today. Don't forget to track your expenses!"
        }
        
        return localized
    }
}

