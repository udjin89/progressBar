package com.example.progressbar.test

// AnimationSpecFactory.kt
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

fun getTransitionSpec(type: AnimationType): ContentTransform {
    return when (type) {
        AnimationType.Fade -> ContentTransform(
            targetContentEnter = fadeIn(tween(250)),
            initialContentExit = fadeOut(tween(250))
        )

        AnimationType.SwipeLeft -> ContentTransform(
            targetContentEnter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            initialContentExit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        )

        AnimationType.SwipeRight -> ContentTransform(
            targetContentEnter = slideInHorizontally(
                initialOffsetX = { it },  // Start off-screen RIGHT
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            initialContentExit = slideOutHorizontally(
                targetOffsetX = { -it },  // Exit off-screen LEFT
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        )

        AnimationType.Scale -> ContentTransform(
            targetContentEnter = scaleIn(initialScale = 0.8f) + fadeIn(tween(200)),
            initialContentExit = scaleOut(targetScale = 1.2f) + fadeOut(tween(200))
        )

        AnimationType.SlideUp -> ContentTransform(
            targetContentEnter = slideInVertically(initialOffsetY = { it }),
            initialContentExit = slideOutVertically(targetOffsetY = { -it })
        )

        is AnimationType.CustomSwipe -> {
            val (enterFromLeft, duration, easing) = type
            ContentTransform(
                targetContentEnter = slideInHorizontally(
                    initialOffsetX = { if (enterFromLeft) -it else it },
                    animationSpec = tween(duration, easing = easing)
                ),
                initialContentExit = slideOutHorizontally(
                    targetOffsetX = { if (enterFromLeft) it else -it },
                    animationSpec = tween(duration, easing = easing)
                )
            )
        }
    }
}