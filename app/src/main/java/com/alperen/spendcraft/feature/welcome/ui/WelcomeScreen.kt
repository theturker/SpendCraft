package com.alperen.spendcraft.feature.welcome.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.R
import com.alperen.spendcraft.core.ui.ActionButton
import androidx.compose.ui.platform.LocalContext
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.FirstLaunchHelper
import com.alperen.spendcraft.ThemeHelper
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
    
    // Get dark mode state
    val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = false)
    
    // Handle first launch completion
    val firstLaunchHelper = remember { FirstLaunchHelper(context) }

    // Star rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "star_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Card fade-in animations
    val cardAlpha by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(500),
        label = "card_alpha"
    )

    val cardOffset by animateFloatAsState(
        targetValue = if (animationTriggered) 0f else 20f,
        animationSpec = tween(500),
        label = "card_offset"
    )

    // Görseldeki gibi koyu mavi arka plan
    val backgroundColor = Color(0xFF111321)
    val primaryColor = Color(0xFF4C5EE6)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Gradient overlay (görseldeki gibi)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            backgroundColor
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f, 1f)
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with logo and title - moved up
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                // Animated Star Logo with "S" in center - smaller
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating star background
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .rotate(rotation)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    
                    // "S" text overlay
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title - smaller font
                Text(
                    text = if (isTurkish) "SpendCraft'a Hoş Geldiniz" else "Welcome to SpendCraft",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle - smaller font
                Text(
                    text = if (isTurkish) "Finansal özgürlüğe ilk adımı atın." else "Take the first step towards financial freedom.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Scrollable Features section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(cardAlpha)
                    .offset(y = cardOffset.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                BeautifulFeatureCard(
                    icon = painterResource(R.drawable.credit_card),
                    title = if (isTurkish) "Harcama Takibi" else "Expense Tracking",
                    description = if (isTurkish) "Giderlerinizi kolayca takip edin ve finansal durumunuzu kontrol altında tutun." else "Easily track your expenses and keep your financial situation under control.",
                    primaryColor = primaryColor,
                    gradientColors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                )
                
                BeautifulFeatureCard(
                    icon = painterResource(R.drawable.bar_chart),
                    title = if (isTurkish) "Detaylı Raporlar" else "Detailed Reports",
                    description = if (isTurkish) "Harcama analizleri ve trend grafikleri ile finansal durumunuzu anlayın." else "Understand your financial situation with expense analysis and trend charts.",
                    primaryColor = primaryColor,
                    gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                )
                
                BeautifulFeatureCard(
                    icon = painterResource(R.drawable.outline_account_balance_24),
                    title = if (isTurkish) "Bütçe Yönetimi" else "Budget Management",
                    description = if (isTurkish) "Kategori bazında bütçe limitleri belirleyin ve hedeflerinize ulaşın." else "Set budget limits by category and reach your goals.",
                    primaryColor = primaryColor,
                    gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669))
                )
                
                BeautifulFeatureCard(
                    icon = painterResource(R.drawable.outline_check_circle_24),
                    title = if (isTurkish) "Başarı Takibi" else "Achievement Tracking",
                    description = if (isTurkish) "Günlük seriler ve rozetler kazanarak finansal başarılarınızı kutlayın." else "Celebrate your financial achievements by earning daily streaks and badges.",
                    primaryColor = primaryColor,
                    gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Beautiful sticky bottom button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                kotlinx.coroutines.GlobalScope.launch {
                    firstLaunchHelper.setFirstLaunchCompleted()
                }
                onStart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = primaryColor.copy(alpha = 0.3f),
                    spotColor = primaryColor.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTurkish) "Başlayalım" else "Let's Start",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BeautifulFeatureCard(
    icon: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    description: String,
    primaryColor: Color,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = gradientColors.first().copy(alpha = 0.2f),
                spotColor = gradientColors.first().copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF111321).copy(alpha = 0.9f),
                            Color(0xFF1F2937).copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Beautiful icon container with gradient - smaller
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = gradientColors.first().copy(alpha = 0.3f),
                            spotColor = gradientColors.first().copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Enhanced text content - smaller fonts
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = Color(0xFF9CA3AF),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
