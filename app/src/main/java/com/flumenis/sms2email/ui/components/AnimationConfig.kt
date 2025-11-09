package com.flumenis.sms2email.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.dp

/**
 * Unified animation configuration for PostaFide app
 * Provides consistent animation behavior across all screens
 */
object AnimationConfig {
    // Duration constants
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500

    // Easing
    val EasingStandard = FastOutSlowInEasing
    val EasingEmphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    // Spring animations
    val SpringDefault = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val SpringSoft = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    )

    // Scale animations
    val scaleUp = tween<Float>(
        durationMillis = DURATION_MEDIUM,
        easing = EasingEmphasized
    )

    val scaleDown = tween<Float>(
        durationMillis = DURATION_SHORT,
        easing = EasingStandard
    )

    // Page transition animations
    fun enterTransition() = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(DURATION_MEDIUM, easing = EasingEmphasized)
    ) + fadeIn(animationSpec = tween(DURATION_MEDIUM))

    fun exitTransition() = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(DURATION_MEDIUM, easing = EasingEmphasized)
    ) + fadeOut(animationSpec = tween(DURATION_MEDIUM))

    fun popEnterTransition() = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(DURATION_MEDIUM, easing = EasingEmphasized)
    ) + fadeIn(animationSpec = tween(DURATION_MEDIUM))

    fun popExitTransition() = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(DURATION_MEDIUM, easing = EasingEmphasized)
    ) + fadeOut(animationSpec = tween(DURATION_MEDIUM))

    // Elevation for pressed state
    val pressedElevation = 8.dp
    val normalElevation = 2.dp
}
