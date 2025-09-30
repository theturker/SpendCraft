package com.alperen.spendcraft.feature.premiumdebug

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard

@Composable
fun BillingDebugScreen(
    billingRepository: BillingRepository,
    onBack: () -> Unit = {}
) {
    val isPremium by billingRepository.isPremium.collectAsState(initial = false)
    
    AppScaffold(
        title = "Billing Debug",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Premium Status
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Premium Durumu",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPremium) "✅ Premium Aktif" else "❌ Premium Değil",
                            style = SpendCraftTheme.typography.bodyLarge,
                            color = if (isPremium) SpendCraftTheme.colors.primary else SpendCraftTheme.colors.error
                        )
                    }
                }
            }
            
            // Simple Info
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Debug Bilgileri",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bu ekran premium durumunu test etmek için kullanılır.",
                            style = SpendCraftTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Premium özellikler:",
                            style = SpendCraftTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "• Sınırsız AI önerileri",
                            style = SpendCraftTheme.typography.bodySmall,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                        Text(
                            text = "• Gelişmiş raporlar",
                            style = SpendCraftTheme.typography.bodySmall,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                        Text(
                            text = "• Reklamsız deneyim",
                            style = SpendCraftTheme.typography.bodySmall,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}