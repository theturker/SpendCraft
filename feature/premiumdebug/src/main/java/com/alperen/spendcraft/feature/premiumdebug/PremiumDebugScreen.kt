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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard

/**
 * QA/Debug Ekranı
 * 
 * Sadece debug build'da görünür.
 * Premium durumunu simüle etmek için toggle (yalnızca DataStore'da set etsin).
 * "Satın alma akışını taklit" butonu ekle (gerçek satın alma çağrısını yapmaz, sadece UI akışını test eder).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumDebugScreen(
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PremiumDebugViewModel = hiltViewModel()
    val isPremium by viewModel.isPremium.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    AppScaffold(
        title = "🔧 Premium Debug",
        onBack = onNavigateUp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Status
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📊 Mevcut Durum",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Premium Durumu:",
                                style = SpendCraftTheme.typography.bodyLarge,
                                color = SpendCraftTheme.colors.onSurfaceVariant
                            )
                            
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPremium) 
                                        SpendCraftTheme.colors.primary 
                                    else 
                                        SpendCraftTheme.colors.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = if (isPremium) "✅ Premium" else "❌ Free",
                                    style = SpendCraftTheme.typography.bodyMedium,
                                    color = if (isPremium) 
                                        SpendCraftTheme.colors.onPrimary 
                                    else 
                                        SpendCraftTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Premium Toggle
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🎛️ Premium Simülasyonu",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Bu toggle sadece DataStore'da premium durumunu değiştirir. Gerçek satın alma yapmaz.",
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Premium'u Aç/Kapat:",
                                style = SpendCraftTheme.typography.bodyLarge,
                                color = SpendCraftTheme.colors.onSurface
                            )
                            
                            Switch(
                                checked = isPremium,
                                onCheckedChange = { 
                                    if (isPremium) {
                                        viewModel.setPremium(false)
                                    } else {
                                        viewModel.setPremium(true)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            
            // Test Actions
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🧪 Test Aksiyonları",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Simulate Purchase Flow
                        Button(
                            onClick = { viewModel.simulatePurchaseFlow() },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = SpendCraftTheme.colors.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Satın Alma Akışını Taklit Et")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Reset Premium
                        OutlinedButton(
                            onClick = { viewModel.resetPremium() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Premium Durumunu Sıfırla")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Refresh Billing
                        OutlinedButton(
                            onClick = { viewModel.refreshBilling() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Billing Durumunu Yenile")
                        }
                    }
                }
            }
            
            // Debug Info
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ Debug Bilgileri",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val debugInfo = listOf(
                            "Build Type" to "Debug",
                            "Billing Status" to if (isLoading) "Yükleniyor..." else "Bağlı",
                            "Premium Status" to if (isPremium) "Aktif" else "Pasif",
                            "DataStore" to "PremiumStateDataStore"
                        )
                        
                        debugInfo.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key,
                                    style = SpendCraftTheme.typography.bodyMedium,
                                    color = SpendCraftTheme.colors.onSurfaceVariant
                                )
                                Text(
                                    text = value,
                                    style = SpendCraftTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SpendCraftTheme.colors.onSurface
                                )
                            }
                        }
                    }
                }
            }
            
            // Warning
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SpendCraftTheme.colors.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = SpendCraftTheme.colors.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Bu ekran sadece debug build'de görünür. Production'da kullanılamaz.",
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
