package com.example.progressbar.dial

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.BrightYellow40
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.Forest80
import com.example.progressbar.ui.theme.Forest90
import com.example.progressbar.ui.theme.Orange40
import com.example.progressbar.ui.theme.Pink20
import com.example.progressbar.ui.theme.Yellow40
import com.example.progressbar.viewmodel.TimerViewModel


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel,
    onComplete: () -> Unit = {}
) {
    val heightOfBar = 20.dp

    val state by viewModel.state.collectAsState()
    val duration by viewModel.totalDuration.collectAsState()
    val thresholds by viewModel.thresholds.collectAsState()

    var parentWidthPx by remember { mutableStateOf(0) }

    fun getMarkerColor(threshold: Long): Color {
        return if (duration > 0 && state.elapsedMillis >= threshold) {
            Forest40  // ✅ Passed: green
        } else {
            Orange40  // ⏳ Not reached: orange
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            //.border(1.dp, YellowGrey40)
            .onSizeChanged { parentWidthPx = it.width }
    ) {

        thresholds.forEach { threshold ->
            val markerPosition = ( threshold.toFloat() / duration.toFloat() ).coerceIn(0f, 1f)
            Log.w("DBG", "Add marker: $markerPosition")
            if(threshold <= duration && threshold != 0L){
                val markerColor = getMarkerColor(threshold)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset {
                            with(density) {
                                val xPx = (markerPosition * parentWidthPx).toInt()
                                IntOffset(x = xPx, y = 0)
                            }
                        }
                        .width(2.dp)
                        .height(heightOfBar * 3)  // Taller than bar for visibility
                        .background(markerColor)      // ✅ Marker color
                        .padding(vertical = 2.dp)
                )
            }
        }

// ✅ ADD THIS: Dynamic Gradient Progress Bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(heightOfBar)
                .background(BrightYellow40, RoundedCornerShape(4.dp))  // Track background
        ) {
            val barHeight = size.height
            val barWidth = size.width
            val progressWidth = barWidth * state.progress  // ✅ Width grows with progress

            // ✅ Draw rounded track background (optional, if you want rounded ends)
            drawRoundRect(
                color = Pink20,  // Track color
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // ✅ Draw progress with DYNAMIC gradient (only over filled portion)
            if (state.progress > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Yellow40,   //  Start of filled portion
                            Forest80,   //  Middle
                            Forest40,   //  Later
                            Forest90    //  End of filled portion
                        ),
                        startX = 0f,
                        endX = barWidth  // ✅ Gradient scales with progress!
                    ),
                    size = Size(progressWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }
    }
}