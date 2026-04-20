package com.example.progressbar.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.progressbar.dialogs.DialogBase
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
            .wrapContentHeight(Alignment.CenterVertically).fillMaxWidth()
            .padding(16.dp)
    ) {
        // 🔹 1️⃣ Total Time Box
        TimeCard(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).clickable{
                onTotalTimeClick()
            },
            title = "Total Duration",
            timeMillis = (state.totalDurationMillis),
        )

        // 🔹 2️⃣ Thresholds Header
        Text(
            text = "Checkpoints / Thresholds",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF3A4A42), // GreenGrey90
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 🔹 1. State: tracks WHICH threshold is being edited (index + value)
        var editingThreshold by remember { mutableStateOf<Pair<Int, Long>?>(null) }

        // 🔹 3️⃣ Thresholds List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val thresholds = listOf(60L, 150L, 240L)
            itemsIndexed(thresholds) { index, threshold ->
                TimeCard(
                    modifier = Modifier.fillMaxWidth().clickable{
                        Log.i("DBG", "Click TimeCard: ${index + 1}")
                        editingThreshold = index to threshold
                        //onThresholdClick(threshold)
                                                                },
                    title = "Threshold ${index + 1}",
                    timeMillis = threshold,
                )
            }
        }
        editingThreshold?.let { (index, value) ->
            // Dialog visibility state (persists across recompositions)
            val showDialog = remember { mutableStateOf(true) }
            // Auto-clear selection when dialog closes
            if (!showDialog.value) editingThreshold = null

            DialogBase(
                show = showDialog,
                onDismiss = { editingThreshold = null }, // Clear on back/outside tap
                content = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Hello! ${index} and ${value}👋")
                        }
                    }
                }
            )
        }
    }
}