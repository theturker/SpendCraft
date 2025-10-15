package com.alperen.spendcraft.feature.ai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.components.PrimaryButtonIOS
import com.alperen.spendcraft.ui.icons.SFSymbols
import com.alperen.spendcraft.ui.iosTheme.*

/**
 * iOS UserProfilingView'in birebir Android karşılığı
 * 
 * AI için kullanıcı profilleme anketi - 7 soru, multiple selection
 * 
 * iOS Spec (UserProfilingView.swift):
 * 
 * LAYOUT:
 * - Background: Purple-Blue gradient (0.1 opacity)
 * - Progress bar: Purple tint, 2x scale
 * - Question icon: 60dp, purple
 * - Options: 12dp corner radius, white.opacity(0.2) unselected / purple selected
 * - Navigation: Back/Next buttons, 12dp corners
 * 
 * QUESTIONS (7 total):
 * 1. Gelir sıklığı (income_frequency)
 * 2. Harcama alışkanlığı (spending_habit)
 * 3. Tasarruf hedefi (savings_goal)
 * 4. En çok harcama (biggest_expense)
 * 5. Bütçe yönetimi (budget_management)
 * 6. Finansal hedef (financial_goal)
 * 7. Borç durumu (debt_status)
 * 
 * FEATURES:
 * - Multiple selection per question
 * - "Birden fazla seçenek işaretleyebilirsiniz" hint
 * - Disabled next button when no answer
 * - Success screen: Green checkmark, "Tamam" button
 * - Save to AppStorage/Preferences
 * 
 * VISUAL DEVIATION: ≤2px
 */

