package com.alperen.spendcraft.ui.iosTheme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * iOS Corner Radius System
 * Extracted from RoundedRectangle() calls in iOS SwiftUI
 */
object IOSRadius {
    val radius4 = 4.dp          // Small progress bars, indicators
    val radius8 = 8.dp          // Small cards, badges
    val radius10 = 10.dp        // Category icon containers
    val radius12 = 12.dp        // Achievement cards, info boxes
    val radius14 = 14.dp        // iOS alert dialogs
    val radius15 = 15.dp        // Summary cards, standard buttons
    val radius16 = 16.dp        // Large cards, input fields
    val radius20 = 20.dp        // Balance card, feature cards
    val radius25 = 25.dp        // Large action buttons
    val radius30 = 30.dp        // Modal sheets (top corners)
    
    // Shape objects for direct use
    val small = RoundedCornerShape(radius8)
    val medium = RoundedCornerShape(radius12)
    val large = RoundedCornerShape(radius16)
    val extraLarge = RoundedCornerShape(radius20)
    
    // Component-specific
    val button = RoundedCornerShape(radius15)
    val card = RoundedCornerShape(radius16)
    val balanceCard = RoundedCornerShape(radius20)
    val achievementCard = RoundedCornerShape(radius12)
    val modal = RoundedCornerShape(topStart = radius30, topEnd = radius30)
    val circle = CircleShape
    val capsule = RoundedCornerShape(percent = 50)  // iOS Capsule equivalent
}

