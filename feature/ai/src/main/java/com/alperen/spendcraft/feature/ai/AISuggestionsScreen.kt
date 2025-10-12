package com.alperen.spendcraft.feature.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import com.alperen.spendcraft.core.ui.*
import com.alperen.spendcraft.data.repository.AIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * iOS AISuggestionsView'in birebir Android karşılığı
 * 
 * Özellikler:
 * - Gradient background
 * - Sparkles icon (gradient renkli)
 * - Advice type selection (card based)
 * - Financial stats cards (4 tane)
 * - AI advice display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISuggestionsScreen(
    aiRepository: AIRepository,
    categoryBreakdown: Map<String, Long>,
    totalExpense: Long,
    income: Long,
    expenses: Long,
    savings: Long,
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var currentAdvice by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedAdviceType by remember { mutableStateOf<AdviceType?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var weeklyQuota by remember { mutableStateOf(2) }
    var usedThisWeek by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        aiRepository.getUsageInfo().collect { info ->
            weeklyQuota = info.weeklyQuota
            usedThisWeek = info.usedThisWeek
        }
    }
    
    // Scroll behavior ekleyerek iOS gibi collapsible davranış sağlıyoruz
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Scroll oranına göre text boyutunu ayarlayalım
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = androidx.compose.ui.unit.lerp(
        start = 32.sp,
        stop = 22.sp,
        fraction = collapsedFraction
    )
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "AI Önerileri",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                            contentDescription = "Geri"
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
        // iOS-style gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            IOSColors.Purple.copy(alpha = 0.1f),
                            IOSColors.Blue.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header with Sparkles Icon
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Gradient Sparkles Icon
                        Icon(
                            painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_sparkles),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = IOSColors.Purple
                        )
                        
                        Text(
                            text = "AI Finansal Danışman",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Yapay zeka destekli kişiselleştirilmiş finansal öneriler",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Advice Type Selection - iOS Card Style
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Öneri Türü Seçin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        AdviceType.values().forEach { adviceType ->
                            IOSAdviceTypeCard(
                                adviceType = adviceType,
                                isSelected = selectedAdviceType == adviceType,
                                onClick = { selectedAdviceType = adviceType }
                            )
                        }
                    }
                }
            
                // Generate Button - iOS Style
                item {
                    Button(
                        onClick = {
                        if (selectedAdviceType != null) {
                            isLoading = true
                            errorMessage = null
                            currentAdvice = null
                            
                            coroutineScope.launch {
                                try {
                                    val result = when (selectedAdviceType) {
                                        AdviceType.SPENDING_ANALYSIS -> {
                                            aiRepository.generateSpendingAdvice(categoryBreakdown, totalExpense)
                                        }
                                        AdviceType.BUDGET_OPTIMIZATION -> {
                                            aiRepository.generateBudgetAdvice(income, expenses, savings)
                                        }
                                        AdviceType.SAVINGS_ADVICE -> {
                                            aiRepository.generateAdvice("Tasarruf yapmak için bana öneriler ver. Gelirim: ${income/100} TL, Giderim: ${expenses/100} TL, Tasarrufum: ${savings/100} TL")
                                        }
                                        null -> return@launch
                                    }
                                    
                                    if (result.isSuccess) {
                                        currentAdvice = result.getOrNull()
                                        errorMessage = null
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Bilinmeyen hata oluştu"
                                        currentAdvice = null
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Bilinmeyen hata oluştu"
                                    currentAdvice = null
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                        enabled = selectedAdviceType != null && !isLoading && (isPremium || (weeklyQuota - usedThisWeek) > 0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedAdviceType == null) Color.Gray else IOSColors.Blue,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_sparkles),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoading) "Öneri oluşturuluyor..." else "AI Önerisi Al",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            
                // AI Advice Display - iOS Style
                currentAdvice?.let { advice ->
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_lightbulb_fill),
                                        contentDescription = null,
                                        tint = IOSColors.Yellow,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "AI Önerisi",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Text(
                                    text = advice,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
                
                // Error Message
                errorMessage?.let { error ->
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = IOSColors.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = IOSColors.Red
                                )
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IOSColors.Red
                                )
                            }
                        }
                    }
                }
                
                // Financial Stats Cards - iOS Style (4 cards in 2x2 grid)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Finansal Durum Özeti",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // First Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FinancialStatCard(
                                title = "Gelir",
                                value = "₺${income / 100}",
                                icon = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_down_circle_fill,
                                color = IOSColors.Green,
                                modifier = Modifier.weight(1f)
                            )
                            
                            FinancialStatCard(
                                title = "Gider",
                                value = "₺${expenses / 100}",
                                icon = com.alperen.spendcraft.core.ui.R.drawable.ic_arrow_up_circle_fill,
                                color = IOSColors.Red,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // Second Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FinancialStatCard(
                                title = "Bakiye",
                                value = "₺${(income - expenses) / 100}",
                                icon = com.alperen.spendcraft.core.ui.R.drawable.ic_banknote,
                                color = IOSColors.Blue,
                                modifier = Modifier.weight(1f)
                            )
                            
                            FinancialStatCard(
                                title = "Kategori",
                                value = "${categoryBreakdown.size}",
                                icon = com.alperen.spendcraft.core.ui.R.drawable.ic_folder_fill,
                                color = IOSColors.Orange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// iOS-style Advice Type Card
@Composable
private fun IOSAdviceTypeCard(
    adviceType: AdviceType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (adviceType) {
        AdviceType.SPENDING_ANALYSIS -> listOf(IOSColors.Blue, IOSColors.Purple)
        AdviceType.BUDGET_OPTIMIZATION -> listOf(IOSColors.Green, IOSColors.Blue)
        AdviceType.SAVINGS_ADVICE -> listOf(IOSColors.Purple, IOSColors.Pink)
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        shadowElevation = if (isSelected) 8.dp else 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle with Gradient
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.linearGradient(colors = gradientColors)
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.2f),
                                    Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = when (adviceType) {
                            AdviceType.SPENDING_ANALYSIS -> com.alperen.spendcraft.core.ui.R.drawable.ic_chart_bar_fill
                            AdviceType.BUDGET_OPTIMIZATION -> com.alperen.spendcraft.core.ui.R.drawable.ic_pie_chart_vector
                            AdviceType.SAVINGS_ADVICE -> com.alperen.spendcraft.core.ui.R.drawable.ic_banknote
                        }
                    ),
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = adviceType.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = adviceType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            
            // Checkmark
            if (isSelected) {
                Icon(
                    painter = painterResource(id = com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill),
                    contentDescription = null,
                    tint = IOSColors.Blue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// iOS-style Financial Stat Card
@Composable
private fun FinancialStatCard(
    title: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class AdviceType(
    val title: String,
    val description: String
) {
    SPENDING_ANALYSIS(
        "Harcama Analizi",
        "Kategori bazında harcama alışkanlıklarınızı analiz eder"
    ),
    BUDGET_OPTIMIZATION(
        "Bütçe Optimizasyonu",
        "Gelir-gider dengesini optimize etmek için öneriler verir"
    ),
    SAVINGS_ADVICE(
        "Tasarruf Önerileri",
        "Daha fazla tasarruf yapmak için pratik öneriler sunar"
    )
}

