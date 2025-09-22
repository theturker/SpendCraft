package com.alperen.spendcraft.feature.settings.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.CurrencyHelper
import com.alperen.spendcraft.core.model.Category
import androidx.compose.ui.res.stringResource
import com.alperen.spendcraft.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    categories: List<Category> = emptyList(),
    onAddCategory: (String) -> Unit = {},
    onDeleteCategory: (Long) -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var darkMode by rememberSaveable { mutableStateOf(false) }
    var currency by rememberSaveable { mutableStateOf(CurrencyHelper.getCurrency(context)) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var languageExpanded by rememberSaveable { mutableStateOf(false) }
    
    // Para birimleri ve sembolleri
    val currencies = remember { CurrencyHelper.currencies.toList() }
    
    // Dil seçenekleri
    val languages = remember { 
        listOf(
            "🇹🇷 Türkçe" to "tr", 
            "🇺🇸 English" to "en"
        ) 
    }
    
    val currentLanguage = LocaleHelper.getLanguage(context)
    var selectedLanguage by rememberSaveable { 
        mutableStateOf(languages.find { it.second == currentLanguage }?.first ?: "🇺🇸 English")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Language Selection
            Text(
                text = "🌍 ${stringResource(R.string.language_selection)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current: $selectedLanguage",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { (displayName, languageCode) ->
                            Button(
                                onClick = {
                                    selectedLanguage = displayName
                                    LocaleHelper.setLocale(context, languageCode)
                                    // Restart activity to apply language change
                                    (context as? android.app.Activity)?.recreate()
                                },
                                modifier = Modifier.weight(1f),
                                colors = if (selectedLanguage == displayName) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            ) {
                                Text(displayName)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Category Management
            Text(
                text = "📂 Kategori Yönetimi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kategorileri Yönet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${categories.size} kategori mevcut",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onNavigateToCategories) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Kategori Yönetimi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Currency Selection
            Text(
                text = "💰 Currency / Para Birimi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current: ${currencies.find { it.first == currency }?.let { "${it.second} ${it.first}" } ?: currency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        currencies.forEach { (currencyCode, symbol) ->
                            Button(
                                onClick = {
                                    currency = currencyCode
                                    CurrencyHelper.setCurrency(context, currencyCode)
                                },
                                modifier = Modifier.weight(1f),
                                colors = if (currency == currencyCode) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            ) {
                                Text("$symbol $currencyCode")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Dark Mode
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌙", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Switch to dark theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Current Settings Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📋 Current Settings",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Language: $selectedLanguage")
                    Text("Currency: ${currencies.find { it.first == currency }?.let { "${it.second} ${it.first}" } ?: currency}")
                    Text("Theme: ${if (darkMode) "Dark" else "Light"}")
                }
            }
        }
    }
}
//