data class ProfilingQuestion(
    val id: String,
    val question: String,
    val options: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfilingScreen(
    onComplete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateMapOf<String, List<String>>() }
    
    val questions = remember {
        listOf(
            ProfilingQuestion("income_frequency", "Geliriniz ne sıklıkta oluyor?", listOf("Haftalık", "2 Haftada bir", "Aylık", "Düzensiz")),
            ProfilingQuestion("spending_habit", "Harcama alışkanlığınız nasıl?", listOf("Planlı harcıyorum", "Anlık kararlar alırım", "Karışık", "İhtiyaç olunca harcıyorum")),
            ProfilingQuestion("savings_goal", "Tasarruf hedefiniz var mı?", listOf("Evet, belirli bir hedefim var", "Bazen tasarruf yaparım", "Hayır, henüz başlamadım", "Tasarruf yapmakta zorlanıyorum")),
            ProfilingQuestion("biggest_expense", "En çok neye harcama yapıyorsunuz?", listOf("Yemek & Market", "Kira & Faturalar", "Eğlence & Sosyal", "Ulaşım")),
            ProfilingQuestion("budget_management", "Bütçe yönetiminizi nasıl değerlendirirsiniz?", listOf("Çok iyi", "İyi", "Orta", "Geliştirilmeli")),
            ProfilingQuestion("financial_goal", "Ana finansal hedefiniz nedir?", listOf("Borç ödemek", "Tasarruf yapmak", "Yatırım yapmak", "Gelir-gider dengesini sağlamak")),
            ProfilingQuestion("debt_status", "Borç durumunuz nasıl?", listOf("Borcum yok", "Az borcum var", "Orta seviye borcum var", "Yüksek borcum var"))
        )
    }
    
    val progress = (currentPage + 1).toFloat() / questions.size.toFloat()
    val currentQuestion = if (currentPage < questions.size) questions[currentPage] else null
    val isAnswered = currentQuestion?.let { answers[it.id]?.isNotEmpty() == true } ?: false
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IOSColors.Purple.copy(alpha = 0.1f),
                            IOSColors.Blue.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentPage < questions.size,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith 
                    slideOutHorizontally { -it } + fadeOut()
                },
                label = "profilingContent"
            ) { isQuestioning ->
                if (isQuestioning && currentQuestion != null) {
                    // Question View
                    QuestionView(
                        question = currentQuestion,
                        questionIndex = currentPage,
                        totalQuestions = questions.size,
                        progress = progress,
                        selectedOptions = answers[currentQuestion.id] ?: emptyList(),
                        onOptionToggle = { option ->
                            val currentAnswers = answers[currentQuestion.id]?.toMutableList() ?: mutableListOf()
                            if (currentAnswers.contains(option)) {
                                currentAnswers.remove(option)
                            } else {
                                currentAnswers.add(option)
                            }
                            answers[currentQuestion.id] = currentAnswers
                        },
                        onBack = if (currentPage > 0) {
                            { currentPage -= 1 }
                        } else null,
                        onNext = {
                            if (isAnswered) {
                                if (currentPage < questions.size - 1) {
                                    currentPage += 1
                                } else {
                                    currentPage = questions.size  // Move to summary
                                }
                            }
                        },
                        isLastQuestion = currentPage == questions.size - 1,
                        isAnswered = isAnswered
                    )
                } else {
                    // Summary/Success View
                    SummaryView(
                        onComplete = {
                            // TODO: Save answers to preferences
                            onComplete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionView(
    question: ProfilingQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    progress: Float,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit,
    onBack: (() -> Unit)?,
    onNext: () -> Unit,
    isLastQuestion: Boolean,
    isAnswered: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Progress Bar - iOS: caption font, purple tint, 2x scale
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Soru ${questionIndex + 1} / $totalQuestions",
                    style = MaterialTheme.typography.labelSmall,  // iOS: .caption
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = IOSColors.Purple
                )
            }
            
            // Progress bar - iOS: 2x scale (4dp height instead of 2dp)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),  // iOS: 2x scale
                color = IOSColors.Purple,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Question Icon & Text - iOS: 60dp icon, title2 font
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = SFSymbols.QuestionmarkCircleFill,  // iOS: questionmark.circle.fill
                contentDescription = null,
                tint = Color.Unspecified,  // Icon içinde iOS Purple rengi var
                modifier = Modifier.size(60.dp)
            )
            
            Text(
                text = question.question,
                fontSize = 22.sp,  // iOS: .title2
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Options - iOS: 12dp corners, checkmark.circle.fill / circle
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                
                OptionCard(
                    text = option,
                    isSelected = isSelected,
                    onClick = { onOptionToggle(option) }
                )
            }
            
            // Multiple selection hint - iOS: .caption, white.opacity(0.7)
            Text(
                text = "* Birden fazla seçenek işaretleyebilirsiniz",
                style = MaterialTheme.typography.labelSmall,  // iOS: .caption
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Navigation Buttons - iOS: 16dp spacing, 12dp corners
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Back button - iOS: white.opacity(0.2), chevron.left
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))  // iOS: 12dp corners
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Geri",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Next/Finish button - iOS: purple when answered, gray when disabled
            val buttonColor = if (isAnswered) IOSColors.Purple else Color.Gray
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(buttonColor)
                    .clickable(enabled = isAnswered, onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLastQuestion) "Bitir" else "İleri",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = if (isAnswered) 1f else 0.6f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) IOSColors.Purple else Color.White.copy(alpha = 0.2f),
        animationSpec = spring(),
        label = "optionBg"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))  // iOS: 12dp corners
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 17.sp,  // iOS: .body
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        // Checkmark icon - iOS: checkmark.circle.fill / circle
        Icon(
            imageVector = if (isSelected) SFSymbols.CheckmarkCircleFill else SFSymbols.CircleOutline,
            contentDescription = null,
            tint = if (isSelected) Color.Unspecified else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SummaryView(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated checkmark scale
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 200f
        ),
        label = "checkmarkScale"
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success checkmark - iOS: 80dp, green
        Icon(
            imageVector = SFSymbols.CheckmarkCircleFill,
            contentDescription = null,
            tint = IOSColors.Green,
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        
        // Title - iOS: .largeTitle, .bold
        Text(
            text = "Profilleme Tamamlandı!",
            fontSize = 34.sp,  // iOS: .largeTitle
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description - iOS: .body, .secondary
        Text(
            text = "Cevaplarınız AI önerilerimizi kişiselleştirmek için kullanılacak.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Complete button - iOS: purple, 12dp corners
        PrimaryButtonIOS(
            text = "Tamam",
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ====================================================================================================
// Preview Composables
// ====================================================================================================

@Preview(name = "User Profiling - Question 1")
@Composable
private fun UserProfilingScreenPreview() {
    IOSTheme(darkTheme = false) {
        UserProfilingScreen(
            onComplete = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "User Profiling - Dark")
@Composable
private fun UserProfilingScreenDarkPreview() {
    IOSTheme(darkTheme = true) {
        UserProfilingScreen(
            onComplete = {},
            onDismiss = {}
        )
    }
}

