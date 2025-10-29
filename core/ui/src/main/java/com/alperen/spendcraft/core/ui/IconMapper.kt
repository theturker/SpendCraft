package com.alperen.spendcraft.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps iOS SF Symbol names to Material Icons
 * iOS uses SF Symbols (e.g., "fork.knife"), Android uses Material Icons
 */
object IconMapper {
    
    /**
     * Check if icon string is an emoji
     */
    fun isEmoji(icon: String?): Boolean {
        if (icon.isNullOrBlank()) return false
        // Check if first character is emoji (Unicode emoji range)
        val firstChar = icon.first()
        return firstChar.code in 0x1F300..0x1F9FF || 
               firstChar.code in 0x2600..0x26FF ||
               firstChar.code in 0x2700..0x27BF ||
               firstChar.code in 0xFE00..0xFE0F ||
               firstChar.code in 0x1F900..0x1F9FF ||
               firstChar.code in 0x1F1E0..0x1F1FF
    }
    
    /**
     * Convert emoji or SF Symbol name to drawable resource ID
     * Returns drawable ID for emoji icons, used with PainterResource
     */
    fun getCategoryIconDrawable(icon: String?): Int {
        if (icon.isNullOrBlank()) {
            return R.drawable.ic_circle_fill
        }
        
        return when (icon) {
            // Emoji icons (veritabanında saklanıyor)
            "🍔", "🍽️" -> R.drawable.ic_fork_knife // Yemek
            "🚌", "🚋" -> R.drawable.ic_tram_fill // Ulaşım
            "🎬" -> R.drawable.ic_film_fill // Eğlence
            "💼" -> R.drawable.ic_briefcase_fill // Maaş / İş
            "🛒" -> R.drawable.ic_cart_fill // Alışveriş
            "🏠" -> R.drawable.ic_house_fill // Ev
            "🚗" -> R.drawable.ic_car_fill // Araba
            "✈️" -> R.drawable.ic_airplane // Uçak / Seyahat
            "⚡" -> R.drawable.ic_bolt_fill // Elektrik / Faturalar
            "👜" -> R.drawable.ic_bag_fill // Çanta / Moda
            "🎁" -> R.drawable.ic_gift_fill // Hediye
            "📚" -> R.drawable.ic_book_fill // Kitap / Eğitim
            "🎮" -> R.drawable.ic_gamecontroller_fill // Oyun
            "❤️" -> R.drawable.ic_heart_fill // Sağlık / Sevgi
            "💳" -> R.drawable.ic_creditcard_fill // Kredi Kartı
            "💊" -> R.drawable.ic_pills_fill // İlaç / Sağlık
            "🎓" -> R.drawable.ic_graduationcap_fill // Eğitim
            "📱" -> R.drawable.ic_phone_fill // Telefon / İletişim
            
            // Android drawable isimleri (DbModule'de kullanılan format)
            "ic_fork_knife", "fork.knife" -> R.drawable.ic_fork_knife
            "ic_car_fill", "car.fill" -> R.drawable.ic_car_fill
            "ic_doc_text_fill", "doc.text.fill" -> R.drawable.ic_doc_text_fill
            "ic_gamecontroller_fill", "gamecontroller.fill" -> R.drawable.ic_gamecontroller_fill
            "ic_cart_fill", "cart.fill" -> R.drawable.ic_cart_fill
            "ic_heart_fill", "heart.fill" -> R.drawable.ic_heart_fill
            "ic_book_closed_fill", "book.closed.fill" -> R.drawable.ic_book_closed_fill
            "ic_book_fill", "book.fill" -> R.drawable.ic_book_fill
            "ic_creditcard_fill", "creditcard.fill" -> R.drawable.ic_creditcard_fill
            "ic_ellipsis_circle_fill", "ellipsis.circle.fill" -> R.drawable.ic_ellipsis_circle_fill
            "ic_banknote", "banknote", "banknote.fill" -> R.drawable.ic_banknote
            "ic_house_fill", "house.fill" -> R.drawable.ic_house_fill
            "ic_star_fill", "star.fill" -> R.drawable.ic_star_fill
            "ic_chart_line_uptrend", "chart.line.uptrend.xyaxis" -> R.drawable.ic_chart_line_uptrend
            "ic_gift_fill", "gift.fill" -> R.drawable.ic_gift_fill
            "ic_briefcase_fill", "briefcase.fill" -> R.drawable.ic_briefcase_fill
            "ic_building_2_fill", "building.2.fill" -> R.drawable.ic_building_2_fill
            "ic_tram_fill", "tram.fill" -> R.drawable.ic_tram_fill
            "ic_airplane", "airplane" -> R.drawable.ic_airplane
            "ic_bolt_fill", "bolt.fill" -> R.drawable.ic_bolt_fill
            "ic_bag_fill", "bag.fill" -> R.drawable.ic_bag_fill
            "ic_film_fill", "film.fill" -> R.drawable.ic_film_fill
            "ic_pills_fill", "pills.fill" -> R.drawable.ic_pills_fill
            "ic_graduationcap_fill", "graduationcap.fill" -> R.drawable.ic_graduationcap_fill
            "ic_phone_fill", "phone.fill" -> R.drawable.ic_phone_fill
            "ic_circle_fill", "circle.fill" -> R.drawable.ic_circle_fill
            
            else -> R.drawable.ic_circle_fill // Default
        }
    }
    
