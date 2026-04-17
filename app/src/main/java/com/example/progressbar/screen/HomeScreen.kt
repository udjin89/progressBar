package com.example.progressbar.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progressbar.dial.ProgressBar
import com.example.progressbar.dialogs.WarningDialog
import com.example.progressbar.settings.SettingsTimers
import com.example.progressbar.test.SwapableScreen
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.viewmodel.TimerViewModel

@Composable
fun HomeScreen(name: String, modifier : Modifier, viewModel: TimerViewModel = viewModel() ){

    val showTotalDurationDialog = rememberSaveable { mutableStateOf(false) }

    // This column takes up all screen
    Column(modifier = Modifier
        //.border(10.dp, Sage40)
        .background(GreenLight80)) {
        Text(
            text = " $name",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color =  Forest40,
            modifier = modifier
                .fillMaxWidth()
                //.border(1.dp, Forest40)
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
                onTotalTimeClick = { showTotalDurationDialog.value = true })
            if (showTotalDurationDialog.value) {
                Log.i("DBG", "we need show dialog")
                WarningDialog(
                    show = showTotalDurationDialog,
                    title = "Fucking Alert !!!",
                    description = "Test for my dialog",
                    onConfirm = {
                        // ✅ Optional: Run code when user taps OK
//                        viewModel.reset()
                        // Dialog auto-closes via show.value = false inside WarningDialog
                    }
                )
            }
            SwapableScreen()
        }

    }
}





