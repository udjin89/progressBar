package com.example.progressbar.test

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing

sealed interface AnimationType {
    object Fade : AnimationType
    object SwipeLeft : AnimationType          // Enter: left → Exit: right
    object SwipeRight : AnimationType         // Enter: right → Exit: left
    object Scale : AnimationType
    object SlideUp : AnimationType

    // Configurable variants
    data class CustomSwipe(
        val enterFromLeft: Boolean = true,    // true = enter from left, false = enter from right
        val durationMillis: Int = 300,
        val easing: Easing = FastOutSlowInEasing
    ) : AnimationType
}