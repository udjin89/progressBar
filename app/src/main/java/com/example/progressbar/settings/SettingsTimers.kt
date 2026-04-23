package com.example.progressbar.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.progressbar.dialogs.DialogBase
import com.example.progressbar.dialogs.WarningDialogWithCancel
import com.example.progressbar.dialogs.timepicker.WheelTimePicker
import com.example.progressbar.ui.theme.GreenGrey90
import com.example.progressbar.utils.toHours
import com.example.progressbar.utils.toMinutes
import com.example.progressbar.utils.toSeconds
import com.example.progressbar.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsTimers(
    //settings: AppSettings,
    viewModel: TimerViewModel,
    onThresholdClick: (Long) -> Unit = {},     // Future: edit threshold
    onTotalTimeClick: () -> Unit = {}          // Future: edit total time
) {

    val state by viewModel.state.collectAsState()
    val duration by viewModel.totalDuration.collectAsState()
    val thresholds by viewModel.thresholds.collectAsState()

    // ✅ Track which threshold is being edited
    var editingThreshold by remember { mutableStateOf<Pair<Int, Long>?>(null) }
    val showDeleteThreshold = remember { mutableStateOf(false) }
    var pendingDeleteIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 🔹 1️⃣ Total Time Box
        TimeCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            title = "Total Duration",
            timeMillis = duration,
            inRange = true,
            onClick = {
                onTotalTimeClick()
            }
        )

       Row(modifier = Modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.SpaceBetween,
           verticalAlignment = Alignment.CenterVertically
       ){
           Text(
               text = "Checkpoints / Thresholds",
               style = MaterialTheme.typography.titleMedium,
               color = GreenGrey90,
           )

           OutlinedButton(
               onClick = {
                   val current = viewModel.thresholds.value
                   if(current.size < 30){
                       viewModel.viewModelScope.launch {
                           viewModel.saveThresholds(current + 0L)
                       }
                   } else {

                   }

               },
               //modifier = Modifier.padding(top = 16.dp)
           ) {
               Text("+ Add")
           }
       }


        // 🔹 3️⃣ Thresholds List
        LazyColumn(
            //modifier = Modifier.navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                start = 0.dp,
                end = 0.dp,
                bottom = 40.dp  // ✅ No extra padding beyond what innerPadding provides
            )
        ) {
            itemsIndexed(thresholds) { index, threshold ->
                val inRange : Boolean = (threshold <= duration) && threshold != 0L
                TimeCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                        //.border(2.dp, Yellow40),
                    title = "Threshold ${index + 1}",
                    timeMillis = threshold,
                    inRange = inRange,
                    onDelete = {
                        pendingDeleteIndex = index
                        showDeleteThreshold.value = true
                    },
                    onClick = {
                        Log.i("DBG", "Click TimeCard: ${index + 1}")
                        editingThreshold = index to threshold
                    }
                )
            }
        }
// 🔹 Dialog: Edit Threshold Time Picker
        editingThreshold?.let { (index, currentValue) ->
            val showDialog = remember { mutableStateOf(true) }

            // If dialog is dismissed, reset editingThreshold
            if (!showDialog.value) {
                editingThreshold = null
            }

            DialogBase(
                show = showDialog,
                onDismiss = {
                    showDialog.value = false
                    editingThreshold = null  // ✅ Reset on cancel/back
                }
            ) {
                WheelTimePicker(
                    // ✅ Convert current value to h/m/s for picker
                    initialHours = currentValue.toHours(),
                    initialMinutes = currentValue.toMinutes(),
                    initialSeconds = currentValue.toSeconds(),

                    // Optional: live preview while picking
                    onTimeChange = { h, m, s ->
                        // Update preview if needed
                    },

                    // ✅ User tapped Save
                    onConfirm = { h, m, s ->
                        // Convert back to millis
                        val newMillis = ((h * 3600L) + (m * 60L) + s) * 1000L

                        // Save to ViewModel
                        viewModel.viewModelScope.launch {
                            val currentList = viewModel.thresholds.value
                            if (index in currentList.indices) {
                                val newList = currentList.toMutableList()
                                newList[index] = newMillis
                                viewModel.saveThresholds(newList)  // Persists + triggers UI update
                            }
                        }

                        // Close dialog
                        showDialog.value = false
                        editingThreshold = null  // ✅ Reset after save
                    },

                    // ✅ User tapped Cancel or back button
//                    onDismiss = {
//                        showDialog.value = false
//                        editingThreshold = null  // ✅ Reset on cancel
//                    }
                )
            }
        }
    }

    if(showDeleteThreshold.value){
        WarningDialogWithCancel(
            showDeleteThreshold,
            "Deletion",
            "The threshold $pendingDeleteIndex will be deleted !",
            onConfirm = {
                Log.i("DBG", "we have to delete item")
                pendingDeleteIndex?.let { index ->
                    viewModel.viewModelScope.launch {
                        val current = viewModel.thresholds.value
                        if (index in current.indices) {
                            val updated = current.toMutableList()
                            updated.removeAt(index)
                            viewModel.saveThresholds(updated)  // ✅ Persists + triggers UI update
                        }
                    }
                }
                pendingDeleteIndex = null
            },
            onDismiss = {
                pendingDeleteIndex = null
            }
        )
    }


}