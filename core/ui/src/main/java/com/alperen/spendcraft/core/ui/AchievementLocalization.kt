package com.alperen.spendcraft.core.ui

import android.content.Context
import java.util.Locale

/**
 * Achievement isim ve açıklama yerelleştirmesi.
 * Veritabanında TR saklanan bilinen achievement isimlerini cihaz dili TR değilse EN karşılıklarına çevirir.
 */
object AchievementLocalization {
    private val achievementTrToEnMap: Map<String, String> = mapOf(
        // Achievement Names
        "İlk Adım" to "First Step",
        "İşlem Ustası" to "Transaction Master",
        "Süper İzleyici" to "Super Tracker",
        "Hafta Savaşçısı" to "Week Warrior",
        "Aylık Şampiyon" to "Month Champion",
        "Bütçe Planlayıcısı" to "Budget Planner",
        "Kategori Uzmanı" to "Category Expert",
        "Tasarruf Ustası" to "Savings Master",
        "Kayıt Şampiyonu" to "Record Champion",
        "Ay Kralı" to "Month King",
        "Bütçe Koruyucusu" to "Budget Guardian",
        "Tasarruf Başlangıcı" to "Savings Beginner",
        "Premium Üye" to "Premium Member",
        "AI Keşifçisi" to "AI Explorer",
        "Başlangıç" to "Beginner",
        "Düzenli" to "Regular",
        "Uzman" to "Expert",
        "Kategori Ustası" to "Category Master",
        "Bütçe Bilinci" to "Budget Awareness",
        "Tutumlu" to "Thrifty",
        "Yatırımcı" to "Investor"
    )
    
    private val achievementDescTrToEnMap: Map<String, String> = mapOf(
        // Achievement Descriptions
        "İlk işleminizi kaydedin" to "Record your first transaction",
        "10 işlem kaydedin" to "Record 10 transactions",
        "100 işlem kaydedin" to "Record 100 transactions",
        "7 gün üst üste işlem kaydedin" to "Record transactions for 7 consecutive days",
        "30 gün üst üste işlem kaydedin" to "Record transactions for 30 consecutive days",
        "İlk bütçenizi oluşturun" to "Create your first budget",
        "5 farklı kategoride işlem yapın" to "Make transactions in 5 different categories",
        "Bir ay boyunca bütçenizi aşmayın" to "Don't exceed your budget for a month",
        "5 farklı kategori kullanın" to "Use 5 different categories",
        "Aylık bütçenize uyun" to "Stay within your monthly budget",
        "İlk gelirinizi kaydedin" to "Record your first income",
        "Toplam 1000 TL tasarruf edin" to "Save a total of 1000 TL",
        "1000 işlem kaydettiniz" to "You recorded 1000 transactions",
        "30 gün üst üste işlem kaydettiniz" to "You recorded transactions for 30 consecutive days",
        "İlk işleminizi kaydettiniz" to "You recorded your first transaction",
        "100 işlem kaydettiniz" to "You recorded 100 transactions",
        "7 gün üst üste işlem kaydettiniz" to "You recorded transactions for 7 consecutive days",
        "İlk bütçenizi oluşturdunuz" to "You created your first budget",
        "3 ay üst üste bütçenizi aşmadınız" to "You didn't exceed your budget for 3 consecutive months",
        "İlk defa aylık gelir > gider" to "For the first time monthly income > expenses",
        "6 ay üst üste tasarruf yaptınız" to "You saved money for 6 consecutive months",
        "Premium üyeliğe geçtiniz" to "You upgraded to premium membership",
        "AI önerilerini 10 kez kullandınız" to "You used AI recommendations 10 times"
    )

    fun localizeName(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        return achievementTrToEnMap[original] ?: original
    }
    
    fun localizeDescription(context: Context, original: String?): String {
        if (original.isNullOrBlank()) return original ?: ""
        val isTr = Locale.getDefault().language.equals("tr", ignoreCase = true)
        if (isTr) return original
        return achievementDescTrToEnMap[original] ?: original
    }
}

