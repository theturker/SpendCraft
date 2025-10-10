package com.alperen.spendcraft.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS AddCategoryView'in birebir Android karşılığı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, color: String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("circle.fill") }
    var selectedIconResId by remember { mutableStateOf(CoreR.drawable.ic_cart_fill) }
    var selectedColor by remember { mutableStateOf(IOSColors.Blue) }
    
    // iOS'taki categoryIcons listesi
    val categoryIcons = listOf(
        "cart.fill" to CoreR.drawable.ic_cart_fill,
        "fork.knife" to CoreR.drawable.ic_fork_knife,
        "house.fill" to CoreR.drawable.ic_house_fill,
        "car.fill" to CoreR.drawable.ic_car_fill,
        "tram.fill" to CoreR.drawable.ic_tram_fill,
        "airplane" to CoreR.drawable.ic_airplane,
        "bolt.fill" to CoreR.drawable.ic_bolt_fill,
        "bag.fill" to CoreR.drawable.ic_bag_fill,
        "gift.fill" to CoreR.drawable.ic_gift_fill,
        "book.fill" to CoreR.drawable.ic_book_fill,
        "gamecontroller.fill" to CoreR.drawable.ic_gamecontroller_fill,
        "film.fill" to CoreR.drawable.ic_film_fill,
        "heart.fill" to CoreR.drawable.ic_heart_fill,
        "creditcard.fill" to CoreR.drawable.ic_creditcard_fill,
        "pills.fill" to CoreR.drawable.ic_pills_fill,
        "briefcase.fill" to CoreR.drawable.ic_briefcase_fill,
        "graduationcap.fill" to CoreR.drawable.ic_graduationcap_fill,
        "phone.fill" to CoreR.drawable.ic_phone_fill
    )
    
    // iOS'taki categoryColors listesi
    val categoryColors = listOf(
        "Mavi" to IOSColors.Blue,
        "Yeşil" to IOSColors.Green,
        "Kırmızı" to IOSColors.Red,
        "Turuncu" to IOSColors.Orange,
        "Mor" to IOSColors.Purple,
        "Pembe" to IOSColors.Pink,
        "Sarı" to IOSColors.Yellow,
        "Kahverengi" to Color(0xFF8B4513),
        "İndigo" to IOSColors.Indigo,
        "Cyan" to IOSColors.Cyan,
        "Mint" to IOSColors.Mint,
        "Teal" to IOSColors.Teal
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yeni Kategori",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat"
                        )
                    }
                }
                
                // Category Name
                Text(
                    text = "Kategori Bilgileri",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    placeholder = { Text("Kategori Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Icon Selection
                Text(
                    text = "İkon Seç",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(60.dp),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryIcons) { (sfSymbol, resId) ->
                        val isSelected = selectedIconResId == resId
                        
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) selectedColor 
                                    else selectedColor.copy(alpha = 0.2f)
                                )
                                .clickable {
                                    selectedIcon = sfSymbol
                                    selectedIconResId = resId
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else selectedColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Color Selection
                Text(
                    text = "Renk Seç",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(70.dp),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryColors) { (colorName, color) ->
                        val isSelected = selectedColor == color
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                            
                            Text(
                                text = colorName,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }
                
                // Preview
                Text(
                    text = "Önizleme",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = selectedIconResId),
                                contentDescription = null,
                                tint = selectedColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Text(
                            text = categoryName.ifEmpty { "Kategori Adı" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Save Button
                Button(
                    onClick = {
                        val colorHex = String.format("#%06X", 0xFFFFFF and selectedColor.hashCode())
                        onSave(categoryName, selectedIcon, colorHex)
                    },
                    enabled = categoryName.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Kategori Ekle",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

