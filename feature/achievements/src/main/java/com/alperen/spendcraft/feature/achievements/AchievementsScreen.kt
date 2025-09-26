package com.alperen.spendcraft.feature.achievements

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard

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

@Composable
fun AchievementsScreen(
    onBack: () -> Unit = {}
) {
    // Mock data - gerçek uygulamada repository'den gelecek
    val achievements = listOf(
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

    val unlockedAchievements = achievements.filter { it.isUnlocked }
    val lockedAchievements = achievements.filter { !it.isUnlocked }
    
    AppScaffold(
        title = "Başarımlar",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Summary
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Başarım İlerlemesi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AchievementStat(
                                value = unlockedAchievements.size.toString(),
                                label = "Kazanılan",
                                color = MaterialTheme.colorScheme.secondary
                            )
                            AchievementStat(
                                value = achievements.size.toString(),
                                label = "Toplam",
                                color = MaterialTheme.colorScheme.primary
                            )
                            AchievementStat(
                                value = "${(unlockedAchievements.size * 100 / achievements.size)}%",
                                label = "Tamamlama",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            // Unlocked Achievements
            if (unlockedAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = "🏆 Kazanılan Başarımlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(unlockedAchievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = true
                    )
                }
            }

            // Locked Achievements
            if (lockedAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = "🔒 Henüz Kazanılmayan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(lockedAchievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = false
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Achievement Icon with rarity styling
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (isUnlocked) {
                            Brush.radialGradient(
                                colors = getRarityColors(achievement.rarity)
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)
                                )
                            )
                        },
                        CircleShape
                    )
                    .then(
                        if (isUnlocked && achievement.rarity == AchievementRarity.LEGENDARY) {
                            Modifier
                                .border(
                                    2.dp,
                                    Brush.sweepGradient(getRarityColors(achievement.rarity)),
                                    CircleShape
                                )
                                .rotate(shimmerRotation)
                        } else {
                            Modifier
                        }
                    )
            ) {
                Icon(
                    achievement.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (isUnlocked) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    
                    RarityBadge(achievement.rarity)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!isUnlocked && achievement.progress > 0f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "İlerleme",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${achievement.currentProgress}/${achievement.maxProgress}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        LinearProgressIndicator(
                            progress = achievement.progress,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }

                achievement.reward?.let { reward ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎁 $reward",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementStat(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RarityBadge(rarity: AchievementRarity) {
    val (text, colors) = when (rarity) {
        AchievementRarity.COMMON -> "Ortak" to listOf(Color(0xFF9CA3AF), Color(0xFF6B7280))
        AchievementRarity.RARE -> "Nadir" to listOf(Color(0xFF3B82F6), Color(0xFF1E40AF))
        AchievementRarity.EPIC -> "Epik" to listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
        AchievementRarity.LEGENDARY -> "Efsane" to listOf(Color(0xFFF59E0B), Color(0xFFD97706))
    }

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .background(
                Brush.horizontalGradient(colors),
                RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun getRarityColors(rarity: AchievementRarity): List<Color> {
    return when (rarity) {
        AchievementRarity.COMMON -> listOf(Color(0xFF9CA3AF), Color(0xFF6B7280))
        AchievementRarity.RARE -> listOf(Color(0xFF3B82F6), Color(0xFF1E40AF))
        AchievementRarity.EPIC -> listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
        AchievementRarity.LEGENDARY -> listOf(Color(0xFFF59E0B), Color(0xFFD97706))
    }
}
