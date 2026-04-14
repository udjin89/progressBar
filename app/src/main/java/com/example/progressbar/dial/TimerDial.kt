package com.example.progressbar.dial

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TimerDial(
    totalDurationMillis: Long,
    remainingMillis: Long,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    // Clamp to prevent negative progress
    val safeRemaining = maxOf(remainingMillis, 0L)
    val progress = if (totalDurationMillis > 0) {
        safeRemaining.toFloat() / totalDurationMillis
    } else 0f

    // ✅ FIX: Removed explicit `State<Float>` type. `by` auto-unwraps to `Float`.
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "TimerDialProgress"
    )

    val sweepAngle = animatedProgress * 360f
    val minutes = safeRemaining / 1000 / 60
    val seconds = (safeRemaining / 1000) % 60
    val timeText = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Box(
        modifier = modifier
            .size(200.dp)
            .semantics { contentDescription = "Timer showing $timeText" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize().padding(16.dp)) {
            val strokeWidth = 24f
            val radius = size.minDimension / 2 - strokeWidth / 2

            // Background track
            drawCircle(
                color = trackColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Progress arc (starts at 12 o'clock)
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        // Center time text
        Text(
            text = timeText,
            style = textStyle,
            textAlign = TextAlign.Center
        )
    }
}