package com.example.progressbar.dial

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.progressbar.ui.theme.Pink80
import com.example.progressbar.ui.theme.Purple40
import kotlinx.coroutines.delay


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    totalTimeMillis: Long = 10_000, // Total time in ms (represents 100% progress)
    updateIntervalMillis: Long = 16L, // UI update frequency (~20fps, smooth enough)
    onComplete: () -> Unit = {}
) {
    var remainingTime by remember { mutableStateOf(totalTimeMillis) }

    // Progress grows from 0f -> 1f as remainingTime decreases
    val progress = (1f - (remainingTime.toFloat() / totalTimeMillis)).coerceIn(0f, 1f)

    LaunchedEffect(totalTimeMillis) {
        var currentTime = totalTimeMillis
        while (currentTime > 0) {
            delay(updateIntervalMillis)
            currentTime = (currentTime - updateIntervalMillis).coerceAtLeast(0)
            remainingTime = currentTime
        }
        onComplete() // Trigger callback when timer finishes
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.fillMaxWidth(),
        color = Purple40, // main track, time is passed
        trackColor = Pink80 // time which remains
    )
}