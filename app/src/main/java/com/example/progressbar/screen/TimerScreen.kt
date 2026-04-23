package com.example.progressbar.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.progressbar.dial.TimerDial
import com.example.progressbar.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val haptic = LocalHapticFeedback.current
    val state by viewModel.state.collectAsState()

    var isRunning by remember { mutableStateOf(false) } //initial state
    val duration by viewModel.totalDuration.collectAsState()


    Column(
        modifier = Modifier.wrapContentHeight(Alignment.CenterVertically).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDial(
            //viewModel = viewModel,
            totalDurationMillis = duration,
            elapsedMillis = state.elapsedMillis,
           // modifier = Modifier.border(1.dp, Yellow40)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                isRunning = !isRunning
                viewModel.onClickButton()
                      },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(
                text = when {
                    state.isFinished -> "Reset"      // ✅ Highest priority
                    state.isRunning -> "Pause"        // ✅ Second
                    else -> "Start"                   // ✅ Default
                }
            )
        }
    }

    LaunchedEffect(isRunning) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}