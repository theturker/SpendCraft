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


