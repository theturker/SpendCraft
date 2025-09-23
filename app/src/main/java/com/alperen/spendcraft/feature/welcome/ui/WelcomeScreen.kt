package com.alperen.spendcraft.feature.welcome.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.R
import com.alperen.spendcraft.core.ui.ActionButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.FirstLaunchHelper
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onStart: () -> Unit
) {
    val context = LocalContext.current
    var animationTriggered by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animationTriggered = true
    }
    
    // Get current language
    val currentLanguage = LocaleHelper.getLanguage(context)
    val isTurkish = currentLanguage == "tr"
    
    // Handle first launch completion
    val firstLaunchHelper = remember { FirstLaunchHelper(context) }

    val scale by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0.8f,
        animationSpec = tween(800),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = if (isTurkish) "SpendCraft'a Hoş Geldiniz!" else "Welcome to SpendCraft!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = if (isTurkish) "Harcamalarınızı takip edin, bütçenizi yönetin ve finansal hedeflerinize ulaşın" 
                       else "Track your expenses, manage your budget and achieve your financial goals",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Features List
            Column(
                modifier = Modifier.alpha(alpha),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureItem(
                    icon = "💰",
                    title = if (isTurkish) "Harcama Takibi" else "Expense Tracking",
                    description = if (isTurkish) "Gelir ve giderlerinizi kolayca kaydedin" else "Easily record your income and expenses"
                )
                FeatureItem(
                    icon = "📊",
                    title = if (isTurkish) "Detaylı Raporlar" else "Detailed Reports",
                    description = if (isTurkish) "Harcama analizleri ve trend grafikleri" else "Expense analysis and trend charts"
                )
                FeatureItem(
                    icon = "🎯",
                    title = if (isTurkish) "Bütçe Yönetimi" else "Budget Management",
                    description = if (isTurkish) "Kategori bazında bütçe limitleri belirleyin" else "Set budget limits by category"
                )
                FeatureItem(
                    icon = "🏆",
                    title = if (isTurkish) "Başarı Takibi" else "Achievement Tracking",
                    description = if (isTurkish) "Günlük seriler ve rozetler kazanın" else "Earn daily streaks and badges"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Start Button
            ActionButton(
                text = if (isTurkish) "Başla" else "Start",
                onClick = {
                    // Mark first launch as completed
                    kotlinx.coroutines.GlobalScope.launch {
                        firstLaunchHelper.setFirstLaunchCompleted()
                    }
                    onStart()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
