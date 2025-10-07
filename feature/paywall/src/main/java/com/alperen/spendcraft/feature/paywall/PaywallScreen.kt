package com.alperen.spendcraft.feature.paywall

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard

@Composable
private fun BenefitItem(
    leading: @Composable () -> Unit,
    title: String,
    desc: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = color,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                leading()
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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
                .padding(16.dp)
                .consumeWindowInsets(WindowInsets.systemBars),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                ModernCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_premium_crown),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Paratik Premium",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Tüm özelliklerin kilidini açın",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // Benefits (richer visuals)
            item {
                ModernCard {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // subtle shine overlay
                        val infinite = rememberInfiniteTransition(label = "benefits-shine")
                        val x = infinite.animateFloat(
                            initialValue = -120f,
                            targetValue = 520f,
                            animationSpec = infiniteRepeatable(animation = tween(2600, easing = LinearEasing)),
                            label = "x"
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "✨ Premium Avantajları",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                BenefitItem(
                                    leading = {
                                        Icon(
                                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_eye_off_vector),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    },
                                    title = "Reklamsız deneyim",
                                    desc = "Hiç reklam görmeden kullanın",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                BenefitItem(
                                    leading = {
                                        Icon(
                                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_repeat_vector),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    },
                                    title = "Tekrarlayan işlemler",
                                    desc = "Otomatik gelir/gider ekleme",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                BenefitItem(
                                    leading = {
                                        Icon(
                                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_notifications_vector),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    },
                                    title = "Bütçe & limit uyarıları",
                                    desc = "Akıllı harcama takibi",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                BenefitItem(
                                    leading = {
                                        Icon(
                                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_assignment_vector),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    },
                                    title = "Dışa aktarım",
                                    desc = "CSV/Excel/PDF aktarımı",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                BenefitItem(
                                    leading = {
                                        Icon(
                                            painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_group_vector),
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    },
                                    title = "Aile/Ortak bütçe",
                                    desc = "Birlikte finansal hedefler",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                                    .offset(x = x.value.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color.White.copy(alpha = 0.14f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "🤖 AI Haftalık Öneriler",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Premium olmadan da AI önerileri alın! Haftalık 1 AI analizi ile harcama alışkanlıklarınızı optimize edin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TRY 10,99/hafta",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tek seferlik satın alma",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Button(
                                onClick = { showAIWeekly = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val productOptions = listOf(
                            Triple("premium_monthly", "Aylık", painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_plan_month)),
                            Triple("premium_yearly", "Yıllık (En Popüler)", painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_plan_year)),
                            Triple("premium_lifetime", "Yaşam Boyu", painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_plan_lifetime)),
                            Triple("ai_weekly", "AI Haftalık Raporlar", painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_plan_ai))
                        )
                        
                        productOptions.forEach { (productId, title, iconPainter) ->
                            val isSelected = selectedProduct == productId
                            val isPopular = productId == "premium_yearly"
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) 
                                        MaterialTheme.colorScheme.primaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) 
                                    androidx.compose.foundation.BorderStroke(
                                        2.dp, 
                                        MaterialTheme.colorScheme.primary
                                    ) 
                                else null,
                                onClick = { selectedProduct = productId }
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    painter = iconPainter,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isPopular) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Card(
                                                        shape = RoundedCornerShape(999.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                        ),
                                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                                    ) {
                                                        Text(
                                                            text = "POPÜLER",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = viewModel.getProductDescription(productId),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = viewModel.getProductPrice(productId),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedProduct = productId }
                                        )
                                    }
                                    if (isPopular) {
                                        val infinite = rememberInfiniteTransition(label = "popular-shine")
                                        val x = infinite.animateFloat(
                                            initialValue = -120f,
                                            targetValue = 520f,
                                            animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing)),
                                            label = "x"
                                        )
                                        Box(modifier = Modifier.matchParentSize()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(60.dp)
                                                    .offset(x = x.value.dp)
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(
                                                                Color.Transparent,
                                                                Color.White.copy(alpha = 0.25f),
                                                                Color.Transparent
                                                            )
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // CTA Button
            item {
                val shape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(shape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                ) {
                    Button(
                        onClick = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            android.util.Log.d("PaywallScreen", "Launching purchase for: $selectedProduct")
                            when (selectedProduct) {
                                "premium_monthly" -> viewModel.buyMonthly(activity)
                                "premium_yearly" -> viewModel.buyYearly(activity)
                                "premium_lifetime" -> viewModel.buyLifetime(activity)
                                "ai_weekly" -> viewModel.buyAIWeekly(activity)
                            }
                        } else {
                            android.util.Log.e("PaywallScreen", "Context is not an Activity!")
                        }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_premium_crown),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Premium'a Yükselt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
