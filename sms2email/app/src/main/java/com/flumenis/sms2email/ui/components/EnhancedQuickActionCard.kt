package com.flumenis.sms2email.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Enhanced Quick Action Card with improved animations and feedback
 */
@Composable
fun EnhancedQuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPremium: Boolean = false,
    isLocked: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animated scale with press feedback
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed && !isLocked -> 0.92f
            isLocked -> 0.95f
            else -> 1f
        },
        animationSpec = AnimationConfig.SpringDefault,
        label = "card_scale"
    )

    // Animated icon scale
    val iconScale by animateFloatAsState(
        targetValue = if (isPressed && !isLocked) 0.9f else 1f,
        animationSpec = AnimationConfig.SpringDefault,
        label = "icon_scale"
    )

    // Animated icon color
    val iconColor by animateColorAsState(
        targetValue = when {
            isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            isPremium -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(
            durationMillis = AnimationConfig.DURATION_SHORT,
            easing = AnimationConfig.EasingStandard
        ),
        label = "icon_color"
    )

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.scale(scale),
        interactionSource = interactionSource,
        colors = if (isPremium) {
            CardDefaults.outlinedCardColors(
                containerColor = if (isLocked)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        } else {
            CardDefaults.outlinedCardColors()
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(iconScale),
                        tint = iconColor
                    )

                    // Lock overlay for premium features
                    if (isLocked) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isPremium) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isLocked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                if (isPremium) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (isLocked)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Premium",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLocked)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}
