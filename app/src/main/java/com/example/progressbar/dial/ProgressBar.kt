package com.example.progressbar.dial

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.BrightYellow40
import com.example.progressbar.ui.theme.Orange40
import com.example.progressbar.ui.theme.Pink80
import com.example.progressbar.ui.theme.Purple40
import com.example.progressbar.utils.formatDuration
import com.example.progressbar.viewmodel.TimerViewModel


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel,
    onComplete: () -> Unit = {}
) {
    val heightOfBar = 20.dp

    val state by viewModel.state.collectAsState()

    var parentWidthPx by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            //.border(1.dp, YellowGrey40)
            .onSizeChanged { parentWidthPx = it.width }
    ) {
        //val markerPosition = 0.5f
        //Log.e("DBG", "totalDuration: ${state.totalDurationMillis}")
        val markerPosition = ( (3.0 * 60 * 60_000L) / state.totalDurationMillis ).toFloat().coerceIn(0f, 1f)
        Log.e("DBG", "Elapsed: ${formatDuration(state.elapsedMillis)}")

        // 🟢 VERTICAL LINE (BEHIND)
        markerPosition?.let { position ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset {
                        // ✅ Convert Dp → Px inside density context
                        with(density) {
                            val xPx = (markerPosition * parentWidthPx).toInt()
                            val yPx: Int = 0
                            IntOffset(x = xPx, y = yPx)
                        }
                    }
                    .width(2.dp)
                    .height(heightOfBar * 3)
                    .background(Orange40) // ✅ Line color
                    .padding(vertical = 2.dp) // Optional: make line shorter than bar
            )
        }

        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(heightOfBar)
                //.border(1.dp, Green40)
                .background(BrightYellow40),
            color = Purple40, // main track, time is passed
            trackColor = Pink80, // time which remains
            strokeCap = StrokeCap.Butt,
            gapSize = 1.dp,
            drawStopIndicator = {},
            )
    }
}