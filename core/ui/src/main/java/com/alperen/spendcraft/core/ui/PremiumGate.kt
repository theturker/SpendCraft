package com.alperen.spendcraft.core.ui

import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PremiumGate(
    title: String,
    description: String,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModernCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = SpendCraftTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = SpendCraftTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SpendCraftTheme.colors.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = SpendCraftTheme.typography.bodyMedium,
                color = SpendCraftTheme.colors.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onUpgrade,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Upgrade,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Premium'a Geç")
            }
        }
    }
}

@Composable
fun PremiumFeatureCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isPremium) {
        content()
    } else {
        ModernCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    icon()
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            style = SpendCraftTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = description,
                            style = SpendCraftTheme.typography.bodyMedium,
                            color = SpendCraftTheme.colors.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = SpendCraftTheme.colors.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Premium'a Geç")
                }
            }
        }
    }
}
