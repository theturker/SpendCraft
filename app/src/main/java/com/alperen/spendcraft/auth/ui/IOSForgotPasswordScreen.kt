package com.alperen.spendcraft.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.alperen.spendcraft.ui.icons.SFSymbols
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.*
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS ForgotPasswordView Pixel-Perfect Implementation
 * 
 * iOS Source: iosApp/SpendCraftiOS/OnboardingView.swift:1359-1742
 * Features:
 * - Orange-Purple gradient background
 * - Success state with animated icon
 * - Info box with helpful text
 */
@Composable
fun IOSForgotPasswordScreen(
    onSendResetLink: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean = false,
    isSuccess: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    
    var animateContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateContent = true
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background gradient - iOS: Orange/Purple/Blue at 0.15 alpha
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF8C42).copy(alpha = 0.15f),  // Orange
                            Color(0xFFB366CC).copy(alpha = 0.15f),  // Purple
                            Color(0xFF5A7FFF).copy(alpha = 0.15f)   // Blue
                        )
                    )
                )
        )
        
        // Floating blur circles
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-130).dp, y = (-160).dp)
                .clip(CircleShape)
                .background(IOSColors.Orange.copy(alpha = 0.1f))
                .blur(50.dp)
        )
        
        Box(
            modifier = Modifier
                .size(210.dp)
                .offset(x = 130.dp, y = 260.dp)
                .clip(CircleShape)
                .background(IOSColors.Purple.copy(alpha = 0.1f))
                .blur(50.dp)
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            if (isSuccess) {
                // Success State - iOS style
                AnimatedVisibility(
                    visible = animateContent,
                    enter = scaleIn(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        initialScale = 0.5f
                    ) + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Success icon with glass morphism
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(140.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                IOSColors.Green.copy(alpha = 0.2f),
                                                IOSColors.Green.copy(alpha = 0.1f)
                                            )
                                        )
                                    )
                                    .blur(20.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = SFSymbols.EnvelopeFill,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = IOSColors.Green
                                )
                            }
                        }
                        
                        // Success badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = SFSymbols.CheckmarkCircleFill,
                                contentDescription = null,
                                tint = IOSColors.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Tamamlandı!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = IOSColors.Green
                            )
                        }
                        
                        Text(
                            text = "E-posta Gönderildi",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            text = "E-posta adresinize şifre sıfırlama bağlantısı gönderildi. Lütfen gelen kutunuzu kontrol edin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        
                        // Back to login button
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            shape = IOSRadius.button
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(IOSColors.Green, IOSColors.Blue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Giriş Sayfasına Dön",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                        Icon(
                                            imageVector = SFSymbols.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                }
                            }
                        }
                        
                        Text(
                            text = "E-posta gelmediyse spam klasörünü kontrol edin",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Form State
                AnimatedVisibility(
                    visible = animateContent,
                    enter = scaleIn(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        initialScale = 0.8f
                    ) + fadeIn()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            IOSColors.Orange.copy(alpha = 0.3f),
                                            IOSColors.Purple.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                                .blur(20.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = SFSymbols.LockFill,
                                contentDescription = null,
                                modifier = Modifier.size(45.dp),
                                tint = IOSColors.Orange
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedVisibility(
                    visible = animateContent,
                    enter = slideInVertically(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        initialOffsetY = { 20 }
                    ) + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Şifrenizi mi Unuttunuz?",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            text = "E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Form card
                AnimatedVisibility(
                    visible = animateContent,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 100))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = IOSRadius.modal,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Email Field
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = SFSymbols.EnvelopeFill,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "E-posta Adresi",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = "ornek@email.com",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = SFSymbols.EnvelopeFill,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    shape = IOSRadius.button,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedBorderColor = IOSColors.Orange,
                                        unfocusedBorderColor = Color.Transparent
                                    )
                                )
                            }
                            
                            // Error message
                            if (errorMessage != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(IOSRadius.medium)
                                        .background(IOSColors.Red.copy(alpha = 0.1f))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                Icon(
                                    imageVector = SFSymbols.ExclamationmarkCircleFill,
                                    contentDescription = null,
                                    tint = IOSColors.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                    Text(
                                        text = errorMessage,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = IOSColors.Red
                                    )
                                }
                            }
                            
                            // Info box - iOS style
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(IOSRadius.medium)
                                    .background(IOSColors.Blue.copy(alpha = 0.1f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = SFSymbols.InfoCircleFill,
                                    contentDescription = null,
                                    tint = IOSColors.Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Kayıtlı e-posta adresinizi kullanın",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Bağlantı 24 saat geçerli olacaktır",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Send button
                            Button(
                                onClick = {
                                    if (!isLoading && email.isNotEmpty()) {
                                        onSendResetLink(email)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !isLoading && email.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Gray
                                ),
                                shape = IOSRadius.button
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (isLoading || email.isEmpty()) {
                                                Brush.linearGradient(
                                                    colors = listOf(Color.Gray, Color.Gray.copy(alpha = 0.8f))
                                                )
                                            } else {
                                                Brush.linearGradient(
                                                    colors = listOf(IOSColors.Orange, IOSColors.Purple)
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Sıfırlama Bağlantısı Gönder",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color.White
                                            )
                                            Icon(
                                                imageVector = SFSymbols.PaperplaneFill,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Back to login link
                AnimatedVisibility(
                    visible = animateContent,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 200))
                ) {
                    OutlinedButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .height(50.dp),
                        shape = IOSRadius.capsule,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = null
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = SFSymbols.ArrowBackward,
                                contentDescription = null,
                                tint = IOSColors.Orange,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Giriş Sayfasına Dön",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    brush = Brush.linearGradient(
                                        colors = listOf(IOSColors.Orange, IOSColors.Purple)
                                    )
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(name = "Forgot Password - Form")
@Composable
private fun IOSForgotPasswordScreenPreview() {
    IOSTheme(darkTheme = false) {
        IOSForgotPasswordScreen(
            onSendResetLink = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(name = "Forgot Password - Success")
@Composable
private fun IOSForgotPasswordSuccessPreview() {
    IOSTheme(darkTheme = false) {
        IOSForgotPasswordScreen(
            onSendResetLink = {},
            onNavigateToLogin = {},
            isSuccess = true
        )
    }
}

@Preview(name = "Forgot Password - Dark")
@Composable
private fun IOSForgotPasswordDarkPreview() {
    IOSTheme(darkTheme = true) {
        IOSForgotPasswordScreen(
            onSendResetLink = {},
            onNavigateToLogin = {}
        )
    }
}

