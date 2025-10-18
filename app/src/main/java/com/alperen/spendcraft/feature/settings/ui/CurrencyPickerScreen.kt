package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS CurrencySettingsView'in birebir Android karşılığı
 * 
 * iOS Source: iosApp/SpendCraftiOS/CurrencySettingsView.swift:1-111
 * 
 * Features:
 * - 12 para birimi listesi (TRY default)
 * - Flag emoji + name + code + symbol
 * - Checkmark for selected currency
 * - Search bar
 * - @AppStorage sync
 */

data class Currency(
    val id: String,
    val code: String,
    val symbol: String,
    val name: String,
    val flag: String
) {
    companion object {
        // iOS: Currency.currencies - CurrencySettingsView.swift:17-30
        val currencies = listOf(
            Currency("TRY", "TRY", "₺", "Türk Lirası", "🇹🇷"),
            Currency("USD", "USD", "$", "Amerikan Doları", "🇺🇸"),
            Currency("EUR", "EUR", "€", "Euro", "🇪🇺"),
            Currency("GBP", "GBP", "£", "İngiliz Sterlini", "🇬🇧"),
            Currency("JPY", "JPY", "¥", "Japon Yeni", "🇯🇵"),
            Currency("CHF", "CHF", "Fr", "İsviçre Frangı", "🇨🇭"),
            Currency("CAD", "CAD", "C$", "Kanada Doları", "🇨🇦"),
            Currency("AUD", "AUD", "A$", "Avustralya Doları", "🇦🇺"),
            Currency("CNY", "CNY", "¥", "Çin Yuanı", "🇨🇳"),
            Currency("RUB", "RUB", "₽", "Rus Rublesi", "🇷🇺"),
            Currency("SAR", "SAR", "﷼", "Suudi Arabistan Riyali", "🇸🇦"),
            Currency("AED", "AED", "د.إ", "BAE Dirhemi", "🇦🇪")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    
    var selectedCurrency by remember { 
        mutableStateOf(prefs.getString("selectedCurrency", "TRY") ?: "TRY")
    }
    var searchText by remember { mutableStateOf("") }
    
    val filteredCurrencies = remember(searchText) {
        if (searchText.isEmpty()) {
            Currency.currencies
        } else {
            Currency.currencies.filter { 
                it.name.contains(searchText, ignoreCase = true) ||
                it.code.contains(searchText, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Para Birimi") },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar - iOS: .searchable(text: $searchText)
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Para birimi ara...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Ara"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Temizle"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Currency List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Section Header - iOS: header { }
                item {
                    Text(
                        text = "Kullanılabilir Para Birimleri",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                // Currency Items - iOS: ForEach(filteredCurrencies)
                items(filteredCurrencies) { currency ->
                    CurrencyListItem(
                        currency = currency,
                        isSelected = selectedCurrency == currency.code,
                        onClick = {
                            selectedCurrency = currency.code
                            // iOS: @AppStorage("selectedCurrency") - CurrencySettingsView.swift:97-98
                            prefs.edit()
                                .putString("selectedCurrency", currency.code)
                                .putString("selectedCurrencySymbol", currency.symbol)
                                .apply()
                            
                            // Haptic feedback yok - Android'de optional
                            onBack()
                        }
                    )
                }
                
                // Section Footer - iOS: footer { }
                item {
                    Text(
                        text = "Seçtiğiniz para birimi tüm uygulamada kullanılacaktır.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyListItem(
    currency: Currency,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // iOS: Button { ... } label: { HStack } - CurrencySettingsView.swift:55-83
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag - iOS: Text(currency.flag).font(.system(size: 32))
        Text(
            text = currency.flag,
            fontSize = 32.sp
        )
        
        // Name & Code - iOS: VStack(alignment: .leading)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = currency.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${currency.code} (${currency.symbol})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Checkmark - iOS: Image(systemName: "checkmark.circle.fill")
        if (isSelected) {
            Icon(
                painter = painterResource(id = CoreR.drawable.ic_checkmark_circle_fill),
                contentDescription = "Seçili",
                tint = IOSColors.Blue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

