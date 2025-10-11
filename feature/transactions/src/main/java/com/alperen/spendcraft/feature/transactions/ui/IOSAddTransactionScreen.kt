package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * iOS AddTransactionView'in birebir Android karşılığı
 * 
 * Form Sections:
 * 1. Transaction Type (Segmented Control)
 * 2. Amount (₺ symbol ile)
 * 3. Category (Horizontal scrollable buttons)
 * 4. Account (Picker)
 * 5. Date (Date Picker)
 * 6. Note (Optional)
 * 7. Save Button
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSAddTransactionScreen(
    categories: List<Category>,
    initialTransactionType: Boolean? = null,
    onSave: (amountMinor: Long, note: String?, categoryId: Long?, isIncome: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(categories.firstOrNull()) }
    var isIncome by remember { mutableStateOf(initialTransactionType ?: false) }
    var selectedDate by remember { mutableStateOf(Date()) }
    
    val isValid = amount.isNotEmpty() && 
                  amount.toDoubleOrNull() != null && 
                  amount.toDouble() > 0 &&
                  selectedCategory != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Yeni İşlem",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
            // 1. Transaction Type Section (Segmented Control)
            item {
                FormSection(title = null) {
                    SegmentedControl(
                        selectedIsIncome = isIncome,
                        onSelectionChange = { isIncome = it }
                    )
                }
            }
            
            // 2. Amount Section
            item {
                FormSection(title = "Miktar") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        
                        Text(
                            text = "₺",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 3. Category Section (Horizontal Buttons)
            item {
                FormSection(title = "Kategori") {
                    if (categories.isEmpty()) {
                        Text(
                            text = "Kategori bulunamadı",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(categories) { category ->
                                CategoryButton(
                                    category = category,
                                    isSelected = selectedCategory?.id == category.id,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }
                }
            }
            
            // 4. Account Section (Placeholder)
            item {
                FormSection(title = "Hesap") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Varsayılan Hesap",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // 5. Date Section
            item {
                FormSection(title = "Tarih") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        val dateFormat = remember { SimpleDateFormat("d MMM yyyy, HH:mm", Locale("tr")) }
                        Text(
                            text = dateFormat.format(selectedDate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // 6. Note Section
            item {
                FormSection(title = "Not") {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("İsteğe bağlı not") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // 7. Save Button
            item {
                FormSection(title = null) {
                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull() ?: 0.0
                            val amountMinor = (amountValue * 100).toLong()
                            onSave(
                                amountMinor,
                                note.ifEmpty { null },
                                selectedCategory?.id,
                                isIncome
                            )
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Kaydet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Form Section Container - iOS Form style
 */
@Composable
private fun FormSection(
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

/**
 * Segmented Control - iOS style
 */
@Composable
private fun SegmentedControl(
    selectedIsIncome: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Gider
        SegmentedButton(
            text = "Gider",
            isSelected = !selectedIsIncome,
            onClick = { onSelectionChange(false) },
            modifier = Modifier.weight(1f)
        )
        
        // Gelir
        SegmentedButton(
            text = "Gelir",
            isSelected = selectedIsIncome,
            onClick = { onSelectionChange(true) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SegmentedButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary 
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Category Button - iOS style circular button with icon
 */
@Composable
private fun CategoryButton(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icon Circle - iOS'taki gibi category.icon kullan
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) categoryColor 
                    else categoryColor.copy(alpha = 0.2f)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = getCategoryIconResource(category.icon ?: "circle.fill")),
                contentDescription = null,
                tint = if (isSelected) Color.White else categoryColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Category Name
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1
        )
    }
}

/**
 * iOS SF Symbol to Android Drawable mapping for categories
 * iOS'taki kategori icon isimlerini Android drawable resource'larına çevirir
 */
private fun getCategoryIconResource(sfSymbol: String): Int {
    return when (sfSymbol) {
        "cart.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_cart_fill
        "fork.knife" -> com.alperen.spendcraft.core.ui.R.drawable.ic_fork_knife
        "house.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_house_fill
        "car.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_car_fill
        "tram.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_tram_fill
        "airplane" -> com.alperen.spendcraft.core.ui.R.drawable.ic_airplane
        "bolt.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_bolt_fill
        "bag.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_bag_fill
        "gift.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_gift_fill
        "book.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_book_fill
        "gamecontroller.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_gamecontroller_fill
        "film.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_film_fill
        "heart.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_heart_fill
        "creditcard.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_creditcard_fill
        "pills.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_pills_fill
        "briefcase.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_briefcase_fill
        "graduationcap.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_graduationcap_fill
        "phone.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_phone_fill
        "circle.fill" -> com.alperen.spendcraft.core.ui.R.drawable.ic_circle_fill
        else -> com.alperen.spendcraft.core.ui.R.drawable.ic_circle_fill // Default
    }
}
