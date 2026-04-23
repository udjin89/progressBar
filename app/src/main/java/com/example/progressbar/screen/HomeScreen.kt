package com.example.progressbar.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progressbar.dial.ProgressBar
import com.example.progressbar.dialogs.DialogBase
import com.example.progressbar.dialogs.WarningDialog
import com.example.progressbar.dialogs.timepicker.WheelTimePicker
import com.example.progressbar.settings.SettingsTimers
import com.example.progressbar.test.SwapableScreen
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.utils.toHMS
import com.example.progressbar.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(name: String, modifier : Modifier, viewModel: TimerViewModel = viewModel() ){

    val showTotalDurationDialog = rememberSaveable { mutableStateOf(false) }
    val showRestartConfirmation = remember { mutableStateOf(false) }
    val showAlertCantSave = remember { mutableStateOf(false) }
    var pendingTotalDuration by remember { mutableLongStateOf(0L) }

    val duration by viewModel.totalDuration.collectAsState()

    // This column takes up all screen
    Column(modifier = Modifier
        .fillMaxSize()
        //.border(10.dp, Sage40)
        .background(GreenLight80)) {
        Text(
            text = " $name",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color =  Forest40,
            modifier = modifier
                .fillMaxWidth()
                .background(GreenLight80)
                .padding(0.dp, 5.dp)
        )
        // Above Column has some space after Text
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
        ) {
            ProgressBar(
                modifier = Modifier
                    //.border(5.dp, BrightYellow40)
                    .padding(5.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                viewModel = viewModel,
            )
            TimerScreen(viewModel = viewModel)
            SettingsTimers(
                viewModel = viewModel,
                onTotalTimeClick = { showTotalDurationDialog.value = true }
            )

            if (showTotalDurationDialog.value) {
                Log.i("DBG", "we need show dialog")
                DialogBase(
                    show = showTotalDurationDialog,
                    onDismiss = {},
                    content = {

                        val currentDuration = duration.toHMS()

                        WheelTimePicker(
                            initialHours = currentDuration.first,
                            initialMinutes = currentDuration.second,
                            initialSeconds = currentDuration.third,
                            onTimeChange = { o,t,th ->  },
                            onConfirm = { h, m, s ->
                                Log.i("DBG", "✅ onConfirm called: ${h}h ${m}m ${s}s")
                                val totalMillis = ((h * 3600L) + (m * 60L) + s) * 1000L
                                if(totalMillis.toInt() == 0){
                                    showAlertCantSave.value = true
                                } else {
                                    pendingTotalDuration = totalMillis
                                    Log.i("DBG", "✅ Dismissing dialog. totalMillis = $totalMillis")
                                    showRestartConfirmation.value = true
                                }
                            }
                        )
                    }
                )
            }
            if (showRestartConfirmation.value) {
                AlertDialog(
                    onDismissRequest = { showRestartConfirmation.value = false },
                    title = { Text("Restart Timer?") },
                    text = { Text("Changing duration will reset the current timer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.saveTotalDuration(pendingTotalDuration)
                                // Optional: reset timer if running
                                viewModel.resetInitialState()
                            }
                            showRestartConfirmation.value = false
                            showTotalDurationDialog.value = false  // Ensure both closed
                        }) {
                            Text("Yes, Restart", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showRestartConfirmation.value = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            if(showAlertCantSave.value){
                WarningDialog(
                    showAlertCantSave,
                    "Alert",
                    "Can't set up a zero timer!",
                    onConfirm = {}
                )
            }
            SwapableScreen()
        }

    }
}





