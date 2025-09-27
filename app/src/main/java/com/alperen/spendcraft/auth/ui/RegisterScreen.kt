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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
    ) {
        // top glow
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .blur(90.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x334C5EE6), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(bottomStart = 120.dp, bottomEnd = 120.dp)
                )
                .align(Alignment.TopCenter)
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card
            SoftShadowCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = cardColor,
                shape = RoundedCornerShape(26.dp),
                addDropShadow = true
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = if (isTr) "Hesap oluştur" else "Create your account",
                        color = titleColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Name
                    LinedField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = if (isTr) "Ad Soyad" else "Name",
                        leading = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = labelMuted)
                        },
                        stroke = fieldStroke
                    )
                    Spacer(Modifier.height(14.dp))

                    // Email
                    LinedField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = if (isTr) "E-posta" else "Email",
                        leading = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = labelMuted)
                        },
                        keyboard = KeyboardType.Email,
                        stroke = fieldStroke
                    )
                    Spacer(Modifier.height(14.dp))

                    // Password
                    LinedField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = if (isTr) "Şifre" else "Password",
                        leading = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = labelMuted)
                        },
                        trailing = {
                            IconButton(onClick = { pwdVisible = !pwdVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (confirmVisible) R.drawable.outline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = null,
                                    tint = labelMuted
                                )
                            }
                        },
                        keyboard = KeyboardType.Password,
                        visual = if (pwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        stroke = fieldStroke
                    )

                    // Password strength
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isTr) "Şifre Gücü" else "Password Strength",
                            color = titleColor.copy(0.9f),
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = strengthText, color = strengthColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { strength.second },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        trackColor = Color(0xFF2A2F3D),
                        color = strengthColor
                    )
                    Spacer(Modifier.height(14.dp))

                    // Confirm password
                    LinedField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        placeholder = if (isTr) "Şifreyi Doğrula" else "Confirm Password",
                        leading = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = labelMuted)
                        },
                        trailing = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (confirmVisible) R.drawable.outline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = null,
                                    tint = labelMuted
                                )
                            }
                        },
                        keyboard = KeyboardType.Password,
                        visual = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        stroke = fieldStroke
                    )
                    Spacer(Modifier.height(16.dp))

                    // Terms & Conditions
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = agree,
                            onCheckedChange = { agree = it },
                            colors = CheckboxDefaults.colors(checkedColor = primary, uncheckedColor = labelMuted)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isTr) "Kabul ediyorum " else "I agree to the ",
                            color = labelMuted,
                            fontSize = 15.sp
                        )
                        val tc = buildAnnotatedString {
                            append(if (isTr) "Şartlar ve " else "Terms & ")
                            withStyle(SpanStyle(color = primary)) {
                                append(if (isTr) "Koşullar" else "Conditions")
                            }
                            append(if (isTr) " ve " else " and ")
                            withStyle(SpanStyle(color = primary)) {
                                append(if (isTr) "Gizlilik Politikası" else "Privacy Policy")
                            }
                        }
                        Text(text = tc, fontSize = 15.sp)
                    }

                    // Error
                    if (!errorMessage.isNullOrEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Sign Up (previous behavior preserved)
                    Button(
                        onClick = {
                            if (agree && name.isNotBlank() && email.isNotBlank()
                                && password.isNotBlank() && confirm == password
                            ) {
                                authViewModel.register(name, email, password, confirm)
                                // önceki akış ne ise (ör. success callback) ViewModel içinde tetiklenmeli
                            }
                        },
                        enabled = !isLoading && agree &&
                                name.isNotBlank() && email.isNotBlank() &&
                                password.isNotBlank() && confirm.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(text = if (isTr) "Kayıt Ol" else "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Google bölümü ve divider kaldırıldı
                }
            }

            Spacer(Modifier.height(12.dp))

            // İstersen altta login linki kalsın
            TextButton(onClick = onNavigateToLogin) {
                Text(text = if (isTr) "Hesabın var mı? Giriş yap" else "Have an account? Sign in", color = Color(0xFF9CA3AF))
            }
        }
    }
}

/* ---------- Reusable pieces ---------- */

@Composable
private fun LinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    keyboard: KeyboardType = KeyboardType.Text,
    visual: VisualTransformation = VisualTransformation.None,
    stroke: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFA1A1AA)) },
        leadingIcon = leading,
        trailingIcon = trailing,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        visualTransformation = visual,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = stroke,
            unfocusedBorderColor = stroke,
            focusedContainerColor = Color(0x1A0E111A),
            unfocusedContainerColor = Color(0x1A0E111A),
            focusedTextColor = Color(0xFFE6EAF2),
            unfocusedTextColor = Color(0xFFE6EAF2),
            cursorColor = Color(0xFF4C5EE6)
        )
    )
}

@Composable
private fun SoftShadowCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    shape: RoundedCornerShape = RoundedCornerShape(26.dp),
    elevationLight: Dp = 8.dp,
    elevationDark: Dp = 4.dp,
    addDropShadow: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val wrap = if (addDropShadow)
        modifier.shadow(elevation = 18.dp, shape = shape, clip = false)
    else modifier

    Box(modifier = wrap, contentAlignment = Alignment.Center) {
        // bottom soft shadow
        Box(
            Modifier
                .fillMaxWidth(0.96f)
                .height(120.dp)
                .offset(y = 18.dp)
                .blur(50.dp)
                .background(Color.Black.copy(0.20f), shape)
        )
        // top glow
        Box(
            Modifier
                .fillMaxWidth(0.92f)
                .height(100.dp)
                .offset(y = (-6).dp)
                .blur(60.dp)
                .background(Color(0x334C5EE6), shape)
        )
        // card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), shape = shape)
                .clip(shape),
            color = containerColor,
            shape = shape,
            tonalElevation = elevationDark,
            shadowElevation = elevationLight
        ) { Column(content = content) }
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
