package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alperen.spendcraft.auth.AuthViewModel
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR
import kotlinx.coroutines.launch

/**
 * iOS AccountInfoView'in birebir Android karşılığı
 * 
 * iOS Source: iosApp/SpendCraftiOS/AccountInfoView.swift:1-341
 * 
 * Features:
 * - User profile display (avatar, name, email)
 * - Edit name
 * - Change password
 * - Email verification status
 * - Delete account (danger zone)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showEditName by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Get user info
    val userName = when (val state = authState) {
        is com.alperen.spendcraft.auth.AuthState.Authenticated -> state.user.displayName ?: "Kullanıcı"
        else -> "Kullanıcı"
    }
    val userEmail = when (val state = authState) {
        is com.alperen.spendcraft.auth.AuthState.Authenticated -> state.user.email ?: ""
        else -> ""
    }
    val isEmailVerified = when (val state = authState) {
        is com.alperen.spendcraft.auth.AuthState.Authenticated -> state.user.isEmailVerified
        else -> false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hesap Bilgileri") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = CoreR.drawable.ic_chevron_left),
                            contentDescription = "Geri"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // User Profile Section - iOS: AccountInfoView.swift:25-63
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar - iOS: Circle with gradient + initial letter
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(IOSColors.Blue, IOSColors.Purple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        // User Info
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            // Email Verification Status
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isEmailVerified) CoreR.drawable.ic_checkmark_seal_fill
                                        else CoreR.drawable.ic_exclamationmark_triangle_fill
                                    ),
                                    contentDescription = null,
                                    tint = if (isEmailVerified) IOSColors.Green else IOSColors.Orange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isEmailVerified) "E-posta doğrulandı" else "E-posta doğrulanmadı",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isEmailVerified) IOSColors.Green else IOSColors.Orange
                                )
                            }
                        }
                    }
                }
            }
            
            // Section Header - Hesap İşlemleri
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Hesap İşlemleri",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Edit Name - iOS: AccountInfoView.swift:68-82
            item {
                AccountActionItem(
                    icon = CoreR.drawable.ic_person_fill,
                    iconColor = IOSColors.Blue,
                    title = "Adı Düzenle",
                    onClick = { showEditName = true }
                )
            }
            
            // Change Password - iOS: AccountInfoView.swift:85-98
            item {
                AccountActionItem(
                    icon = CoreR.drawable.ic_lock_fill,
                    iconColor = IOSColors.Orange,
                    title = "Şifre Değiştir",
                    onClick = { showChangePassword = true }
                )
            }
            
            // Email Verification - iOS: AccountInfoView.swift:101-117
            if (!isEmailVerified) {
                item {
                    AccountActionItem(
                        icon = CoreR.drawable.ic_envelope_fill,
                        iconColor = IOSColors.Green,
                        title = "E-posta Doğrulama Gönder",
                        onClick = {
                isLoading = true
                            scope.launch {
                                authViewModel.sendEmailVerification().fold(
                                    onSuccess = {
                                        alertMessage = "Doğrulama e-postası gönderildi! E-postanızı kontrol edin."
                                        showAlert = true
                                        isLoading = false
                                    },
                                    onFailure = { error ->
                                        alertMessage = error.message ?: "Doğrulama e-postası gönderilemedi"
                                        showAlert = true
                                        isLoading = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
            
            // Danger Zone - iOS: AccountInfoView.swift:120-132
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tehlikeli Bölge",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                AccountActionItem(
                    icon = CoreR.drawable.ic_trash_fill,
                    iconColor = IOSColors.Red,
                    title = "Hesabı Sil",
                    titleColor = IOSColors.Red,
                    onClick = { showDeleteAccount = true }
                )
            }
        }
        
        // Alert Dialog for messages
        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                title = { Text("Bilgi") },
                text = { Text(alertMessage) },
                confirmButton = {
                    TextButton(onClick = { showAlert = false }) {
                        Text("Tamam")
                    }
                }
            )
        }
    }
    
    // Edit Name Sheet - iOS: EditNameSheet (BottomSheet)
    if (showEditName) {
        EditNameSheet(
            currentName = userName,
            onDismiss = { showEditName = false },
            onSave = { newName ->
                scope.launch {
                    authViewModel.updateDisplayName(newName).fold(
                        onSuccess = {
                            alertMessage = "Adınız başarıyla güncellendi!"
                            showAlert = true
                            showEditName = false
                        },
                        onFailure = { error ->
                            alertMessage = error.message ?: "Ad güncellenemedi"
                            showAlert = true
                        }
                    )
                }
            }
        )
    }
    
    // Change Password Sheet - iOS: ChangePasswordSheet (BottomSheet)
    if (showChangePassword) {
        ChangePasswordSheet(
            onDismiss = { showChangePassword = false },
            onSave = { currentPassword, newPassword ->
                scope.launch {
                    authViewModel.updatePassword(currentPassword, newPassword).fold(
                        onSuccess = {
                            alertMessage = "Şifreniz başarıyla güncellendi!"
                            showAlert = true
                            showChangePassword = false
                        },
                        onFailure = { error ->
                            alertMessage = error.message ?: "Şifre güncellenemedi"
                            showAlert = true
                        }
                    )
                }
            }
        )
    }
    
    // Delete Account Dialog - iOS: AccountInfoView.swift:147-154
    if (showDeleteAccount) {
        AlertDialog(
            onDismissRequest = { showDeleteAccount = false },
            icon = {
                Icon(
                    painter = painterResource(id = CoreR.drawable.ic_exclamationmark_triangle_fill),
                    contentDescription = null,
                    tint = IOSColors.Red,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Hesabı Sil") },
            text = {
                Text("Hesabınızı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz ve tüm verileriniz silinecektir.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Delete account (iOS: contact support message)
                        showDeleteAccount = false
                    }
                ) {
                    Text("Sil", color = IOSColors.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccount = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun AccountActionItem(
    icon: Int,
    iconColor: Color,
    title: String,
    titleColor: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// MARK: - Edit Name Sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameSheet(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Adı Düzenle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Adınız") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("İptal")
                }
                
                Button(
                    onClick = { onSave(newName) },
                    modifier = Modifier.weight(1f),
                    enabled = newName.isNotEmpty(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// MARK: - Change Password Sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordSheet(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    val isValid = currentPassword.isNotEmpty() &&
                  newPassword.isNotEmpty() &&
                  newPassword == confirmPassword &&
                  newPassword.length >= 6
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "Şifre Değiştir",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mevcut Şifre") },
                    visualTransformation = if (showCurrentPassword) 
                        androidx.compose.ui.text.input.VisualTransformation.None 
                    else 
                        androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                imageVector = if (showCurrentPassword) 
                                    androidx.compose.material.icons.Icons.Filled.Lock
                                else 
                                    androidx.compose.material.icons.Icons.Filled.Lock,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
            
            item {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Yeni Şifre") },
                    visualTransformation = if (showNewPassword) 
                        androidx.compose.ui.text.input.VisualTransformation.None 
                    else 
                        androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                imageVector = if (showNewPassword) 
                                    androidx.compose.material.icons.Icons.Filled.Lock
                                else 
                                    androidx.compose.material.icons.Icons.Filled.Lock,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
            
            item {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Yeni Şifre (Tekrar)") },
                    visualTransformation = if (showConfirmPassword) 
                        androidx.compose.ui.text.input.VisualTransformation.None 
                    else 
                        androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) 
                                    androidx.compose.material.icons.Icons.Filled.Lock
                                else 
                                    androidx.compose.material.icons.Icons.Filled.Lock,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    singleLine = true,
                    isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
            
            item {
                Text(
                    text = "Şifreniz en az 6 karakter olmalıdır.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal")
                    }
                    
                    Button(
                        onClick = { onSave(currentPassword, newPassword) },
                        modifier = Modifier.weight(1f),
                        enabled = isValid,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
}

