package com.alperen.spendcraft.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.core.ui.CurrencyFormatter
import com.alperen.spendcraft.core.ui.R as CoreR

/**
 * iOS CategoriesView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Category list with icons and colors
 * - Budget information per category
 * - Progress bars for budget tracking
 * - Add category button
 * - Tap to add/edit budget
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSCategoriesScreen(
    categories: List<Category>,
    budgets: Map<Long, Double> = emptyMap(), // categoryId to budget limit
    spent: Map<Long, Double> = emptyMap(), // categoryId to spent amount
    onAddCategory: (name: String, icon: String, color: String) -> Unit,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Kategoriler",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_plus_circle_fill),
                            contentDescription = "Kategori Ekle",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryRow(
                    category = category,
                    budgetLimit = budgets[category.id],
                    spentAmount = spent[category.id] ?: 0.0,
                    onClick = {
                        selectedCategory = category
                        showAddBudgetDialog = true
                    }
                )
            }
        }
    }
    
    // Add Budget Dialog
    if (showAddBudgetDialog && selectedCategory != null) {
        AddBudgetDialog(
            category = selectedCategory!!,
            currentBudget = budgets[selectedCategory!!.id],
            onDismiss = {
                showAddBudgetDialog = false
                selectedCategory = null
            },
            onSave = { amount ->
                // TODO: Save budget
                showAddBudgetDialog = false
                selectedCategory = null
            }
        )
    }
    
    // Add Category Dialog - iOS AddCategoryView
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave = { name: String, icon: String, color: String ->
                onAddCategory(name, icon, color)
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    budgetLimit: Double?,
    spentAmount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    val progress = if (budgetLimit != null && budgetLimit > 0) {
        (spentAmount / budgetLimit).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon - iOS'taki category.icon kullanımı
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = getCategoryIconResource(category.icon ?: "circle.fill")),
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Category Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (budgetLimit != null) {
                        Text(
                            text = "Bütçe: ${CurrencyFormatter.format(context, (budgetLimit * 100).toLong())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Bütçe yok",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Spent Amount
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.format(context, (spentAmount * 100).toLong()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (budgetLimit != null) {
                        val progressPercent = (progress * 100).toInt()
                        val progressColor = when {
                            progress > 0.9 -> IOSColors.Red
                            progress > 0.7 -> IOSColors.Orange
                            else -> IOSColors.Green
                        }
                        
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.bodySmall,
                            color = progressColor
                        )
                    }
                }
            }
            
            // Budget Progress Bar
            if (budgetLimit != null) {
                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        progress > 0.9 -> IOSColors.Red
                        progress > 0.7 -> IOSColors.Orange
                        else -> IOSColors.Green
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * iOS AddCategoryView - Grid layout for icons and colors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("cart.fill") }
    var selectedColor by remember { mutableStateOf(IOSColors.Blue) }
    
    // iOS'taki category icons - SF Symbol isimleri
    val categoryIcons = listOf(
        "cart.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_cart_fill,
        "fork.knife" to com.alperen.spendcraft.core.ui.R.drawable.ic_fork_knife,
        "house.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_house_fill,
        "car.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_car_fill,
        "airplane" to com.alperen.spendcraft.core.ui.R.drawable.ic_airplane,
        "bolt.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_bolt_fill,
        "bag.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_bag_fill,
        "gift.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_gift_fill,
        "book.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_book_fill,
        "gamecontroller.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_gamecontroller_fill,
        "film.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_film_fill,
        "heart.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_heart_fill,
        "creditcard.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_creditcard_fill,
        "pills.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_pills_fill,
        "briefcase.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_briefcase_fill,
        "graduationcap.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_graduationcap_fill,
        "phone.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_phone_fill,
        "tram.fill" to com.alperen.spendcraft.core.ui.R.drawable.ic_tram_fill
    )
    
    // iOS'taki category colors
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
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }
                    
                    Text(
                        text = "Yeni Kategori",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(
                        onClick = {
                            if (categoryName.isNotBlank()) {
                                onSave(categoryName, selectedIcon, selectedColor.toHexString())
                            }
                        },
                        enabled = categoryName.isNotBlank()
                    ) {
                        Text("Kaydet")
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Scrollable Content
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Category Name
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Kategori Bilgileri",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            OutlinedTextField(
                                value = categoryName,
                                onValueChange = { categoryName = it },
                                placeholder = { Text("Kategori Adı") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                    
                    // Icon Selection - Grid Layout
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "İkon Seç",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Grid of icons
                            androidx.compose.foundation.layout.FlowRow(
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
                    
                    // Color Selection - Grid Layout
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Renk Seç",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Grid of colors
                            androidx.compose.foundation.layout.FlowRow(
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
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .then(
                                                    if (selectedColor == color) {
                                                        Modifier.border(
                                                            3.dp,
                                                            Color.White,
                                                            CircleShape
                                                        )
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
                    
                    // Preview
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                    ?: com.alperen.spendcraft.core.ui.R.drawable.ic_category_vector
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
                }
            }
        }
    }
}

// Helper function to convert Color to hex string
private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

/**
 * Category icon mapping - Android category verilerindeki emoji iconları ve iOS SF Symbol isimlerini Android drawable'lara çevirir
 */
