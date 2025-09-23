package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Streak
import com.alperen.spendcraft.core.ui.ModernCard

@Composable
fun StreakBanner(
    streak: Streak,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeText = when {
        streak.current >= 30 -> "🏆 Gold"
        streak.current >= 7 -> "🥈 Silver"
        streak.current >= 3 -> "🥉 Bronze"
        else -> null
    }

    ModernCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = null,
                    tint = if (streak.current > 0) Color(0xFFFF6B35) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = "🔥 ${streak.current} day streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Best ${streak.best}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            badgeText?.let { badge ->
                Surface(
                    color = when {
                        badge.contains("Gold") -> Color(0xFFFFD700)
                        badge.contains("Silver") -> Color(0xFFC0C0C0)
                        badge.contains("Bronze") -> Color(0xFFCD7F32)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

