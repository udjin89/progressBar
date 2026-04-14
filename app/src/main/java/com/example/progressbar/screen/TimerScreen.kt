package com.example.progressbar.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.progressbar.dial.TimerDial
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay

@Composable
fun TimerScreen() {
    val totalDuration = 60_000L // 60 seconds
    var remaining by remember { mutableLongStateOf(totalDuration) }
    var isRunning by remember { mutableStateOf(false) } //initial state

    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (remaining > 0 && isActive) {
            delay(1000L)
            remaining -= 1000L
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDial(
            totalDurationMillis = totalDuration,
            remainingMillis = remaining,
            modifier = Modifier.size(220.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(if (isRunning) "Pause" else "Resume")
        }
    }
}