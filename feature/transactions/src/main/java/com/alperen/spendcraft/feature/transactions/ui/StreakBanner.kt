package com.alperen.spendcraft.feature.transactions.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import kotlin.random.Random
import com.alperen.spendcraft.core.model.Streak
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.R as CoreR

@Composable
fun StreakBanner(
    streak: Streak,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTierDialog by remember { mutableStateOf(false) }
    val tier = remember(streak.current) { currentTier(streak.current) }
    val nextTier = remember(streak.current) { nextTierLabel(streak.current) }
    val daysLeft = remember(streak.current) { daysToNext(streak.current) }
    val tierTint = remember(tier) { tierColor(tier) }
    val badgeText = when {
        streak.current >= 30 -> stringResource(CoreR.string.gold_badge)
        streak.current >= 7 -> stringResource(CoreR.string.silver_badge)
        streak.current >= 3 -> stringResource(CoreR.string.bronze_badge)
        else -> null
    }

    ModernCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { showTierDialog = true }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            tierTint.copy(alpha = 0.20f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Alev ikonlu rozet
                Surface(
                    color = if (streak.current > 0) Color(0xFFFFEDD5) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val infinite = rememberInfiniteTransition(label = "streak-infinite")
                        val firePulse = infinite.animateFloat(
                            initialValue = 0.95f,
                            targetValue = 1.08f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "fire-pulse"
                        )
                        Icon(
                            painter = painterResource(CoreR.drawable.ic_fire_vector),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(firePulse.value)
                        )
                        Text(
                            text = "${streak.current} ${stringResource(CoreR.string.day_streak)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // En iyi seri için madalya
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val bestMedal = when (currentTier(streak.best)) {
                            "Platinum" -> CoreR.drawable.ic_medal_platinum
                            "Gold" -> CoreR.drawable.ic_medal_gold
                            "Silver" -> CoreR.drawable.ic_medal_silver
                            "Bronze" -> CoreR.drawable.ic_medal_bronze
                            else -> CoreR.drawable.ic_medal_silver
                        }
                        Icon(
                            painter = painterResource(bestMedal),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${stringResource(CoreR.string.best_streak)} ${streak.best}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
                // Orta: ilerleme ve alt satır içerikleri
                Column(modifier = Modifier.fillMaxWidth()) {
                    val target = ((streak.current % 30).coerceAtLeast(0)) / 30f
                    val progress = animateFloatAsState(
                        targetValue = target,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 150f),
                        label = "streak-progress"
                    )
                    LinearProgressIndicator(
                        progress = progress.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = tierTint
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val currentMedal = when (tier) {
                                    "Platinum" -> CoreR.drawable.ic_medal_platinum
                                    "Gold" -> CoreR.drawable.ic_medal_gold
                                    "Silver" -> CoreR.drawable.ic_medal_silver
                                    "Bronze" -> CoreR.drawable.ic_medal_bronze
                                    else -> CoreR.drawable.ic_medal_bronze
                                }
                                Icon(
                                    painter = painterResource(currentMedal),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tier,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(CoreR.drawable.ic_trophy_vector),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sıradaki: $nextTier (${daysLeft}gün)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            // Shimmer
            val shine = rememberInfiniteTransition(label = "shine")
            val shineX = shine.animateFloat(
                initialValue = -120f,
                targetValue = 520f,
                animationSpec = infiniteRepeatable(animation = tween(2200, easing = LinearEasing)),
                label = "shine-x"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .offset(x = shineX.value.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }

    if (showTierDialog) {
        val dialogMedal = when (tier) {
            "Platinum" -> CoreR.drawable.ic_medal_platinum
            "Gold" -> CoreR.drawable.ic_medal_gold
            "Silver" -> CoreR.drawable.ic_medal_silver
            "Bronze" -> CoreR.drawable.ic_medal_bronze
            else -> CoreR.drawable.ic_medal_bronze
        }
        TierCelebrationDialog(
            tier = tier,
            streak = streak,
            nextTier = nextTier,
            daysLeft = daysLeft,
            medalRes = dialogMedal,
            tierTint = tierTint,
            onDismiss = { showTierDialog = false },
            onConfirm = { showTierDialog = false; onClick() }
        )
    }
}

private fun currentTier(days: Int): String = when {
    days >= 90 -> "Platinum"
    days >= 30 -> "Gold"
    days >= 7 -> "Silver"
    days >= 3 -> "Bronze"
    else -> "New"
}

private fun nextTierLabel(days: Int): String = when {
    days < 3 -> "Bronze"
    days < 7 -> "Silver"
    days < 30 -> "Gold"
    days < 90 -> "Platinum"
    else -> "Platinum+"
}

private fun daysToNext(days: Int): Int = when {
    days < 3 -> 3 - days
    days < 7 -> 7 - days
    days < 30 -> 30 - days
    days < 90 -> 90 - days
    else -> 0
}

private fun tierColor(tier: String): Color = when (tier) {
    "Platinum" -> Color(0xFF9FA8DA)
    "Gold" -> Color(0xFFFFD700)
    "Silver" -> Color(0xFFC0C0C0)
    "Bronze" -> Color(0xFFCD7F32)
    else -> Color(0xFF6C63FF)
}

@Composable
private fun TierCelebrationDialog(
    tier: String,
    streak: Streak,
    nextTier: String,
    daysLeft: Int,
    medalRes: Int,
    tierTint: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                // Arka plan gradient ve efektler
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                0f to tierTint.copy(alpha = 0.16f),
                                1f to MaterialTheme.colorScheme.surface
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Konfeti katmanı (hafif)
                    ConfettiLayer(
                        modifier = Modifier
                            .matchParentSize(),
                        colors = listOf(
                            tierTint,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        ),
                        count = 26
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Başlık
                        Text(
                            text = when (tier) {
                                "Platinum" -> "Platin Madalya!"
                                "Gold" -> "Altın Madalya!"
                                "Silver" -> "Gümüş Madalya!"
                                "Bronze" -> "Bronz Madalya!"
                                else -> "Yeni Seviye!"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Dönen ışınlar + pulsing madalya
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(140.dp)
                        ) {
                            RotatingRays(color = tierTint.copy(alpha = 0.35f))

                            val infinite = rememberInfiniteTransition(label = "medal-pulse")
                            val scale = infinite.animateFloat(
                                initialValue = 0.95f,
                                targetValue = 1.07f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            Icon(
                                painter = painterResource(medalRes),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(100.dp).scale(scale.value)
                            )
                        }

                        // Mesajlar
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tebrikler! ${streak.current} günlük seriye ulaştın.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Sıradaki hedef: $nextTier (${daysLeft} gün)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Buton
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tierTint
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Harika!", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RotatingRays(color: Color) {
    val infinite = rememberInfiniteTransition(label = "rays")
    val angle = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "angle"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = this.center
        val radius = size.minDimension / 2f
        rotate(degrees = angle.value) {
            // 12 ışın
            val rays = 12
            for (i in 0 until rays) {
                val progress = i / rays.toFloat()
                val alpha = 0.2f + 0.5f * progress
                drawLine(
                    color = color.copy(alpha = alpha),
                    start = center,
                    end = androidx.compose.ui.geometry.Offset(
                        x = center.x + radius * kotlin.math.cos(2 * Math.PI * progress).toFloat(),
                        y = center.y + radius * kotlin.math.sin(2 * Math.PI * progress).toFloat()
                    ),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun ConfettiLayer(
    modifier: Modifier,
    colors: List<Color>,
    count: Int = 20
) {
    val particles = remember(colors, count) {
        List(count) {
            ConfettiParticle(
                x = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 1.2f,
                size = 4f + Random.nextFloat() * 8f,
                color = colors[Random.nextInt(colors.size)],
                phase = Random.nextFloat()
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "confetti")
    val t = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2400, easing = LinearEasing)),
        label = "t"
    )
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        particles.forEach { p ->
            val y = ((t.value + p.phase) % 1f) * height
            val x = (p.x * width) + kotlin.math.sin((y / height) * 6f).toFloat() * 12f
            drawCircle(
                color = p.color.copy(alpha = 0.75f),
                radius = p.size,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val phase: Float
)

