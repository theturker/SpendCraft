package com.alperen.spendcraft.feature.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 
 * iOS Source: iosApp/SpendCraftiOS/CategoriesView.swift:279-424
 * 
 * Özellikler:
 * - Kategori Adı (TextField)
 * - Kategori Tipi (Segmented: Gider/Gelir)
 * - İkon Seç (LazyVGrid, 18 icon)
 * - Renk Seç (12 color)
 * - Önizleme
 * - Kaydet butonu
 */

// iOS: categoryIcons - CategoriesView.swift:296-301
private val CategoryIcons = listOf(
    CoreR.drawable.ic_cart_fill,
    CoreR.drawable.ic_fork_knife,
    CoreR.drawable.ic_house_fill,
    CoreR.drawable.ic_car_fill,
    CoreR.drawable.ic_tram_fill,
    CoreR.drawable.ic_airplane,
    CoreR.drawable.ic_bolt_fill,
    CoreR.drawable.ic_bag_fill,
    CoreR.drawable.ic_gift_fill,
    CoreR.drawable.ic_book_fill,
    CoreR.drawable.ic_gamecontroller_fill,
    CoreR.drawable.ic_film_fill,
    CoreR.drawable.ic_heart_fill,
    CoreR.drawable.ic_creditcard_fill,
    CoreR.drawable.ic_pills_fill,
    CoreR.drawable.ic_briefcase_fill,
    CoreR.drawable.ic_graduationcap_fill,
    CoreR.drawable.ic_phone_fill
)

// iOS: categoryColors - CategoriesView.swift:303-308
private data class CategoryColor(val name: String, val color: Color)
private val CategoryColors = listOf(
    CategoryColor("Mavi", IOSColors.Blue),
    CategoryColor("Yeşil", IOSColors.Green),
    CategoryColor("Kırmızı", IOSColors.Red),
    CategoryColor("Turuncu", IOSColors.Orange),
    CategoryColor("Mor", IOSColors.Purple),
    CategoryColor("Pembe", IOSColors.Pink),
    CategoryColor("Sarı", IOSColors.Yellow),
    CategoryColor("Kahverengi", Color(0xFF8B4513)),
    CategoryColor("İndigo", Color(0xFF4B0082)),
    CategoryColor("Cyan", Color(0xFF00FFFF)),
    CategoryColor("Mint", Color(0xFF98FF98)),
    CategoryColor("Teal", Color(0xFF008080))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    initialType: String? = null, // "income" or "expense"
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, color: String, type: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryType by remember { mutableStateOf(initialType ?: "expense") }
    var selectedIconIndex by remember { mutableStateOf(0) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    
    val selectedColor = CategoryColors[selectedColorIndex].color
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Yeni Kategori",
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
            // 1. Kategori Bilgileri - iOS: Section("Kategori Bilgileri")
            item {
                Text(
                    text = "Kategori Bilgileri",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category Name TextField - iOS: TextField("Kategori Adı")
                        OutlinedTextField(
                            value = categoryName,
                            onValueChange = { categoryName = it },
                            label = { Text("Kategori Adı") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Category Type Picker - iOS: Picker("Kategori Tipi").pickerStyle(.segmented)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("expense" to "Gider", "income" to "Gelir").forEach { (type, label) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (categoryType == type) MaterialTheme.colorScheme.primary
                                            else Color.Transparent
                                        )
                                        .clickable { categoryType = type }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (categoryType == type) Color.White
                                               else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (categoryType == type) FontWeight.SemiBold
                                                     else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 2. İkon Seç - iOS: Section("İkon Seç") + LazyVGrid
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "İkon Seç",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // iOS: LazyVGrid(columns: [GridItem(.adaptive(minimum: 50))], spacing: 12)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 60.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(240.dp), // Fixed height for grid
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(CategoryIcons.size) { index ->
                        val isSelected = selectedIconIndex == index
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) selectedColor
                                    else selectedColor.copy(alpha = 0.2f)
                                )
                                .clickable { selectedIconIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = CategoryIcons[index]),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else selectedColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            
            // 3. Renk Seç - iOS: Section("Renk Seç")
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Renk Seç",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // iOS: HStack with color circles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryColors.forEachIndexed { index, categoryColor ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 50.dp else 44.dp)
                                .clip(CircleShape)
                                .background(categoryColor.color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }
            }
            
            // 4. Önizleme - iOS: Section("Önizleme")
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Önizleme",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Preview Icon
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = CategoryIcons[selectedIconIndex]),
                                contentDescription = null,
                                tint = selectedColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Preview Name
                        Text(
                            text = categoryName.ifEmpty { "Kategori Adı" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            // 5. Kaydet Butonu - iOS: Section { Button("Kategori Ekle") }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // Convert icon resource ID to string name
                        val iconName = CategoryIcons[selectedIconIndex].toString()
                        val colorHex = String.format("#%06X", 0xFFFFFF and selectedColor.hashCode())
                        onSave(categoryName, iconName, colorHex, categoryType)
                    },
                    enabled = categoryName.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IOSColors.Blue,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Kategori Ekle",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


