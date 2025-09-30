package com.alperen.spendcraft.feature.paywall

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard

/**
 * Paywall UI (Compose)
 * 
 * Şık, hızlı, sade bir ekran:
 * - Başlık, avantaj listesi (ikonlu maddeler)
 * - Aylık / Yıllık / Lifetime kartları (seçilebilir)
 * - Gerçek fiyatı ProductDetails'tan çek, skeleton göster
 * - CTA: "Şimdi Yükselt"
 * - Terms/Privacy link placeholder (metin buton)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onNavigateUp: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: PaywallViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val products by viewModel.products.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    
    var selectedProduct by remember { mutableStateOf("premium_yearly") }
    var showAIWeekly by remember { mutableStateOf(false) }
    
    // Success handling
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onSuccess()
        }
    }
    
    // Error handling
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Error will be shown in Snackbar
        }
    }
    
    AppScaffold(
        title = "⭐ Premium'a Yükselt",
        onBack = onNavigateUp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚀 SpendCraft Premium",
                            style = SpendCraftTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Tüm özelliklerin kilidini açın",
                            style = SpendCraftTheme.typography.bodyLarge,
                            color = SpendCraftTheme.colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Benefits
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "✨ Premium Avantajları",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val benefits = listOf(
                            "🚫 Reklamsız deneyim" to "Hiç reklam görmeden kullanın",
                            "🔄 Tekrarlayan işlemler" to "Otomatik gelir/gider ekleme",
                            "💰 Bütçe & limit uyarıları" to "Akıllı harcama takibi",
                            "📊 CSV/Excel/PDF dışa aktarım" to "Sınırsız veri aktarımı",
                            "👨‍👩‍👧‍👦 Aile/Ortak bütçe" to "Birlikte finansal hedefler"
                        )
                        
                        benefits.forEach { (title, description) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    style = SpendCraftTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SpendCraftTheme.colors.primary
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = description,
                                    style = SpendCraftTheme.typography.bodyMedium,
                                    color = SpendCraftTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // AI Weekly Upsell
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = SpendCraftTheme.colors.primary
                            )
                            Text(
                                text = "🤖 AI Haftalık Öneriler",
                                style = SpendCraftTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Premium olmadan da AI önerileri alın! Haftalık 1 AI analizi ile harcama alışkanlıklarınızı optimize edin.",
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "₺9,99/hafta",
                                    style = SpendCraftTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SpendCraftTheme.colors.primary
                                )
                                Text(
                                    text = "Tek seferlik satın alma",
                                    style = SpendCraftTheme.typography.bodySmall,
                                    color = SpendCraftTheme.colors.onSurfaceVariant
                                )
                            }
                            
                            Button(
                                onClick = { showAIWeekly = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SpendCraftTheme.colors.secondary
                                )
                            ) {
                                Text("AI Paketi Al")
                            }
                        }
                    }
                }
            }
            
            // Product Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💎 Planınızı Seçin",
                            style = SpendCraftTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SpendCraftTheme.colors.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val productOptions = listOf(
                            "premium_monthly" to "Aylık",
                            "premium_yearly" to "Yıllık (En Popüler)",
                            "ai_weekly" to "AI Haftalık Raporlar"
                        )
                        
                        productOptions.forEach { (productId, title) ->
                            val isSelected = selectedProduct == productId
                            val isPopular = productId == "premium_yearly"
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) 
                                        SpendCraftTheme.colors.primaryContainer 
                                    else 
                                        SpendCraftTheme.colors.surface
                                ),
                                border = if (isSelected) 
                                    androidx.compose.foundation.BorderStroke(
                                        2.dp, 
                                        SpendCraftTheme.colors.primary
                                    ) 
                                else null,
                                onClick = { selectedProduct = productId }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = title,
                                                style = SpendCraftTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SpendCraftTheme.colors.onSurface
                                            )
                                            
                                            if (isPopular) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = SpendCraftTheme.colors.primary
                                                    )
                                                ) {
                                                    Text(
                                                        text = "POPÜLER",
                                                        style = SpendCraftTheme.typography.labelSmall,
                                                        color = SpendCraftTheme.colors.onPrimary,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                        
                                        Text(
                                            text = viewModel.getProductDescription(productId),
                                            style = SpendCraftTheme.typography.bodyMedium,
                                            color = SpendCraftTheme.colors.onSurfaceVariant
                                        )
                                        
                                        Text(
                                            text = viewModel.getProductPrice(productId),
                                            style = SpendCraftTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = SpendCraftTheme.colors.primary
                                        )
                                    }
                                    
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedProduct = productId }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // CTA Button
            item {
                Button(
                    onClick = {
                        when (selectedProduct) {
                            "premium_monthly" -> viewModel.buyMonthly(context as android.app.Activity)
                            "premium_yearly" -> viewModel.buyYearly(context as android.app.Activity)
                            "ai_weekly" -> viewModel.buyAIWeekly(context as android.app.Activity)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpendCraftTheme.colors.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SpendCraftTheme.colors.onPrimary
                        )
                    } else {
                        Text(
                            text = "⭐ Premium'a Yükselt",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Terms & Privacy
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { 
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://theturker.github.io/SpendCraft/")))
                    }) {
                        Text("Kullanım Şartları")
                    }
                    
                    Text(
                        text = " • ",
                        color = SpendCraftTheme.colors.onSurfaceVariant
                    )
                    
                    TextButton(onClick = { 
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://theturker.github.io/SpendCraft/")))
                    }) {
                        Text("Gizlilik Politikası")
                    }
                }
            }
        }
    }
    
    // Error Snackbar
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar
            viewModel.clearError()
        }
    }
}
