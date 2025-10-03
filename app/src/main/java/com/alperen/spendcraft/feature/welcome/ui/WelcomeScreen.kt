package com.alperen.spendcraft.feature.welcome.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
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
            // Top section with logo and title - better spacing
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                // Animated Star Logo with "S" in center
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating star background
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .rotate(rotation)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(55.dp)
                        )
                    }
                    
                    // "S" text overlay
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title - better spacing
                Text(
                    text = if (isTurkish) "SpendCraft'a Hoş Geldiniz" else "Welcome to SpendCraft",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle - better spacing
                Text(
                    text = if (isTurkish) "Finansal özgürlüğe ilk adımı atın." else "Take the first step towards financial freedom.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Features section with better spacing - 3x2 grid (3 rows, 2 columns each)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(cardAlpha)
                    .offset(y = cardOffset.dp)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // First row - 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.credit_card),
                        title = if (isTurkish) "Harcama Takibi" else "Expense Tracking",
                        primaryColor = primaryColor,
                        gradientColors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f)),
                        modifier = Modifier.weight(1f)
                    )
                    
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.bar_chart),
                        title = if (isTurkish) "Raporlar" else "Reports",
                        primaryColor = primaryColor,
                        gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Second row - 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.outline_account_balance_24),
                        title = if (isTurkish) "Bütçe" else "Budget",
                        primaryColor = primaryColor,
                        gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                        modifier = Modifier.weight(1f)
                    )
                    
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.outline_check_circle_24),
                        title = if (isTurkish) "Başarılar" else "Achievements",
                        primaryColor = primaryColor,
                        gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Third row - 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.outline_notifications_24),
                        title = if (isTurkish) "Hatırlatıcılar" else "Reminders",
                        primaryColor = primaryColor,
                        gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFFA855F7)),
                        modifier = Modifier.weight(1f)
                    )
                    
                    CompactFeatureCard(
                        icon = painterResource(R.drawable.outline_analytics_24),
                        title = if (isTurkish) "Analiz" else "Analytics",
                        primaryColor = primaryColor,
                        gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Beautiful elegant bottom button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Subtle background blur effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Elegant button with glass morphism
        Card(
            onClick = {
                kotlinx.coroutines.GlobalScope.launch {
                    firstLaunchHelper.setFirstLaunchCompleted()
                }
                onStart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = primaryColor.copy(alpha = 0.2f),
                    spotColor = primaryColor.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.9f),
                                primaryColor.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // Elegant icon with subtle animation
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = if (isTurkish) "Başlayalım" else "Let's Start",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactFeatureCard(
    icon: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    primaryColor: Color,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = gradientColors.first().copy(alpha = 0.2f),
                spotColor = gradientColors.first().copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(16.dp),
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
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Compact icon container
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(10.dp),
                            ambientColor = gradientColors.first().copy(alpha = 0.3f),
                            spotColor = gradientColors.first().copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Compact text content
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
