package com.alperen.spendcraft.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.ThemeHelper
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isDarkMode by ThemeHelper.getDarkMode(context).collectAsState(initial = true)

    val isTr = LocaleHelper.getLanguage(context) == "tr"

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pwdVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var agree by remember { mutableStateOf(false) }

    // Colors
    val bgTop = Color(0xFF111321)
    val bgBottom = Color(0xFF0C0F19)
    val primary = Color(0xFF4C5EE6)
    val titleColor = Color(0xFFF2F4F8)
    val labelMuted = Color(0xFF9CA3AF)
    val fieldStroke = Color(0xFF2D3240)
    val cardColor = Color(0xFF141826)

    // Password strength
    val strength = remember(password) { passwordStrength(password) }
    val strengthText = when (strength.first) {
        Strength.WEAK -> if (isTr) "Zayıf" else "Weak"
        Strength.MEDIUM -> if (isTr) "Orta" else "Medium"
        Strength.STRONG -> if (isTr) "Güçlü" else "Strong"
    }
    val strengthColor = when (strength.first) {
        Strength.WEAK -> Color(0xFFE74C3C)
        Strength.MEDIUM -> Color(0xFFF39C12)
        Strength.STRONG -> Color(0xFF2ECC71)
    }

    // Renkler
    val backgroundColor = if (isDarkMode) Color(0xFF111321) else Color(0xFFF6F6F8)
    val primaryColor = Color(0xFF4C5EE6)
    val cardBackground = if (isDarkMode) Color(0xFF181B28) else Color.White.copy(alpha = 0.97f)
    val titleTextColor = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val subtitleColor = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                            contentDescription = if (isTr) "Geri" else "Back",
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
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(15.dp),
                            ambientColor = primaryColor.copy(alpha = 0.2f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isTr) "Hesap Oluştur" else "Create Account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = titleTextColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = if (isTr) 
                        "Yeni hesabınızı oluşturun" 
                    else 
                        "Create your new account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }
            // Modern Form Card
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
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = {
                            Text(
                                if (isTr) "Ad Soyad" else "Full Name",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            ) 
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
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                if (isTr) "E-posta adresi" else "Email address",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Email, 
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
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                if (isTr) "Şifre" else "Password",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Lock, 
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            ) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { pwdVisible = !pwdVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (pwdVisible) R.drawable.outline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = if (pwdVisible) "Hide password" else "Show password",
                                    tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (pwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Password Strength Indicator
                    if (password.isNotEmpty()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTr) "Şifre Gücü" else "Password Strength",
                                    color = titleTextColor.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = strengthText,
                                    color = strengthColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { strength.second },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                trackColor = if (isDarkMode) Color(0xFF2A2F3D) else Color(0xFFE5E7EB),
                                color = strengthColor
                            )
                        }
                    }

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        placeholder = {
                            Text(
                                if (isTr) "Şifreyi Doğrula" else "Confirm Password",
                                color = if (isDarkMode) Color(0xFF787C86) else Color(0xFFA1A1AA)
                            )
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Lock, 
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                            ) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (confirmVisible) R.drawable.outline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = if (confirmVisible) "Hide password" else "Show password",
                                    tint = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))

                    // Terms & Conditions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agree,
                            onCheckedChange = { agree = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = primaryColor,
                                uncheckedColor = if (isDarkMode) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                            ),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val tc = buildAnnotatedString {
                            append(if (isTr) "Kabul ediyorum " else "I agree to the ")
                            withStyle(SpanStyle(color = primaryColor)) {
                                append(if (isTr) "Şartlar ve Koşullar" else "Terms & Conditions")
                            }
                        }
                        Text(
                            text = tc,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = subtitleColor
                        )
                    }

                    // Error Message
                    if (!errorMessage.isNullOrEmpty()) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }

                    // Modern Sign Up Button
                    Button(
                        onClick = {
                            if (agree && name.isNotBlank() && email.isNotBlank()
                                && password.isNotBlank() && confirm == password
                            ) {
                                authViewModel.register(name, email, password, confirm)
                            }
                        },
                        enabled = !isLoading && agree &&
                                name.isNotBlank() && email.isNotBlank() &&
                                password.isNotBlank() && confirm.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
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
                                    painter = painterResource(R.drawable.ic_person_add),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTr) "Kayıt Ol" else "Sign Up",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Google bölümü ve divider kaldırıldı
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Login Link
            TextButton(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text(
                    text = if (isTr) "Hesabın var mı? Giriş yap" else "Have an account? Sign in",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Bottom padding for scroll
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


/* ---- Password strength util ---- */
private enum class Strength { WEAK, MEDIUM, STRONG }

private fun passwordStrength(pwd: String): Pair<Strength, Float> {
    if (pwd.isEmpty()) return Strength.WEAK to 0f
    var score = 0
    if (pwd.length >= 8) score++
    if (pwd.any { it.isUpperCase() } && pwd.any { it.isLowerCase() }) score++
    if (pwd.any { it.isDigit() }) score++
    if (pwd.any { "!@#\$%^&*()_+-=[]{}|;:'\",.<>/?".contains(it) }) score++

    return when {
        score <= 1 -> Strength.WEAK to 0.25f
        score == 2 || score == 3 -> Strength.MEDIUM to 0.6f
        else -> Strength.STRONG to 1f
    }
}
