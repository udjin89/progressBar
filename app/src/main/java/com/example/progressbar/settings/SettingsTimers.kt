package com.example.progressbar.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.progressbar.viewmodel.TimerViewModel

@Composable
fun SettingsTimers(
    //settings: AppSettings,
    viewModel: TimerViewModel,
    onThresholdClick: (Long) -> Unit = {},     // Future: edit threshold
    onTotalTimeClick: () -> Unit = {}          // Future: edit total time
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 1️⃣ Total Time Box
        TimeCard(
            title = "Total Duration",
            timeSeconds = (state.totalDurationMillis) / 1000L,
            onClick = onTotalTimeClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        // 🔹 2️⃣ Thresholds Header
        Text(
            text = "Checkpoints / Thresholds",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF3A4A42), // GreenGrey90
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 🔹 3️⃣ Thresholds List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val thresholds = listOf(60L, 150L, 240L)
            itemsIndexed(thresholds) { index, threshold ->
                TimeCard(
                    title = "Threshold ${index + 1}",
                    timeSeconds = threshold,
                    onClick = { onThresholdClick(threshold) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}