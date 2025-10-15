package com.alperen.spendcraft.feature.achievements

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.IOSColors
// import com.alperen.spendcraft.ui.iosTheme.*  // Note: IOSTheme in app module

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: Painter,
    val category: AchievementCategory,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f, // 0f to 1f
    val maxProgress: Int = 1,
    val currentProgress: Int = 0,
    val reward: String? = null,
    val rarity: AchievementRarity = AchievementRarity.COMMON
)

enum class AchievementCategory {
    TRACKING, // İşlem kaydetme
    BUDGETING, // Bütçe yönetimi
    SAVING, // Tasarruf
    STREAK, // Süreklilik
    PREMIUM, // Premium özellikler
    SOCIAL // Sosyal özellikler
}

enum class AchievementRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

/**
 * iOS'taki AchievementsListView'in birebir aynısı
 * ScrollView + VStack + Total Points Card + LazyVGrid (2 column) + AchievementCardLarge
 */
@Composable
fun AchievementsScreen(
    onBack: () -> Unit = {},
    viewModel: AchievementsViewModel? = null
) {
    // Gerçek veri - ViewModel'den gelecek
    val achievements by (viewModel?.achievements ?: remember { 
        kotlinx.coroutines.flow.MutableStateFlow(emptyList<com.alperen.spendcraft.data.db.entities.AchievementEntity>()) 
    }).collectAsState(initial = emptyList())
    
    val totalPoints by (viewModel?.totalPoints ?: remember { 
        kotlinx.coroutines.flow.MutableStateFlow(0) 
    }).collectAsState(initial = 0)
    
    val unlockedCount by (viewModel?.unlockedCount ?: remember { 
        kotlinx.coroutines.flow.MutableStateFlow(0) 
    }).collectAsState(initial = 0)

    // Mock data - gerçek uygulamada repository'den gelecek
    val mockAchievements = listOf(
        // Tracking Achievements
        Achievement(
            id = "first_transaction",
            title = "İlk Adım",
            description = "İlk işleminizi kaydettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_play_arrow_vector),
            category = AchievementCategory.TRACKING,
            isUnlocked = true,
            progress = 1f,
            currentProgress = 1,
            maxProgress = 1,
            reward = "+10 XP",
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "transaction_100",
            title = "İşlem Ustası",
            description = "100 işlem kaydettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_assignment_vector),
            category = AchievementCategory.TRACKING,
            isUnlocked = true,
            progress = 1f,
            currentProgress = 100,
            maxProgress = 100,
            reward = "+50 XP",
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "transaction_1000",
            title = "Kayıt Şampiyonu",
            description = "1000 işlem kaydettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_emoji_events_vector),
            category = AchievementCategory.TRACKING,
            isUnlocked = false,
            progress = 0.65f,
            currentProgress = 650,
            maxProgress = 1000,
            reward = "+100 XP + Premium Badge",
            rarity = AchievementRarity.EPIC
        ),
        
        // Streak Achievements
        Achievement(
            id = "streak_7",
            title = "Hafta Savaşçısı",
            description = "7 gün üst üste işlem kaydettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_local_fire_department_vector),
            category = AchievementCategory.STREAK,
            isUnlocked = true,
            progress = 1f,
            currentProgress = 7,
            maxProgress = 7,
            reward = "+30 XP",
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "streak_30",
            title = "Ay Kralı",
            description = "30 gün üst üste işlem kaydettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_whatshot_vector),
            category = AchievementCategory.STREAK,
            isUnlocked = false,
            progress = 0.4f,
            currentProgress = 12,
            maxProgress = 30,
            reward = "+100 XP + Streak Master Badge",
            rarity = AchievementRarity.EPIC
        ),
        
        // Budget Achievements
        Achievement(
            id = "first_budget",
            title = "Bütçe Planlayıcısı",
            description = "İlk bütçenizi oluşturdunuz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_account_balance_vector),
            category = AchievementCategory.BUDGETING,
            isUnlocked = true,
            progress = 1f,
            currentProgress = 1,
            maxProgress = 1,
            reward = "+20 XP",
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "budget_keeper",
            title = "Bütçe Koruyucusu",
            description = "3 ay üst üste bütçenizi aşmadınız",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_shield_vector),
            category = AchievementCategory.BUDGETING,
            isUnlocked = false,
            progress = 0.33f,
            currentProgress = 1,
            maxProgress = 3,
            reward = "+75 XP + Budget Master Badge",
            rarity = AchievementRarity.RARE
        ),
        
        // Saving Achievements
        Achievement(
            id = "first_saving",
            title = "Tasarruf Başlangıcı",
            description = "İlk defa aylık gelir > gider",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_savings_vector),
            category = AchievementCategory.SAVING,
            isUnlocked = false,
            progress = 0.8f,
            currentProgress = 800,
            maxProgress = 1000,
            reward = "+40 XP",
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "saving_master",
            title = "Tasarruf Ustası",
            description = "10.000 TL tasarruf ettiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_monetization_on_vector),
            category = AchievementCategory.SAVING,
            isUnlocked = false,
            progress = 0.23f,
            currentProgress = 2300,
            maxProgress = 10000,
            reward = "+200 XP + Golden Saver Badge",
            rarity = AchievementRarity.LEGENDARY
        ),
        
        // Premium Achievements
        Achievement(
            id = "premium_user",
            title = "Premium Üye",
            description = "Premium üyeliğe geçtiniz",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_workspace_premium_vector),
            category = AchievementCategory.PREMIUM,
            isUnlocked = false,
            progress = 0f,
            currentProgress = 0,
            maxProgress = 1,
            reward = "+100 XP + Premium Crown",
            rarity = AchievementRarity.EPIC
        ),
        Achievement(
            id = "ai_user",
            title = "AI Keşifçisi",
            description = "AI önerilerini 10 kez kullandınız",
            icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
            category = AchievementCategory.PREMIUM,
            isUnlocked = false,
            progress = 0.3f,
            currentProgress = 3,
            maxProgress = 10,
            reward = "+60 XP",
            rarity = AchievementRarity.RARE
        )
    )
    
    // Gerçek veri kullan, yoksa mock data
    val displayAchievements = if (achievements.isNotEmpty()) {
        achievements.map { entity ->
            Achievement(
                id = entity.id.toString(),
                title = entity.name,
                description = entity.description,
                icon = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_trophy_vector),
                category = when (entity.category) {
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.SPENDING -> AchievementCategory.TRACKING
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.BUDGET -> AchievementCategory.BUDGETING
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.SAVING -> AchievementCategory.SAVING
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.STREAK -> AchievementCategory.STREAK
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.CATEGORY -> AchievementCategory.TRACKING
                    com.alperen.spendcraft.data.db.entities.AchievementCategory.SPECIAL -> AchievementCategory.PREMIUM
                },
                isUnlocked = entity.isUnlocked,
                progress = if (entity.maxProgress > 0) entity.progress.toFloat() / entity.maxProgress else 0f,
                currentProgress = entity.progress,
                maxProgress = entity.maxProgress,
                reward = "+${entity.points} XP",
                rarity = when (entity.points) {
                    in 0..25 -> AchievementRarity.COMMON
                    in 26..50 -> AchievementRarity.RARE
                    in 51..100 -> AchievementRarity.EPIC
                    else -> AchievementRarity.LEGENDARY
                }
            )
        }
    } else {
        mockAchievements
    }

    // iOS'taki AchievementsListView yapısı
    AppScaffold(
        title = "Başarılar",
        onBack = onBack
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Points Card - iOS'taki gibi crown icon ile
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(IOSColors.Yellow.copy(alpha = 0.1f))
                        .padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Crown icon - iOS'taki gibi 50dp
                    Icon(
                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_emoji_events_vector),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = IOSColors.Yellow
                    )
                    
                    // Total points - iOS'taki .title, .bold
                    Text(
                        text = "$totalPoints Puan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Achievement count - iOS'taki .subheadline, .secondary
                    Text(
                        text = "$unlockedCount / ${displayAchievements.size} Başarı",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Achievements Grid - iOS'taki LazyVGrid(columns: 2)
            item {
                // Grid layout with 2 columns
                val chunkedAchievements = displayAchievements.chunked(2)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    chunkedAchievements.forEach { rowAchievements ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowAchievements.forEach { achievement ->
                                AchievementCardLarge(
                                    achievement = achievement,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Eğer tek achievement varsa, sağ tarafı boş bırak
                            if (rowAchievements.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * iOS'taki AchievementCardLarge'ın birebir aynısı
 * Grid layout için kullanılan büyük kart
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementCardLarge(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    var showDetailSheet by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // iOS'taki minHeight: 200
            .clip(RoundedCornerShape(16.dp)) // iOS'taki cornerRadius: 16
            .background(
                if (achievement.isUnlocked)
                    IOSColors.Yellow.copy(alpha = 0.1f)
                else
                    Color.Gray.copy(alpha = 0.1f)
            )
            .then(
                // iOS'taki border - unlock olanlar için yellow, 2dp
                if (achievement.isUnlocked) {
                    Modifier.border(
                        width = 2.dp,
                        color = IOSColors.Yellow,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { showDetailSheet = true }
            .padding(16.dp), // iOS'taki .padding()
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp) // iOS'taki spacing: 12
    ) {
        // Icon - iOS'taki 40dp
        Icon(
            achievement.icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = if (achievement.isUnlocked) IOSColors.Yellow else Color.Gray
        )
        
        // Title - iOS'taki .subheadline, .semibold
        Text(
            text = achievement.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        
        // Description - iOS'taki .caption, .secondary
        Text(
            text = achievement.description,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        
        // Points or Progress - iOS'taki gibi
        if (achievement.isUnlocked) {
            // Star icon + points
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp), // iOS'taki .caption2 size
                    tint = IOSColors.Yellow
                )
                Text(
                    text = "${achievement.currentProgress} Puan",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Progress bar + progress text
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { achievement.progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF007AFF), // iOS blue
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )
                Text(
                    text = "${achievement.currentProgress}/${achievement.maxProgress}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    // iOS'taki AchievementDetailSheet ile birebir aynı
    if (showDetailSheet) {
        AchievementDetailSheet(
            achievement = achievement,
            onDismiss = { showDetailSheet = false }
        )
    }
}


/**
 * iOS'taki AchievementDetailSheet'in birebir aynısı
 * presentationDetents ile .height(400) kullanıyor iOS'ta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementDetailSheet(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val progressPercentage = if (achievement.maxProgress > 0) {
        achievement.progress
    } else {
        0f
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 500.dp)
        ) {
            // Header with close button - iOS'taki gibi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_xmark_circle_fill),
                        contentDescription = "Kapat",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Icon and Status
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Icon with gradient background - iOS'taki gibi 120x120
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            // Background circle
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (achievement.isUnlocked) {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFFBBF24).copy(alpha = 0.3f),
                                                    Color(0xFFF59E0B).copy(alpha = 0.3f)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Gray.copy(alpha = 0.2f),
                                                    Color.Gray.copy(alpha = 0.1f)
                                                )
                                            )
                                        },
                                        CircleShape
                                    )
                            )

                            // Icon
                            Icon(
                                achievement.icon,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = if (achievement.isUnlocked) {
                                    Color(0xFFFBBF24) // Yellow
                                } else {
                                    Color.Gray
                                }
                            )
                        }

                        // Status badge - iOS'taki "Tamamlandı!" yeşil badge
                        if (achievement.isUnlocked) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_checkmark_circle_fill),
                                    contentDescription = null,
                                    tint = Color(0xFF34C759), // iOS green
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Tamamlandı!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF34C759)
                                )
                            }
                        }
                    }
                }

                // Title - iOS'taki title2, bold, center aligned
                item {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Description - iOS'taki body, secondary color, center aligned
                item {
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Progress or Points
                item {
                    if (achievement.isUnlocked) {
                        // Points card - iOS'taki sarı background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_star_vector),
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "${achievement.reward ?: "0 Puan"} Kazandınız!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Progress section - iOS'taki gibi
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Progress Bar - iOS'taki mavi-mor gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF007AFF).copy(alpha = 0.05f))
                                    .padding(16.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Progress header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "İlerleme",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${achievement.currentProgress} / ${achievement.maxProgress}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF007AFF)
                                        )
                                    }

                                    // Progress bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressPercentage)
                                                .fillMaxHeight()
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFF007AFF),
                                                            Color(0xFFAF52DE)
                                                        )
                                                    )
                                                )
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }

                                    // Progress footer
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${(progressPercentage * 100).toInt()}% Tamamlandı",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Kalan: ${achievement.maxProgress - achievement.currentProgress}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFF9500), // iOS orange
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Reward info - iOS'taki mor background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFAF52DE).copy(alpha = 0.1f))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_gift_fill),
                                        contentDescription = null,
                                        tint = Color(0xFFAF52DE),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Kazanacağınız: ${achievement.reward ?: "0 Puan"}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
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

// ====================================================================================================
// Preview Composables
// ====================================================================================================

@Preview(name = "Achievements - Light")
@Composable
private fun AchievementsScreenPreview() {
    AchievementsScreen(onBack = {})
}
