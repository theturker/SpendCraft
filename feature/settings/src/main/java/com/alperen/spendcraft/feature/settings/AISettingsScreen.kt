package com.alperen.spendcraft.feature.settings

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.alperen.spendcraft.core.ai.AIKeyManager
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import javax.inject.Inject

@Composable
fun AISettingsScreen(
    aiKeyManager: AIKeyManager,
    onBack: () -> Unit = {}
) {
    var apiKey by remember { mutableStateOf(aiKeyManager.getGroqApiKey() ?: "") }
    var showPassword by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    AppScaffold(
        title = "AI Ayarları",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key Input
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = SpendCraftTheme.colors.primary
                        )
                        Text(
                            text = "Groq API Anahtarı",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "AI önerileri almak için Groq API anahtarınızı girin. Anahtarınız güvenli bir şekilde şifrelenerek saklanır.",
                        style = SpendCraftTheme.typography.bodyMedium,
                        color = SpendCraftTheme.colors.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Anahtarı") },
                        placeholder = { Text("gsk_...") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    painter = if (showPassword) 
                                        painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_eye_vector)
                                    else 
                                        painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_eye_off_vector),
                                    contentDescription = if (showPassword) "Gizle" else "Göster"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                apiKey = ""
                                aiKeyManager.clearApiKey()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Temizle")
                        }
                        
                        Button(
                            onClick = { showSaveDialog = true },
                            enabled = apiKey.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kaydet")
                        }
                    }
                }
            }
            
            // API Key Status
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (aiKeyManager.hasGroqApiKey()) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (aiKeyManager.hasGroqApiKey()) 
                                SpendCraftTheme.colors.primary 
                            else 
                                SpendCraftTheme.colors.error
                        )
                        Text(
                            text = "API Anahtarı Durumu",
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (aiKeyManager.hasGroqApiKey()) {
                            "✅ API anahtarı kaydedildi ve AI önerileri kullanılabilir."
                        } else {
                            "⚠️ API anahtarı kaydedilmedi. AI önerileri çalışmayacak."
                        },
                        style = SpendCraftTheme.typography.bodyMedium,
                        color = if (aiKeyManager.hasGroqApiKey()) 
                            SpendCraftTheme.colors.primary 
                        else 
                            SpendCraftTheme.colors.error
                    )
                }
            }
            
            // How to Get API Key
            ModernCard {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "API Anahtarı Nasıl Alınır?",
                        style = SpendCraftTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val steps = listOf(
                        "1. Groq Console'a gidin (console.groq.com)",
                        "2. Hesap oluşturun veya giriş yapın",
                        "3. API Keys bölümüne gidin",
                        "4. 'Create API Key' butonuna tıklayın",
                        "5. Oluşturulan anahtarı kopyalayın",
                        "6. Bu ekranda yapıştırın ve kaydedin"
                    )
                    
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
    
    // Save Confirmation Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("API Anahtarını Kaydet") },
            text = { 
                Text("Bu API anahtarını kaydetmek istediğinizden emin misiniz? Anahtar güvenli bir şekilde şifrelenerek saklanacak.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        aiKeyManager.setGroqApiKey(apiKey)
                        showSaveDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}
