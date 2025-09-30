package com.alperen.spendcraft.core.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit = {}
) {
    var progress by remember { mutableStateOf(0f) }
    var isVisible by remember { mutableStateOf(false) }
    
    // Wallet icon pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    // Progress animation
    LaunchedEffect(Unit) {
        isVisible = true
        delay(100)
        
        // Animate progress from 0 to 100%
        progress = 1f
        
        // Wait for animation to complete
        delay(2500)
        onLoadingComplete()
    }
    
    // Background colors matching HTML - using dark theme
    val backgroundColor = Color(0xFF111121) // background-dark (HTML uses dark by default)
    val primaryColor = Color(0xFF474AEB) // primary
    val textColor = Color(0xFFFFFFFF) // white (dark theme)
    val subtitleColor = Color(0xFF94A3B8) // slate-400 (dark theme)
    val versionColor = Color(0xFF94A3B8) // slate-400 (dark theme)
    val progressBgColor = Color(0xFF475569) // slate-700 (dark theme)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Wallet Icon with pulse animation
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    WalletIcon(
                        modifier = Modifier.size(128.dp),
                        tint = primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title
                Text(
                    text = "SpendCraft",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtitle
                Text(
                    text = "Yükleniyor...",
                    fontSize = 18.sp,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            progressBgColor, // slate-700 (dark theme)
                            RoundedCornerShape(9999.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(
                                primaryColor,
                                RoundedCornerShape(9999.dp)
                            )
                            .animateContentSize(
                                animationSpec = tween(2500, easing = EaseOut)
                            )
                            .let { modifier ->
                                if (progress > 0f) {
                                    modifier.fillMaxWidth(progress)
                                } else {
                                    modifier.fillMaxWidth(0f)
                                }
                            }
                    )
                }
            }
            
            // Version text
            Text(
                text = "SpendCraft v1.0",
                fontSize = 14.sp,
                color = versionColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun WalletIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    androidx.compose.foundation.Image(
        painter = painterResource(com.alperen.spendcraft.R.drawable.ic_wallet),
        contentDescription = "Wallet Icon",
        modifier = modifier,
        colorFilter = if (tint != Color.Unspecified) {
            androidx.compose.ui.graphics.ColorFilter.tint(tint)
        } else null
    )
}
