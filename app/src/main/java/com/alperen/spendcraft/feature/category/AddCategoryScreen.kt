package com.alperen.spendcraft.feature.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS AddCategoryView'in birebir Android karşılığı
 * Source: iosApp/SpendCraftiOS/CategoriesView.swift:279-424
 * 
 * Özellikler:
 * - Full screen navigation (dialog değil)
 * - initialType parametresi ile gelir/gider otomatik seçimi
 * - Icon grid selection
 * - Color grid selection
 * - Preview section
 * - Tip değiştirme segmented control
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    initialType: Boolean, // true = income, false = expense
    onSave: (name: String, icon: String, color: String, isIncome: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("cart.fill") }
    var selectedColor by remember { mutableStateOf(IOSColors.Blue) }
    var categoryType by remember { mutableStateOf(if (initialType) "income" else "expense") }
    
    // Debug log
    LaunchedEffect(initialType) {
        android.util.Log.d("AddCategoryScreen", "🔵 Initialized with initialType=$initialType → categoryType=$categoryType")
    }
    
    // iOS: let categoryIcons - CategoriesView.swift:296-301
    val categoryIcons = listOf(
        "cart.fill" to CoreR.drawable.ic_cart_fill,
        "fork.knife" to CoreR.drawable.ic_fork_knife,
        "house.fill" to CoreR.drawable.ic_house_fill,
        "car.fill" to CoreR.drawable.ic_car_fill,
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
        "phone.fill" to CoreR.drawable.ic_phone_fill,
        "tram.fill" to CoreR.drawable.ic_tram_fill
    )
    
    // iOS: let categoryColors - CategoriesView.swift:303-308
    val categoryColors = listOf(
        "Mavi" to IOSColors.Blue,
        "Yeşil" to IOSColors.Green,
        "Kırmızı" to IOSColors.Red,
        "Turuncu" to IOSColors.Orange,
        "Mor" to IOSColors.Purple,
        "Pembe" to IOSColors.Pink,
        "Sarı" to IOSColors.Yellow,
        "Kahverengi" to Color(0xFF8D6E63),
        "İndigo" to Color(0xFF5E35B1),
        "Cyan" to IOSColors.Cyan,
        "Mint" to IOSColors.Mint,
        "Teal" to IOSColors.Teal
    )
    
    Scaffold(
        topBar = {
            // iOS: .navigationTitle("Yeni Kategori") - CategoriesView.swift:399
            TopAppBar(
                title = { Text("Yeni Kategori") },
                navigationIcon = {
                    // iOS: Button("İptal") - CategoriesView.swift:403-405
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }
                },
                actions = {
                    // iOS: implicit save button (kaydet button in form)
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Kategori Bilgileri Section - iOS: Section("Kategori Bilgileri")
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Kategori Bilgileri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Kategori Adı - iOS: TextField("Kategori Adı", text: $categoryName)
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        placeholder = { Text("Kategori Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Kategori Tipi - iOS: Picker("Kategori Tipi", selection: $categoryType)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Kategori Tipi",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // iOS Segmented Control - iOS: .pickerStyle(.segmented)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Gider
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (categoryType == "expense") 
                                            MaterialTheme.colorScheme.primary
                                        else 
                                            Color.Transparent
                                    )
                                    .clickable { categoryType = "expense" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Gider",
                                    color = if (categoryType == "expense") 
                                        Color.White 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Gelir
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (categoryType == "income") 
                                            MaterialTheme.colorScheme.primary
                                        else 
                                            Color.Transparent
                                    )
                                    .clickable { categoryType = "income" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Gelir",
                                    color = if (categoryType == "income") 
                                        Color.White 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            // 2. İkon Seç Section - iOS: Section("İkon Seç")
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "İkon Seç",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // iOS: LazyVGrid(columns: [GridItem(.adaptive(minimum: 50))], spacing: 12)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 6
                    ) {
                        categoryIcons.forEach { (iconName, iconRes) ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedIcon == iconName) {
                                            selectedColor
                                        } else {
                                            selectedColor.copy(alpha = 0.2f)
                                        }
                                    )
                                    .clickable { selectedIcon = iconName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    tint = if (selectedIcon == iconName) {
                                        Color.White
                                    } else {
                                        selectedColor
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 3. Renk Seç Section - iOS: Section("Renk Seç")
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Renk Seç",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // iOS: LazyVGrid(columns: [GridItem(.adaptive(minimum: 60))], spacing: 12)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 6
                    ) {
                        categoryColors.forEach { (colorName, color) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.width(60.dp)
                            ) {
                                // iOS: Circle().fill(colorItem.1)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (selectedColor == color) {
                                                // iOS: .overlay(Circle().stroke(Color.white, lineWidth: 3))
                                                Modifier.border(3.dp, Color.White, CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable { selectedColor = color }
                                )
                                
                                Text(
                                    text = colorName,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // 4. Önizleme Section - iOS: Section("Önizleme")
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Önizleme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Surface(
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
                                    painter = painterResource(
                                        id = categoryIcons.find { it.first == selectedIcon }?.second
                                            ?: CoreR.drawable.ic_circle_fill
                                    ),
                                    contentDescription = null,
                                    tint = selectedColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Text(
                                text = categoryName.ifBlank { "Kategori Adı" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // 5. Kaydet Butonu Section
            item {
                Button(
                    onClick = {
                        // iOS: saveCategory() - CategoriesView.swift:414-423
                        val isIncome = categoryType == "income"
                        android.util.Log.d("AddCategoryScreen", "💾 Save button clicked: categoryType=$categoryType → isIncome=$isIncome")
                        onSave(categoryName, selectedIcon, selectedColor.toHexString(), isIncome)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = categoryName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Kategori Ekle",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    // iOS: .onAppear - CategoriesView.swift:408-410
    LaunchedEffect(Unit) {
        println("📝 AddCategoryScreen appeared with categoryType: $categoryType")
    }
}

// Helper function to convert Color to hex string
private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}
