package com.alperen.spendcraft.core.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * iOS CurrencyTextField'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Binlik ayırıcı ile formatlama (1.234,56)
 * - Türkçe locale (TRY): nokta binlik, virgül ondalık
 * - Otomatik formatlama
 * - Doğru cursor pozisyonu
 */

@Composable
fun CurrencyTextField(
    value: String,  // Raw value (formatlanmamış): "1234.56"
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    modifier: Modifier = Modifier,
    colors: androidx.compose.material3.TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent
    ),
    singleLine: Boolean = true
) {
    // TextFieldValue ile cursor pozisyonunu kontrol et
    var textFieldValue by remember { 
        mutableStateOf(TextFieldValue(text = formatNumber(value), selection = TextRange(formatNumber(value).length)))
    }
    
    // iOS: formatInput - AddTransactionView.swift:33-60
    fun formatInput(newTextFieldValue: TextFieldValue): TextFieldValue {
        val input = newTextFieldValue.text
        
        // Sadece rakam, nokta ve virgül
        val cleaned = input.replace(Regex("[^0-9,.]"), "")
        
        // Türkçe locale: virgül ondalık ayırıcı
        val decimalSep = ","
        
        // Noktaları ve virgülleri temizle, sonra ondalık kısmı ayır
        val parts = cleaned.split(decimalSep, limit = 2)
        
        // Tamsayı kısmı (noktaları kaldır - bunlar binlik ayırıcı)
        var integerPart = parts.getOrNull(0)?.replace(".", "") ?: ""
        
        // Ondalık kısım (max 2 hane)
        var decimalPart = parts.getOrNull(1)?.take(2) ?: ""
        
        // Raw value'yu güncelle (formatlanmamış, nokta ile)
        val rawValue = if (decimalPart.isEmpty()) {
            integerPart
        } else {
            "$integerPart.$decimalPart"
        }
        
        onValueChange(rawValue)
        
        // Display value'yu formatla
        val formattedText = if (integerPart.isNotEmpty()) {
            formatNumber(rawValue)
        } else {
            ""
        }
        
        // Cursor pozisyonunu hesapla - sonuna koy
        val newCursorPosition = formattedText.length
        
        return TextFieldValue(
            text = formattedText,
            selection = TextRange(newCursorPosition)
        )
    }
    
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = formatInput(newValue)
        },
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = textStyle,
        modifier = modifier,
        colors = colors,
        singleLine = singleLine
    )
    
    // Update display when value changes externally
    LaunchedEffect(value) {
        val formatted = formatNumber(value)
        if (value.isNotEmpty() && textFieldValue.text != formatted) {
            textFieldValue = TextFieldValue(
                text = formatted,
                selection = TextRange(formatted.length)
            )
        }
    }
}

/**
 * iOS formatNumber - AddTransactionView.swift:62-79
 * Formats number with thousand separators
 * 
 * Türkçe (TRY):
 * - Binlik: nokta (.)
 * - Ondalık: virgül (,)
 * - Örnek: 1234.56 → "1.234,56"
 */
private fun formatNumber(number: String): String {
    if (number.isEmpty()) return ""
    
    val doubleValue = number.toDoubleOrNull() ?: return number
    
    // Türkçe formatlama
    val symbols = DecimalFormatSymbols.getInstance(Locale("tr", "TR"))
    symbols.groupingSeparator = '.'  // Binlik
    symbols.decimalSeparator = ','   // Ondalık
    
    val formatter = DecimalFormat("#,##0.##", symbols)
    formatter.isGroupingUsed = true
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 2
    
    return formatter.format(doubleValue)
}

