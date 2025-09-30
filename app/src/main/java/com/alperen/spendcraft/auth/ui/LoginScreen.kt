package com.alperen.spendcraft.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperen.spendcraft.R
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.ThemeHelper
import com.alperen.spendcraft.LocaleHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onGoogleSignInResult: (GoogleSignInAccount?) -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = true)

    val currentLanguage = LocaleHelper.getLanguage(context)
    val isTurkish = currentLanguage == "tr"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Renkler
    val backgroundColor = if (isDarkMode) Color(0xFF111321) else Color(0xFFF6F6F8)
    val primaryColor = Color(0xFF4C5EE6)
    val onPrimary = Color.White
    val titleColor = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val cardBackground = if (isDarkMode) Color(0xFF181B28) else Color.White.copy(alpha = 0.97f)

    // Tipografi
    val h1 = MaterialTheme.typography.headlineLarge.copy(
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = titleColor,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp
    )
    val subtitle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        color = subtitleColor,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val buttonText = MaterialTheme.typography.titleMedium.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.2.sp
    )
    val linkText = MaterialTheme.typography.bodySmall.copy(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 26.dp)
            ) {
                Text(
                    text = "SpendCraft",
                    style = h1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isTurkish) "Hoş geldiniz! Hesabınıza giriş yapın."
                    else "Welcome back! Please sign in to your account.",
                    style = subtitle,
                    textAlign = TextAlign.Center
                )
            }

            // ====== Modern Card Design ======
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = primaryColor.copy(alpha = 0.1f),
                        spotColor = primaryColor.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                if (isTurkish) "E-posta adresi" else "Email address",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor.copy(alpha = 0.95f),
                            unfocusedBorderColor = if (isDarkMode) Color(0xFF2A2E3B) else Color(0xFFE5E7EB),
                            focusedContainerColor = if (isDarkMode) Color(0xFF0E0F15) else Color(0xFFF7F7FB),
                            unfocusedContainerColor = if (isDarkMode) Color(0xFF0E0F15) else Color(0xFFF7F7FB),
                            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            cursorColor = primaryColor
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            )
                        }
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                if (isTurkish) "Şifre" else "Password",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisible) R.drawable.outline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor.copy(alpha = 0.95f),
                            unfocusedBorderColor = if (isDarkMode) Color(0xFF2A2E3B) else Color(0xFFE5E7EB),
                            focusedContainerColor = if (isDarkMode) Color(0xFF0E0F15) else Color(0xFFF7F7FB),
                            unfocusedContainerColor = if (isDarkMode) Color(0xFF0E0F15) else Color(0xFFF7F7FB),
                            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            cursorColor = primaryColor
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            )
                        }
                    )

                    // Hata Mesajı
                    if (!errorMessage.isNullOrEmpty()) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp)
                        )
                    }

                    // Şifremi Unuttum
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onNavigateToForgotPassword,
                            colors = ButtonDefaults.textButtonColors(contentColor = primaryColor),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isTurkish) "Şifremi Unuttum" else "Forgot password?",
                                style = linkText
                            )
                        }
                    }

                    // Modern Giriş Yap Butonu
                    Card(
                        onClick = { 
                            if (!isLoading && email.isNotEmpty() && password.isNotEmpty()) {
                                authViewModel.signIn(email, password)
                            }
                        },
                        enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = if (isTurkish) "Giriş Yap" else "Sign In",
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

                    // Divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = if (isDarkMode) Color(0xFF2E3342) else Color(0xFFD1D5DB)
                        )
                        Text(
                            text = if (isTurkish) "veya" else "Or continue with",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = subtitleColor,
                                letterSpacing = 0.1.sp
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = if (isDarkMode) Color(0xFF2E3342) else Color(0xFFD1D5DB)
                        )
                    }

                    // Google ile giriş - sadece logo
                    Card(
                        onClick = { onGoogleSignInResult(null) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color.Black.copy(alpha = 0.1f),
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkMode) Color(0xFF1A1D29) else Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isDarkMode) 
                                        Color(0xFF1A1D29) 
                                    else 
                                        Color.White,
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDarkMode) Color(0xFF2E3342) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = if (isTurkish) "Google ile giriş yap" else "Sign in with Google",
                                modifier = Modifier.size(28.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
            // ====== /Kart ======

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = if (isTurkish) "Hesabınız yok mu?" else "Don’t have an account?",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = subtitleColor,
                    letterSpacing = 0.1.sp
                )
            )
            TextButton(
                onClick = onNavigateToRegister,
                colors = ButtonDefaults.textButtonColors(contentColor = primaryColor),
            ) {
                Text(
                    text = if (isTurkish) "Kayıt Ol" else "Sign Up",
                    style = linkText
                )
            }
        }
    }
}

/**
 * Glow + (opsiyonel) gerçek drop shadow + gradient border.
 * - addDropShadow = true iken dış sarmalayıcıya .shadow(...) ekler (clip=false).
 * - Koyu temada düşük blur + düşük alpha, açıkta biraz daha belirgin.
 */
@Composable
private fun SoftShadowCard(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    primaryColor: Color,
    containerColor: Color,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    elevationLight: Dp = 12.dp,
    elevationDark: Dp = 3.dp,
    addDropShadow: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    // Dış sarıcı: gerçek drop shadow (clip=false ki gölge kesilmesin)
    val wrapper = if (addDropShadow) {
        modifier.shadow(
            elevation = if (isDarkMode) 10.dp else 18.dp,
            shape = shape,
            clip = false
        )
    } else modifier

    Box(
        modifier = wrapper,
        contentAlignment = Alignment.Center
    ) {
        // Alt düşüş gölgesi (arka planda geniş blur)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(140.dp)
                .offset(y = 18.dp)
                .blur(if (isDarkMode) 38.dp else 52.dp)
                .background(
                    color = if (isDarkMode) Color.Black.copy(0.26f) else Color.Black.copy(0.12f),
                    shape = shape
                )
        )

        // Üst ışıma (primary’den hafif glow)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(112.dp)
                .offset(y = (-6).dp)
                .blur(if (isDarkMode) 44.dp else 56.dp)
                .background(
                    color = primaryColor.copy(alpha = if (isDarkMode) 0.14f else 0.10f),
                    shape = shape
                )
        )

        // Asıl kart + gradient border (Modifier.border ile)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isDarkMode) 1.dp else 0.8.dp,
                    brush = Brush.linearGradient(
                        if (isDarkMode)
                            listOf(Color(0x26FFFFFF), Color(0x1AFFFFFF))
                        else
                            listOf(Color(0x14000000), Color(0x0F000000))
                    ),
                    shape = shape
                )
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape),
                shape = shape,
                colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isDarkMode) elevationDark else elevationLight
                )
            ) {
                Column(content = content)
            }
        }
    }
}
