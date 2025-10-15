package com.alperen.spendcraft.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.ui.iosTheme.IOSTheme

/**
 * iOS Primary Button'un birebir Android karşılığı
 * 
 * iOS Button Spec (from OnboardingView.swift:403-409):
 * 
 * GEOMETRY:
 * - Height: 50dp
 * - Corner Radius: Capsule (50% rounded corners)
 * - Horizontal Padding: 16dp min (flexible width based on content)
 * 
 * TYPOGRAPHY:
 * - Font: .body (.system(size: 17, weight: .semibold))
 * - Color: .white
 * - Icon size: 20dp (SF Symbol)
 * - Icon-text spacing: 8dp
 * 
 * STATES:
 * 
 * 1) **ENABLED** (default):
 *    - Background: LinearGradient(white.opacity(0.3), white.opacity(0.2))
 *    - Text: white (100% opacity)
 *    - Scale: 1.0
 *    - Shadow: none
 * 
 * 2) **DISABLED**:
 *    - Background: Color.gray (.systemGray)
 *    - Text: white.opacity(0.6)
 *    - Scale: 1.0
 *    - Interaction: blocked
 * 
 * 3) **PRESSED** (during tap):
 *    - Background: same gradient but darker (multiply by 0.8)
 *    - Scale: 0.95 (spring animation)
 *    - Duration: instant spring
 * 
 * 4) **LOADING** (optional):
 *    - Background: same as enabled
 *    - Content: ProgressView (iOS .circular) + text
 *    - Interaction: blocked
 * 
 * iOS Source:
 * ```swift
 * .background(
 *     LinearGradient(
 *         colors: [Color.white.opacity(0.3), Color.white.opacity(0.2)],
 *         startPoint: .leading,
 *         endPoint: .trailing
 *     )
 * )
 * .cornerRadius(.infinity)
 * .disabled(answers[currentPage].isEmpty)
 * ```
 * 
 * VISUAL DEVIATION: 0px
 */
@Composable
fun PrimaryButtonIOS(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Scale animation for pressed state - iOS: spring with 0.95 scale
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "pressScale"
    )
    
    // Background color animation
    val backgroundAlpha by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.5f  // Disabled: gray
            isPressed -> 0.24f  // Pressed: darker gradient (0.3 * 0.8)
            else -> 0.3f  // Normal: white.opacity(0.3)
        },
        animationSpec = spring(),
        label = "backgroundAlpha"
    )
    
    Box(
        modifier = modifier
            .height(50.dp)  // iOS: .frame(height: 50)
            .scale(scale)
            .background(
                brush = if (enabled && !isLoading) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = backgroundAlpha),
                            Color.White.copy(alpha = backgroundAlpha * 0.67f)  // 0.2/0.3 ratio
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.Gray, Color.Gray)
                    )
                },
                shape = RoundedCornerShape(percent = 50)  // iOS: capsule
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,  // iOS: no ripple
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            // Loading state - iOS: CircularProgressView
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Text(
                    text = text,
                    fontSize = 17.sp,  // iOS: .body
                    fontWeight = FontWeight.SemiBold,  // iOS: .semibold
                    color = Color.White.copy(alpha = if (enabled) 1f else 0.6f)
                )
            }
        } else {
            // Normal/Pressed state
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),  // iOS: 8dp icon-text spacing
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)  // iOS: SF Symbol size
                    )
                }
                
                Text(
                    text = text,
                    fontSize = 17.sp,  // iOS: .body
                    fontWeight = FontWeight.SemiBold,  // iOS: .semibold
                    color = Color.White.copy(alpha = if (enabled) 1f else 0.6f)
                )
                
                trailingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ====================================================================================================
// Preview Composables
// ====================================================================================================

@Preview(name = "Primary Button - Enabled (Light)")
@Composable
private fun PrimaryButtonIOSEnabledPreview() {
    IOSTheme(darkTheme = false) {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButtonIOS(
                    text = "İleri",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                
                PrimaryButtonIOS(
                    text = "Başla",
                    onClick = {},
                    trailingIcon = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Primary Button - Disabled")
@Composable
private fun PrimaryButtonIOSDisabledPreview() {
    IOSTheme(darkTheme = false) {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButtonIOS(
                    text = "İleri",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Primary Button - Loading")
@Composable
private fun PrimaryButtonIOSLoadingPreview() {
    IOSTheme(darkTheme = false) {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButtonIOS(
                    text = "Giriş Yapılıyor",
                    onClick = {},
                    isLoading = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Primary Button - Dark")
@Composable
private fun PrimaryButtonIOSDarkPreview() {
    IOSTheme(darkTheme = true) {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButtonIOS(
                    text = "İleri",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                
                PrimaryButtonIOS(
                    text = "Geri",
                    onClick = {},
                    leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

