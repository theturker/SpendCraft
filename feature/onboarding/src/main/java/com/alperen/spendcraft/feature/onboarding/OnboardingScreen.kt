package com.alperen.spendcraft.feature.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: Painter,
    val backgroundColor: Color,
    val iconColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "SpendCraft'a Hoş Geldiniz",
            description = "Kişisel finans yönetiminizi kolaylaştıran akıllı asistanınız. Harcamalarınızı takip edin, bütçe oluşturun ve finansal hedeflerinize ulaşın.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
            backgroundColor = Color(0xFF6366F1),
            iconColor = Color.White
        ),
        OnboardingPage(
            title = "Akıllı Kategorilendirme",
            description = "İşlemlerinizi otomatik olarak kategorilere ayırın. Harcama alışkanlıklarınızı analiz edin ve nerede tasarruf edebileceğinizi keşfedin.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_category_vector),
            backgroundColor = Color(0xFF10B981),
            iconColor = Color.White
        ),
        OnboardingPage(
            title = "Bütçe Yönetimi",
            description = "Kategoriler için bütçe belirleyin ve harcamalarınızı kontrol altında tutun. Bütçe aşımlarında anında bildirim alın.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_pie_chart_vector),
            backgroundColor = Color(0xFFF59E0B),
            iconColor = Color.White
        ),
        OnboardingPage(
            title = "AI Destekli Öneriler",
            description = "Yapay zeka teknolojisi ile kişiselleştirilmiş finansal öneriler alın. Harcama alışkanlıklarınızı optimize edin.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
            backgroundColor = Color(0xFF8B5CF6),
            iconColor = Color.White
        ),
        OnboardingPage(
            title = "Detaylı Raporlar",
            description = "Gelir-gider analizleri, kategori bazında harcama dağılımları ve trend analizleri ile finansal durumunuzu takip edin.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_analytics_vector),
            backgroundColor = Color(0xFFEF4444),
            iconColor = Color.White
        ),
        OnboardingPage(
            title = "Premium Özellikler",
            description = "Sınırsız hesap, tekrarlayan işlemler, aile bütçesi paylaşımı ve gelişmiş AI analizleri için Premium'a geçin.",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_workspace_premium_vector),
            backgroundColor = Color(0xFF06B6D4),
            iconColor = Color.White
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinish) {
                    Text(
                        text = "Atla",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom section with indicators and navigation
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == pagerState.currentPage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (pagerState.currentPage == 0) {
                        Arrangement.End
                    } else {
                        Arrangement.SpaceBetween
                    }
                ) {
                    // Back button (hidden on first page)
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Geri")
                        }
                    }

                    // Next/Finish button
                    Button(
                        onClick = {
                            if (pagerState.currentPage == pages.lastIndex) {
                                onFinish()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            if (pagerState.currentPage == pages.lastIndex) "Başla" else "İleri"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            if (pagerState.currentPage == pages.lastIndex) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with floating animation and gradient background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .offset(y = floatingOffset.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            page.backgroundColor,
                            page.backgroundColor.copy(alpha = 0.8f)
                        )
                    ),
                    CircleShape
                )
        ) {
            Icon(
                page.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = page.iconColor
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
        )
    }
}

@Composable
fun OnboardingContainer(
    onOnboardingComplete: () -> Unit,
    content: @Composable () -> Unit
) {
    var showOnboarding by remember { mutableStateOf(true) }

    if (showOnboarding) {
        OnboardingScreen(
            onFinish = {
                showOnboarding = false
                onOnboardingComplete()
            }
        )
    } else {
        content()
    }
}
