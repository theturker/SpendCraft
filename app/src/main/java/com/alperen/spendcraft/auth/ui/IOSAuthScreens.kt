package com.alperen.spendcraft.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS Auth Screens - Login, Register, ForgotPassword
 * iOS'taki temiz ve minimal form tasarımı
 */

/**
 * iOS LoginView'in birebir Android karşılığı
 */
@Composable
fun IOSLoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Header Icon
        Icon(
            painter = painterResource(id = CoreR.drawable.ic_chart_line_uptrend),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = IOSColors.Blue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = "Hoş Geldiniz",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Hesabınıza giriş yapın",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Email Field
        Text(
            text = "E-posta",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "ornek@email.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Password Field
        Text(
            text = "Şifre",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSPasswordField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Şifrenizi girin",
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )
        
        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = IOSColors.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Login Button
        Button(
            onClick = { onLoginClick(email, password) },
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IOSColors.Blue
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Giriş Yap",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Forgot Password
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToForgotPassword) {
            Text(
                text = "Şifremi Unuttum",
                color = IOSColors.Blue
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Divider - "veya"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "veya",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Google Sign In Button - iOS Style
        OutlinedButton(
            onClick = onGoogleSignIn,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.R.drawable.google),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Google ile Giriş Yap",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Register Link
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hesabınız yok mu?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Kayıt Ol",
                    fontWeight = FontWeight.SemiBold,
                    color = IOSColors.Blue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * iOS RegisterView'in birebir Android karşılığı
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
    
    val isValid = name.isNotEmpty() && email.isNotEmpty() && 
                  password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                  password == confirmPassword && password.length >= 6 &&
                  email.contains("@")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header Icon
        Icon(
            painter = painterResource(id = CoreR.drawable.ic_person_badge_plus),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = IOSColors.Blue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = "Hesap Oluştur",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Yeni hesabınızı oluşturun",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        
        // Name Field
        Text(
            text = "Ad Soyad",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Adınızı ve soyadınızı girin"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email Field
        Text(
            text = "E-posta",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "ornek@email.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        Text(
            text = "Şifre",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSPasswordField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Şifrenizi girin",
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Password Field
        Text(
            text = "Şifre Tekrar",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        IOSPasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Şifrenizi tekrar girin",
            passwordVisible = confirmPasswordVisible,
            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
        )
        
        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = IOSColors.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register Button
        Button(
            onClick = { onRegisterClick(name, email, password) },
            enabled = !isLoading && isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValid) IOSColors.Blue else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Hesap Oluştur",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Login Link
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Zaten hesabınız var mı?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Giriş Yap",
                    fontWeight = FontWeight.SemiBold,
                    color = IOSColors.Blue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * iOS ForgotPasswordView'in birebir Android karşılığı
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
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Header Icon
        Icon(
            painter = painterResource(id = CoreR.drawable.ic_key_fill),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = IOSColors.Blue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = "Şifremi Unuttum",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        if (isSuccess) {
            // Success State
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_checkmark_circle_fill),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = IOSColors.Green
                )
                
                Text(
                    text = "E-posta Gönderildi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "E-posta adresinize şifre sıfırlama bağlantısı gönderildi. Lütfen e-postanızı kontrol edin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IOSColors.Blue
                    )
                ) {
                    Text(
                        text = "Giriş Sayfasına Dön",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            // Form State
            Text(
                text = "E-posta",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            IOSTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "ornek@email.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = IOSColors.Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Send Button
            Button(
                onClick = { onSendResetLink(email) },
                enabled = !isLoading && email.isNotEmpty() && email.contains("@"),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (email.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else IOSColors.Blue
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Sıfırlama Bağlantısı Gönder",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Back to Login
            TextButton(onClick = onNavigateToLogin) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = CoreR.drawable.ic_chevron_right),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Giriş Sayfasına Dön",
                                color = IOSColors.Blue
                            )
                        }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * iOS-style Text Field
 */
@Composable
private fun IOSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IOSColors.Blue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}

/**
 * iOS-style Password Field
 */
@Composable
private fun IOSPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IOSColors.Blue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible) CoreR.drawable.ic_eye else CoreR.drawable.ic_eye_slash
                    ),
                    contentDescription = if (passwordVisible) "Şifreyi gizle" else "Şifreyi göster"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
}

