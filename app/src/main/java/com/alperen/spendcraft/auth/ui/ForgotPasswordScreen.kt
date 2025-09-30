package com.alperen.spendcraft.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperen.spendcraft.R
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.ThemeHelper
import com.alperen.spendcraft.LocaleHelper

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = true)
    val isTurkish = LocaleHelper.getLanguage(context) == "tr"
    
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Renkler
    val backgroundColor = if (isDarkMode) Color(0xFF111321) else Color(0xFFF6F6F8)
    val primaryColor = Color(0xFF4C5EE6)
    val cardBackground = if (isDarkMode) Color(0xFF181B28) else Color.White.copy(alpha = 0.97f)
    val titleColor = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = if (isTurkish) "Geri" else "Back",
                            tint = if (isDarkMode) Color.White else Color(0xFF374151),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Header section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock_reset),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = if (isTurkish) "Şifremi Unuttum" else "Forgot Password",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = titleColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isTurkish) 
                        "E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim" 
                    else 
                        "Enter your email address and we'll send you a password reset link",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }
            
            if (!emailSent) {
                // Modern Card
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
                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = {
                                Text(
                                    if (isTurkish) "E-posta adresi" else "Email address",
                                    color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                                )
                            },
                            leadingIcon = { 
                                Icon(
                                    Icons.Filled.Email, 
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
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
                            singleLine = true
                        )
                        
                        // Error message
                        if (errorMessage != null && errorMessage!!.isNotEmpty()) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                        
                        // Beautiful Send Button
                        Button(
                            onClick = {
                                if (!isLoading && email.isNotEmpty()) {
                                    authViewModel.sendPasswordReset(email)
                                    showSuccessDialog = true
                                }
                            },
                            enabled = !isLoading && email.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp,
                                disabledElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White,
                                disabledContainerColor = if (isDarkMode) Color(0xFF2A2E3B) else Color(0xFFE5E7EB),
                                disabledContentColor = if (isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isTurkish) "Gönder" else "Send",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Success Card
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.2f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = if (isTurkish) "E-posta Gönderildi!" else "Email Sent!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = if (isTurkish) 
                                "Şifre sıfırlama bağlantısı $email adresine gönderildi. Lütfen e-posta kutunuzu kontrol edin." 
                            else 
                                "Password reset link has been sent to $email. Please check your email inbox.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            color = subtitleColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Back to Login Button
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTurkish) "Giriş Sayfasına Dön" else "Back to Login",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Modern Success Dialog
        if (showSuccessDialog) {
            Dialog(onDismissRequest = { showSuccessDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f),
                            spotColor = primaryColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = primaryColor.copy(alpha = 0.2f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = if (isTurkish) "E-posta Gönderildi!" else "Email Sent!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (isTurkish) 
                                "Şifre sıfırlama bağlantısı $email adresine gönderildi.\n\nE-posta kutunuzu kontrol edin ve bağlantıya tıklayarak yeni şifrenizi belirleyin.\n\nŞifrenizi belirledikten sonra giriş yapabilirsiniz."
                            else 
                                "Password reset link has been sent to $email.\n\nCheck your email inbox and click the link to set your new password.\n\nYou can sign in after setting your new password.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            color = subtitleColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        // OK Button
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onNavigateToLogin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTurkish) "Tamam" else "OK",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
