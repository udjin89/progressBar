package com.example.progressbar.dial

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.Green40
import com.example.progressbar.ui.theme.Pink80
import com.example.progressbar.ui.theme.Purple40
import com.example.progressbar.viewmodel.TimerViewModel


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel,
    totalTimeMillis: Long = 10_000, // Total time in ms (represents 100% progress)
    updateIntervalMillis: Long = 16L, // UI update frequency (~20fps, smooth enough)
    onComplete: () -> Unit = {}
) {

    val state by viewModel.state.collectAsState()



    LaunchedEffect(totalTimeMillis) {
        onComplete() // Trigger callback when timer finishes
    }

    LinearProgressIndicator(
        progress = { state.progress },
        modifier = modifier.fillMaxWidth()
            .height(20.dp)
            .border(1.dp, Green40)
,
        color = Purple40, // main track, time is passed
        trackColor = Pink80, // time which remains
        strokeCap = StrokeCap.Butt,
        gapSize = 2.dp,
        drawStopIndicator = {},

    )
}