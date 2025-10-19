package com.alperen.spendcraft.feature.notifications

import java.util.UUID

/**
 * iOS NotificationSettingsView'in Android karşılığı
 * Template ve Custom notification modelleri
 */

/**
 * Notification Template - iOS: NotificationTemplate struct
 * NotificationSettingsView.swift pattern
 */
data class NotificationTemplate(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val title: String,
    val body: String,
    val icon: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = false,
    val daysOfWeek: List<Int>? = null, // 1-7 (Pazar-Cumartesi)
    val daysOfMonth: List<Int>? = null // 1-31
) {
    companion object {
        // iOS NotificationManager.swift'teki default templates
        fun getDefaultTemplates(): List<NotificationTemplate> = listOf(
            // Sabah Bildirimleri
            NotificationTemplate(
                category = "Sabah",
                title = "Günaydın! ☀️",
                body = "Yeni güne harcama hedeflerinizle başlayın",
                icon = "sun.max.fill",
                hour = 8,
                minute = 0
            ),
            NotificationTemplate(
                category = "Sabah",
                title = "Kahvaltı Hatırlatması ☕",
                body = "Kahvaltı harcamanızı kaydetmeyi unutmayın",
                icon = "cup.and.saucer.fill",
                hour = 9,
                minute = 0
            ),
            
            // Öğlen Bildirimleri
            NotificationTemplate(
                category = "Öğlen",
                title = "Öğle Yemeği 🍽️",
                body = "Bugünkü öğle yemeği bütçenizi kontrol edin",
                icon = "fork.knife",
                hour = 12,
                minute = 30
            ),
            NotificationTemplate(
                category = "Öğlen",
                title = "Harcama Kontrolü 💰",
                body = "Bugün ne kadar harcadınız? Kontrol edin",
                icon = "chart.bar.fill",
                hour = 14,
                minute = 0
            ),
            
            // Akşam Bildirimleri
            NotificationTemplate(
                category = "Akşam",
                title = "Akşam Özeti 🌙",
                body = "Günlük harcamalarınızı gözden geçirin",
                icon = "moon.stars.fill",
                hour = 20,
                minute = 0
            ),
            NotificationTemplate(
                category = "Akşam",
                title = "Yarının Planı 📝",
                body = "Yarın için bütçenizi planlayın",
                icon = "calendar.badge.clock",
                hour = 21,
                minute = 30
            ),
            
            // Haftalık Bildirimler
            NotificationTemplate(
                category = "Haftalık",
                title = "Hafta Sonu Özeti 📊",
                body = "Bu hafta toplam harcamanız: Raporlara göz atın",
                icon = "calendar",
                hour = 18,
                minute = 0,
                daysOfWeek = listOf(7) // Cumartesi
            ),
            NotificationTemplate(
                category = "Haftalık",
                title = "Haftalık Hedefler 🎯",
                body = "Yeni haftanın bütçe hedeflerini belirleyin",
                icon = "target",
                hour = 10,
                minute = 0,
                daysOfWeek = listOf(2) // Pazartesi
            ),
            
            // Aylık Bildirimler
            NotificationTemplate(
                category = "Aylık",
                title = "Ay Başı Bütçe 💵",
                body = "Yeni ay bütçenizi planlayın",
                icon = "calendar.badge.clock",
                hour = 9,
                minute = 0,
                daysOfMonth = listOf(1)
            ),
            NotificationTemplate(
                category = "Aylık",
                title = "Ay Ortası Kontrolü 📈",
                body = "Aylık bütçenizin yarısına ulaştınız",
                icon = "chart.line.uptrend.xyaxis",
                hour = 20,
                minute = 0,
                daysOfMonth = listOf(15)
            ),
            NotificationTemplate(
                category = "Aylık",
                title = "Ay Sonu Raporu 📑",
                body = "Aylık harcama raporunuzu inceleyin",
                icon = "doc.text.fill",
                hour = 19,
                minute = 0,
                daysOfMonth = listOf(28, 29, 30, 31)
            ),
            
            // Motivasyon Bildirimleri
            NotificationTemplate(
                category = "Motivasyon",
                title = "Harika Gidiyorsun! 🌟",
                body = "Bütçe hedeflerinize sadık kalıyorsunuz",
                icon = "star.fill",
                hour = 12,
                minute = 0
            ),
            NotificationTemplate(
                category = "Motivasyon",
                title = "Tasarruf Zamanı 🐷",
                body = "Her küçük tasarruf büyük hedeflere götürür",
                icon = "dollarsign.circle.fill",
                hour = 15,
                minute = 0
            ),
            NotificationTemplate(
                category = "Motivasyon",
                title = "Düzenli Kayıt 📱",
                body = "Harcamalarınızı düzenli kaydetmeye devam edin",
                icon = "checkmark.seal.fill",
                hour = 17,
                minute = 0
            ),
            
            // Özel Günler
            NotificationTemplate(
                category = "Özel",
                title = "Hafta Sonu Uyarısı 🎉",
                body = "Hafta sonu harcamalarına dikkat edin",
                icon = "gift.fill",
                hour = 10,
                minute = 0,
                daysOfWeek = listOf(6, 7) // Cuma-Cumartesi
            ),
            NotificationTemplate(
                category = "Özel",
                title = "Maaş Günü 💰",
                body = "Maaşınızı akıllıca planlamayı unutmayın",
                icon = "banknote.fill",
                hour = 9,
                minute = 0,
                daysOfMonth = listOf(1, 15)
            )
        )
    }
}

/**
 * Custom Notification - iOS: CustomNotification struct
 * Kullanıcının oluşturduğu özel bildirimler
 */
data class CustomNotification(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var body: String,
    var hour: Int,
    var minute: Int,
    var isEnabled: Boolean = true,
    var daysOfWeek: List<Int>? = null // null = her gün
)

/**
 * Notification Preferences - SharedPreferences'ta saklanacak
 */
data class NotificationPreferences(
    val templates: List<NotificationTemplate> = NotificationTemplate.getDefaultTemplates(),
    val customNotifications: List<CustomNotification> = emptyList()
)