    /**
     * Convert iOS SF Symbol name to Material Icon
     * Returns a fallback icon if mapping not found
     */
    fun getIconFromSFSymbol(sfSymbolName: String?, isIncome: Boolean = false): ImageVector {
        if (sfSymbolName.isNullOrBlank()) {
            // Fallback: Use default icon based on transaction type
            return if (isIncome) Icons.Default.Add else Icons.Default.ShoppingCart
        }
        
        return when (sfSymbolName.lowercase()) {
            // Food & Dining
            "fork.knife" -> Icons.Default.Restaurant
            "fork.knife.fill" -> Icons.Default.Restaurant
            
            // Shopping
            "cart" -> Icons.Default.ShoppingCart
            "cart.fill" -> Icons.Default.ShoppingCart
            
            // Transportation
            "car" -> Icons.Default.DirectionsCar
            "car.fill" -> Icons.Default.DirectionsCar
            "tram.fill" -> Icons.Default.Train
            "bus.fill" -> Icons.Default.DirectionsBus
            "bicycle" -> Icons.Default.DirectionsBike
            
            // Home
            "house" -> Icons.Default.Home
            "house.fill" -> Icons.Default.Home
            
            // Documents
            "doc.text" -> Icons.Default.Description
            "doc.text.fill" -> Icons.Default.Description
            "doc.fill" -> Icons.Default.Description
            
            // Entertainment
            "gamecontroller" -> Icons.Default.SportsEsports
            "gamecontroller.fill" -> Icons.Default.SportsEsports
            "tv" -> Icons.Default.Tv
            "tv.fill" -> Icons.Default.Tv
            "music.note" -> Icons.Default.MusicNote
            
            // Health
            "heart" -> Icons.Default.Favorite
            "heart.fill" -> Icons.Default.Favorite
            "cross.case" -> Icons.Default.LocalHospital
            "cross.case.fill" -> Icons.Default.LocalHospital
            
            // Education
            "book" -> Icons.Default.MenuBook
            "book.fill" -> Icons.Default.MenuBook
            "book.closed" -> Icons.Default.MenuBook
            "book.closed.fill" -> Icons.Default.MenuBook
            "graduationcap" -> Icons.Default.School
            "graduationcap.fill" -> Icons.Default.School
            
            // Finance - Income
            "banknote" -> Icons.Default.Payments
            "banknote.fill" -> Icons.Default.Payments
            "dollarsign" -> Icons.Default.AttachMoney
            "dollarsign.circle" -> Icons.Default.AttachMoney
            "dollarsign.circle.fill" -> Icons.Default.AttachMoney
            
            // Finance - Investment
            "chart.line.uptrend.xyaxis" -> Icons.Default.TrendingUp
            "chart.bar" -> Icons.Default.BarChart
            "chart.bar.fill" -> Icons.Default.BarChart
            
            // Rewards
            "star" -> Icons.Default.Star
            "star.fill" -> Icons.Default.Star
            "gift" -> Icons.Default.CardGiftcard
            "gift.fill" -> Icons.Default.CardGiftcard
            
            // Utilities
            "bolt" -> Icons.Default.Bolt
            "bolt.fill" -> Icons.Default.Bolt
            "drop" -> Icons.Default.WaterDrop
            "drop.fill" -> Icons.Default.WaterDrop
            "flame" -> Icons.Default.Whatshot
            "flame.fill" -> Icons.Default.Whatshot
            
            // Communication
            "phone" -> Icons.Default.Phone
            "phone.fill" -> Icons.Default.Phone
            "envelope" -> Icons.Default.Email
            "envelope.fill" -> Icons.Default.Email
            
            // Shopping Categories
            "bag" -> Icons.Default.ShoppingBag
            "bag.fill" -> Icons.Default.ShoppingBag
            "creditcard" -> Icons.Default.CreditCard
            "creditcard.fill" -> Icons.Default.CreditCard
            
            // Other
            "briefcase" -> Icons.Default.Work
            "briefcase.fill" -> Icons.Default.Work
            "building" -> Icons.Default.Business
            "building.fill" -> Icons.Default.Business
            "building.2" -> Icons.Default.Apartment
            "building.2.fill" -> Icons.Default.Apartment
            "airplane" -> Icons.Default.Flight
            "airplane.circle" -> Icons.Default.Flight
            
            // Misc
            "ellipsis.circle" -> Icons.Default.MoreHoriz
            "ellipsis.circle.fill" -> Icons.Default.MoreHoriz
            "circle" -> Icons.Default.Circle
            "circle.fill" -> Icons.Default.Circle
            
            // Fallback
            else -> if (isIncome) Icons.Default.Add else Icons.Default.ShoppingCart
        }
    }
}


