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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val systemImage: String, // SF Symbol name
    val icon: Painter,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    // iOS'taki OnboardingView pages - birebir aynısı
    val pages = listOf(
        OnboardingPage(
            title = "Paratik'e Hoş Geldiniz",
            description = "Kişisel finans yönetiminizi kolaylaştıran akıllı asistanınız. Harcamalarınızı takip edin, bütçe oluşturun ve finansal hedeflerinize ulaşın.",
            systemImage = "chart.line.uptrend.xyaxis",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_chart_line_uptrend),
            gradientColors = listOf(Color(0xFF007AFF), Color(0xFFAF52DE)) // Blue to Purple
        ),
        OnboardingPage(
            title = "Akıllı Kategorilendirme",
            description = "İşlemlerinizi otomatik olarak kategorilere ayırın. Harcama alışkanlıklarınızı analiz edin ve nerede tasarruf edebileceğinizi keşfedin.",
            systemImage = "square.grid.2x2",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_folder_fill),
            gradientColors = listOf(Color(0xFF34C759), Color(0xFF00C7BE)) // Green to Mint
        ),
        OnboardingPage(
            title = "Bütçe Yönetimi",
            description = "Kategoriler için bütçe belirleyin ve harcamalarınızı kontrol altında tutun. Bütçe aşımlarında anında bildirim alın.",
            systemImage = "chart.pie.fill",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_pie_chart_vector),
            gradientColors = listOf(Color(0xFFFF9500), Color(0xFFFFCC00)) // Orange to Yellow
        ),
        OnboardingPage(
            title = "AI Destekli Öneriler",
            description = "Yapay zeka teknolojisi ile kişiselleştirilmiş finansal öneriler alın. Harcama alışkanlıklarınızı optimize edin.",
            systemImage = "sparkles",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_sparkles),
            gradientColors = listOf(Color(0xFFAF52DE), Color(0xFFFF2D55)) // Purple to Pink
        ),
        OnboardingPage(
            title = "Detaylı Raporlar",
            description = "Gelir-gider analizleri, kategori bazında harcama dağılımları ve trend analizleri ile finansal durumunuzu takip edin.",
            systemImage = "chart.bar.fill",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill),
            gradientColors = listOf(Color(0xFFFF3B30), Color(0xFFFF9500)) // Red to Orange
        ),
        OnboardingPage(
            title = "Hemen Başlayın",
            description = "Tüm özellikler sizin için hazır! Finansal özgürlük yolculuğunuza şimdi başlayın.",
            systemImage = "checkmark.circle.fill",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill),
            gradientColors = listOf(Color(0xFF32ADE6), Color(0xFF007AFF)) // Cyan to Blue
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    // iOS'taki background gradient - current page'e göre değişir
    val currentGradient = pages[pagerState.currentPage].gradientColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = currentGradient
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button (iOS'taki gibi - sadece son sayfa değilse göster)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = onFinish) {
                        Text(
                            text = "Atla",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
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
    // iOS'taki animated icon - scale ve alpha animation
    var isAnimating by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.0f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isAnimating) 1.0f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_alpha"
    )
    
    LaunchedEffect(Unit) {
        isAnimating = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Animated Icon - iOS'taki gibi
        Icon(
            page.icon,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                ),
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Title - iOS'taki gibi büyük ve bold, beyaz renk
        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description - iOS'taki gibi, beyaz renk, line spacing
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                lineHeight = 26.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.9f)
        )
        
        Spacer(modifier = Modifier.weight(1f))
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
