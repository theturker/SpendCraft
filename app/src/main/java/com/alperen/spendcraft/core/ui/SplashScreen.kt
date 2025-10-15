package com.alperen.spendcraft.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.*
import kotlinx.coroutines.delay

/**
 * iOS SplashView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Blue to Purple gradient background
 * - Splash icon (180dp) with shadow
 * - "Paratik" text (42sp, bold, white)
 */
@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(1500) // iOS'taki 1.5 saniye
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        IOSColors.Blue,
                        IOSColors.Purple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Splash Icon - iOS'taki gibi 180x180 with shadow
            Image(
                painter = painterResource(com.alperen.spendcraft.R.drawable.splash),
                contentDescription = "Splash Icon",
                modifier = Modifier
                    .size(180.dp)
                    .shadow(
                        elevation = 12.dp,
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    )
            )
            
            // "Paratik" text - iOS'taki gibi
            Text(
                text = "Paratik",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}

@Preview(name = "Splash Screen")
@Composable
private fun SplashScreenPreview() {
    IOSTheme(darkTheme = false) {
        SplashScreen()
    }
}

