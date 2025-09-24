package com.alperen.spendcraft.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    
    // Get current language
    val currentLanguage = LocaleHelper.getLanguage(context)
    val isTurkish = currentLanguage == "tr"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // HTML exact colors
    val backgroundColor = if (isDarkMode) Color(0xFF111321) else Color(0xFFF6F6F8)
    val primaryColor = Color(0xFF4C5EE6)
    val cardBackground = if (isDarkMode) Color(0xFF111321).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f)
    
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
            // Header Section - HTML exact match
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "SpendCraft",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF111827)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isTurkish) "Hoş geldiniz! Hesabınıza giriş yapın." else "Welcome back! Please sign in to your account.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
            
            // Login Form Card - HTML exact match with backdrop-blur effect
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = primaryColor.copy(alpha = if (isDarkMode) 0.2f else 0.1f),
                        spotColor = primaryColor.copy(alpha = if (isDarkMode) 0.2f else 0.1f)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Email Field - HTML exact match
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { 
                            Text(
                                if (isTurkish) "E-posta Adresi" else "Email Address",
                                color = if (isDarkMode) Color(0xFF71717A) else Color(0xFFA1A1AA)
                            ) 
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = if (isDarkMode) Color.Black.copy(alpha = 0.2f) else Color(0xFFF6F6F8),
                            unfocusedContainerColor = if (isDarkMode) Color.Black.copy(alpha = 0.2f) else Color(0xFFF6F6F8),
                            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Password Field - HTML exact match
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { 
                            Text(
                                if (isTurkish) "Şifre" else "Password",
                                color = if (isDarkMode) Color(0xFF71717A) else Color(0xFFA1A1AA)
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
                                    tint = if (isDarkMode) Color(0xFF71717A) else Color(0xFFA1A1AA)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = if (isDarkMode) Color.Black.copy(alpha = 0.2f) else Color(0xFFF6F6F8),
                            unfocusedContainerColor = if (isDarkMode) Color.Black.copy(alpha = 0.2f) else Color(0xFFF6F6F8),
                            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827),
                            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF111827)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Error Message
                    if (errorMessage != null && errorMessage!!.isNotEmpty()) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Sign In Button - HTML exact match
                    Button(
                        onClick = { authViewModel.signIn(email, password) },
                        enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(8.dp),
                                ambientColor = primaryColor.copy(alpha = 0.4f),
                                spotColor = primaryColor.copy(alpha = 0.4f)
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isTurkish) "Giriş Yap" else "Sign In",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Divider - HTML exact match
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = if (isDarkMode) Color(0xFF374151) else Color(0xFFD1D5DB)
                        )
                        Text(
                            text = if (isTurkish) "veya" else "Or continue with",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = if (isDarkMode) Color(0xFF374151) else Color(0xFFD1D5DB)
                        )
                    }
                    
                    // Google Sign-In Button - HTML exact match
                    OutlinedButton(
                        onClick = { onGoogleSignInResult(null) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.dp,
                            color = if (isDarkMode) Color(0xFF374151) else Color(0xFFD1D5DB)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isDarkMode) Color(0xFF111321) else Color(0xFFF6F6F8),
                            contentColor = if (isDarkMode) Color(0xFFE5E7EB) else Color(0xFF374151)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Google SVG icon equivalent
                            Icon(
                                imageVector = Icons.Filled.Email, // Using Email icon as Google placeholder
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isTurkish) "Google ile Giriş Yap" else "Sign in with Google",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Sign Up Link - HTML exact match
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isTurkish) "Hesabınız yok mu? " else "Don't have an account? ",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            )
            
            TextButton(
                onClick = onNavigateToRegister,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text(
                    text = if (isTurkish) "Kayıt Ol" else "Sign Up",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
