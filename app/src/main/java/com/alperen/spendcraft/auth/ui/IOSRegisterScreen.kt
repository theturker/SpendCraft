package com.alperen.spendcraft.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.*
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS RegisterView Pixel-Perfect Implementation
 * 
 * iOS Source: iosApp/SpendCraftiOS/OnboardingView.swift:926-1357
 * Features:
 * - Green-Blue gradient background
 * - Password strength indicator (3-level: weak/medium/strong)
 * - Confirm password with checkmark validation
 * - Glass morphism design
 */
@Composable
fun IOSRegisterScreen(
    onRegisterClick: (name: String, email: String, password: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Form validation
    val isFormValid = remember(name, email, password, confirmPassword) {
        name.isNotEmpty() &&
                email.isNotEmpty() && email.contains("@") &&
                password.isNotEmpty() && password.length >= 6 &&
                confirmPassword.isNotEmpty() &&
                password == confirmPassword
    }
    
    val passwordsMatch = remember(password, confirmPassword) {
        password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
    }
    
    // Password strength calculation - iOS logic
    val passwordStrength = remember(password) {
        var strength = 0
        if (password.length >= 6) strength += 1
        if (password.length >= 8) strength += 1
        if (password.any { it.isDigit() } && password.any { it.isLetter() }) strength += 1
        strength
    }
    
    val strengthColor = when (passwordStrength) {
        0, 1 -> IOSColors.Red
        2 -> IOSColors.Orange
        3 -> IOSColors.Green
        else -> Color.Gray
    }
    
    val strengthText = when (passwordStrength) {
        0, 1 -> "Zayıf"
        2 -> "Orta"
        3 -> "Güçlü"
        else -> ""
    }
    
    // Staggered fade-in animation
    var animateContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateContent = true
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background gradient - iOS: Green/Blue blended at 0.15 alpha
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
                            Color(0xFF30D158).copy(alpha = 0.15f),  // Green
                            Color(0xFF4066FF).copy(alpha = 0.15f),  // Blue  
                            Color(0xFF9966CC).copy(alpha = 0.15f)   // Purple
                        )
                    )
                )
        )
        
        // Floating blur circles - iOS style
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-140).dp, y = (-180).dp)
                .clip(CircleShape)
                .background(IOSColors.Green.copy(alpha = 0.1f))
                .blur(50.dp)
        )
        
        Box(
            modifier = Modifier
                .size(230.dp)
                .offset(x = 140.dp, y = 280.dp)
                .clip(CircleShape)
                .background(IOSColors.Blue.copy(alpha = 0.1f))
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
            Spacer(modifier = Modifier.height(50.dp))
            
            // Glass morphism logo container
            AnimatedVisibility(
                visible = animateContent,
                enter = scaleIn(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    initialScale = 0.8f
                ) + fadeIn(animationSpec = tween(300))
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
                                        IOSColors.Green.copy(alpha = 0.3f),
                                        IOSColors.Blue.copy(alpha = 0.3f)
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
                            imageVector = SFSymbols.PersonFill,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp),
                            tint = IOSColors.Green
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            AnimatedVisibility(
                visible = animateContent,
                enter = slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    initialOffsetY = { 20 }
                ) + fadeIn(animationSpec = tween(300))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hesap Oluştur",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Finansal özgürlük yolculuğunuza başlayın",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
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
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Name Field
                        IOSRegisterInputField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Ad Soyad",
                            placeholder = "Adınız ve Soyadınız",
                            leadingIcon = Icons.Filled.Person,
                            animationDelay = 100
                        )
                        
                        // Email Field
                        IOSRegisterInputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "E-posta",
                            placeholder = "ornek@email.com",
                            leadingIcon = SFSymbols.EnvelopeFill,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            animationDelay = 200
                        )
                        
                        // Password Field with strength indicator
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            IOSRegisterInputField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Şifre",
                                placeholder = "En az 6 karakter",
                                leadingIcon = SFSymbols.LockFill,
                                trailingIcon = null,  // Visibility icons removed for simplicity
                                onTrailingIconClick = { passwordVisible = !passwordVisible },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                animationDelay = 300
                            )
                            
                            // Password strength indicator - iOS style
                            if (password.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(3) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(3.dp)
                                                    .clip(RoundedCornerShape(4.dp))  // IOSRadius.radius4
                                                    .background(
                                                        if (passwordStrength > index) strengthColor
                                                        else Color.Gray.copy(alpha = 0.3f)
                                                    )
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = strengthText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = strengthColor
                                    )
                                }
                            }
                        }
                        
                        // Confirm Password Field with checkmark
                        IOSRegisterInputField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Şifre Tekrar",
                            placeholder = "Şifrenizi tekrar girin",
                            leadingIcon = if (passwordsMatch) SFSymbols.CheckmarkCircleFill else SFSymbols.LockFill,
                            leadingIconTint = if (passwordsMatch) IOSColors.Green else null,
                            trailingIcon = null,  // Password visibility toggle removed
                            onTrailingIconClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            showSuccessBorder = passwordsMatch,
                            animationDelay = 400
                        )
                        
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
                        
                        // Register Button
                        Button(
                            onClick = {
                                if (!isLoading && isFormValid) {
                                    onRegisterClick(name, email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !isLoading && isFormValid,
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
                                        if (!isFormValid || isLoading) {
                                            Brush.linearGradient(
                                                colors = listOf(Color.Gray, Color.Gray.copy(alpha = 0.8f))
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(IOSColors.Green, IOSColors.Blue)
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
                                            text = "Hesap Oluştur",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                        Icon(
                                            imageVector = SFSymbols.CheckmarkCircleFill,
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            AnimatedVisibility(
                visible = animateContent,
                enter = fadeIn(animationSpec = tween(300, delayMillis = 300))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "veya",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Login link
            AnimatedVisibility(
                visible = animateContent,
                enter = fadeIn(animationSpec = tween(300, delayMillis = 400))
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
                        Text(
                            text = "Zaten hesabınız var mı?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Giriş Yap",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                brush = Brush.linearGradient(
                                    colors = listOf(IOSColors.Green, IOSColors.Blue)
                                )
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IOSRegisterInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    leadingIconTint: Color? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showSuccessBorder: Boolean = false,
    animationDelay: Int = 0
) {
    var animateField by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        animateField = true
    }
    
    AnimatedVisibility(
        visible = animateField,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            initialOffsetY = { 20 }
        ) + fadeIn()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconTint ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                trailingIcon = if (trailingIcon != null) {
                    {
                        IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else null,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = true,
                shape = IOSRadius.button,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = if (showSuccessBorder) IOSColors.Green else IOSColors.Blue,
                    unfocusedBorderColor = if (showSuccessBorder) IOSColors.Green else Color.Transparent
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Light")
@Composable
private fun IOSRegisterScreenPreview() {
    IOSTheme(darkTheme = false) {
        IOSRegisterScreen(
            onRegisterClick = { _: String, _: String, _: String -> },
            onNavigateToLogin = {}
        )
    }
}