private fun getCategoryIconResource(icon: String): Int {
    return when (icon) {
        // Emoji iconlar (veritabanında saklanıyor)
        "🍔" -> CoreR.drawable.ic_fork_knife // Yemek
        "🚌" -> CoreR.drawable.ic_tram_fill // Ulaşım
        "🎬" -> CoreR.drawable.ic_film_fill // Eğlence
        "💼" -> CoreR.drawable.ic_briefcase_fill // Maaş / İş
        "🛒" -> CoreR.drawable.ic_cart_fill // Alışveriş
        "🏠" -> CoreR.drawable.ic_house_fill // Ev
        "🚗" -> CoreR.drawable.ic_car_fill // Araba
        "✈️" -> CoreR.drawable.ic_airplane // Uçak / Seyahat
        "⚡" -> CoreR.drawable.ic_bolt_fill // Elektrik / Faturalar
        "👜" -> CoreR.drawable.ic_bag_fill // Çanta / Moda
        "🎁" -> CoreR.drawable.ic_gift_fill // Hediye
        "📚" -> CoreR.drawable.ic_book_fill // Kitap / Eğitim
        "🎮" -> CoreR.drawable.ic_gamecontroller_fill // Oyun
        "❤️" -> CoreR.drawable.ic_heart_fill // Sağlık / Sevgi
        "💳" -> CoreR.drawable.ic_creditcard_fill // Kredi Kartı
        "💊" -> CoreR.drawable.ic_pills_fill // İlaç / Sağlık
        "🎓" -> CoreR.drawable.ic_graduationcap_fill // Eğitim
        "📱" -> CoreR.drawable.ic_phone_fill // Telefon / İletişim
        
        // iOS SF Symbol isimleri (fallback)
        "cart.fill" -> CoreR.drawable.ic_cart_fill
        "fork.knife" -> CoreR.drawable.ic_fork_knife
        "house.fill" -> CoreR.drawable.ic_house_fill
        "car.fill" -> CoreR.drawable.ic_car_fill
        "tram.fill" -> CoreR.drawable.ic_tram_fill
        "airplane" -> CoreR.drawable.ic_airplane
        "bolt.fill" -> CoreR.drawable.ic_bolt_fill
        "bag.fill" -> CoreR.drawable.ic_bag_fill
        "gift.fill" -> CoreR.drawable.ic_gift_fill
        "book.fill" -> CoreR.drawable.ic_book_fill
        "gamecontroller.fill" -> CoreR.drawable.ic_gamecontroller_fill
        "film.fill" -> CoreR.drawable.ic_film_fill
        "heart.fill" -> CoreR.drawable.ic_heart_fill
        "creditcard.fill" -> CoreR.drawable.ic_creditcard_fill
        "pills.fill" -> CoreR.drawable.ic_pills_fill
        "briefcase.fill" -> CoreR.drawable.ic_briefcase_fill
        "graduationcap.fill" -> CoreR.drawable.ic_graduationcap_fill
        "phone.fill" -> CoreR.drawable.ic_phone_fill
        "circle.fill" -> CoreR.drawable.ic_circle_fill
        
        else -> CoreR.drawable.ic_circle_fill // Default
    }
}

@Composable
private fun AddBudgetDialog(
    category: Category,
    currentBudget: Double?,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    val context = LocalContext.current
    var budgetAmount by remember { 
        mutableStateOf(currentBudget?.toString() ?: "") 
    }
    
    val categoryColor = remember(category.color) {
        try {
            Color(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            IOSColors.Blue
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = CoreR.drawable.ic_category_vector),
                        contentDescription = null,
                        tint = categoryColor
                    )
                    Text(category.name)
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aylık Bütçe Limiti",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = { budgetAmount = it },
                    placeholder = { Text("0.00") },
                    trailingIcon = { Text("₺") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    budgetAmount.toDoubleOrNull()?.let { onSave(it) }
                },
                enabled = budgetAmount.toDoubleOrNull() != null
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

