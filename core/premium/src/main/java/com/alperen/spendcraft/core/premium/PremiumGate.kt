package com.alperen.spendcraft.core.premium

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Premium Gating Yardımcıları
 * 
 * UI ya da use-case katmanında kolay kullanım için:
 * - PremiumGate composable
 * - PremiumOnly annotation (dokümantatif)
 */
@Composable
fun PremiumGate(
    isPremium: Boolean,
    premiumContent: @Composable () -> Unit,
    freeContent: @Composable () -> Unit
) {
    if (isPremium) {
        premiumContent()
    } else {
        freeContent()
    }
}

/**
 * Premium özellik için kilit ekranı
 */
@Composable
fun PremiumLockScreen(
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = SpendCraftTheme.colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = SpendCraftTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Premium Özellik",
                style = SpendCraftTheme.typography.titleMedium,
                color = SpendCraftTheme.colors.onSurface
            )
            
            Text(
                text = "Bu özelliği kullanmak için Premium'a yükseltin",
                style = SpendCraftTheme.typography.bodyMedium,
                color = SpendCraftTheme.colors.onSurfaceVariant
            )
            
            Button(
                onClick = onUpgrade,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Premium'a Yükselt")
            }
        }
    }
}

/**
 * Premium badge göstergesi
 */
@Composable
fun PremiumBadge(
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    if (isPremium) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = SpendCraftTheme.colors.primary
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = SpendCraftTheme.colors.onPrimary,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Premium",
                    color = SpendCraftTheme.colors.onPrimary,
                    style = SpendCraftTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * Dokümantatif annotation - gerçek kontrol ViewModel/use-case'de yapılır
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PremiumOnly
