package com.alperen.spendcraft.feature.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.PremiumGate
import com.alperen.spendcraft.data.repository.AIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
    
    AppScaffold(
        title = "AI Önerileri",
        onBack = onBack,
        showBannerAd = true,
        isPremium = isPremium,
        bannerContent = {
            com.alperen.spendcraft.core.ui.AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                isPremium = isPremium
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isPremium) {
                item {
                    PremiumGate(
                        title = "Premium Özellik",
                        description = "AI önerileri Premium üyeler için sınırsız, diğer kullanıcılar için haftalık 1 öneri hakkı vardır.",
                        onUpgrade = onUpgrade
                    )
                }
            }
            
            // Advice Type Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Öneri Türü Seçin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        AdviceType.values().forEach { adviceType ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedAdviceType == adviceType,
                                        onClick = { selectedAdviceType = adviceType }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAdviceType == adviceType,
                                    onClick = { selectedAdviceType = adviceType }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = adviceType.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = adviceType.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Generate Button
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    val remaining = (weeklyQuota - usedThisWeek).coerceAtLeast(0)
                    Text(text = if (isLoading) "Öneri oluşturuluyor..." else if (isPremium) "AI Önerisi Al (Sınırsız)" else "AI Önerisi Al ($remaining/$weeklyQuota)")
                }
            }
            
            // Error Message
            errorMessage?.let { error ->
                item {
                    ModernCard {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            // AI Advice
            currentAdvice?.let { advice ->
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "AI Önerisi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = advice,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            
            // Usage Info
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kullanım Bilgisi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isPremium) {
                            Text(
                                text = "Premium üyesiniz - Sınırsız AI önerisi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Haftalık $weeklyQuota AI önerisi hakkınız var. Kalan: ${(weeklyQuota - usedThisWeek).coerceAtLeast(0)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
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
