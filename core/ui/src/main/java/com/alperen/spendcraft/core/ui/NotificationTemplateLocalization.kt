package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

object NotificationTemplateLocalization {
    private val titleTrToEnMap: Map<String, String> = mapOf(
        "Günaydın! ☀️" to "Good Morning! ☀️",
        "Kahvaltı Hatırlatması ☕" to "Breakfast Reminder ☕",
        "Öğle Yemeği 🍽️" to "Lunch 🍽️",
        "Harcama Kontrolü 💰" to "Spending Check 💰",
        "Akşam Özeti 🌙" to "Evening Summary 🌙",
        "Yarının Planı 📝" to "Tomorrow's Plan 📝",
        "Hafta Sonu Özeti 📊" to "Weekend Summary 📊",
        "Haftalık Hedefler 🎯" to "Weekly Goals 🎯",
        "Ay Başı Bütçe 💵" to "Month Start Budget 💵",
        "Ay Ortası Kontrolü 📈" to "Mid-Month Check 📈",
        "Ay Sonu Raporu 📑" to "Month End Report 📑",
        "Harika Gidiyorsun! 🌟" to "You're Doing Great! 🌟",
        "Tasarruf Zamanı 🐷" to "Time to Save 🐷",
        "Düzenli Kayıt 📱" to "Regular Record 📱",
        "Hafta Sonu Uyarısı 🎉" to "Weekend Warning 🎉",
        "Maaş Günü 💰" to "Payday 💰"
    )
    
    private val bodyTrToEnMap: Map<String, String> = mapOf(
        "Yeni güne harcama hedeflerinizle başlayın" to "Start your new day with spending goals",
        "Kahvaltı harcamanızı kaydetmeyi unutmayın" to "Don't forget to record your breakfast expense",
        "Bugünkü öğle yemeği bütçenizi kontrol edin" to "Check today's lunch budget",
        "Bugün ne kadar harcadınız? Kontrol edin" to "How much have you spent today? Check it out",
        "Günlük harcamalarınızı gözden geçirin" to "Review your daily expenses",
        "Yarın için bütçenizi planlayın" to "Plan your budget for tomorrow",
        "Bu hafta toplam harcamanız: Raporlara göz atın" to "Your total spending this week: Check out the reports",
        "Yeni haftanın bütçe hedeflerini belirleyin" to "Set budget goals for the new week",
        "Yeni ay bütçenizi planlayın" to "Plan your new month's budget",
        "Aylık bütçenizin yarısına ulaştınız" to "You've reached half of your monthly budget",
        "Aylık harcama raporunuzu inceleyin" to "Review your monthly spending report",
        "Bütçe hedeflerinize sadık kalıyorsunuz" to "You're staying true to your budget goals",
        "Her küçük tasarruf büyük hedeflere götürür" to "Every small savings leads to big goals",
        "Harcamalarınızı düzenli kaydetmeye devam edin" to "Continue to record your expenses regularly",
        "Hafta sonu harcamalarına dikkat edin" to "Be careful with weekend spending",
        "Maaşınızı akıllıca planlamayı unutmayın" to "Don't forget to plan your salary wisely"
    )
    
    fun localizeTitle(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        
        return titleTrToEnMap[original] ?: original
    }
    
    fun localizeBody(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        
        return bodyTrToEnMap[original] ?: original
    }
}

