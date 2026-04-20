package com.example.progressbar.dialogs.timepicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.Forest90
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.ui.theme.Mint40
import com.example.progressbar.ui.theme.Sage40
import com.example.progressbar.ui.theme.Yellow40

// WheelTimePicker.kt
@Composable
fun WheelTimePicker(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    initialSeconds: Int = 0,
    onTimeChange: (Int, Int, Int) -> Unit
) {
    var hours by remember { mutableIntStateOf(initialHours) }
    var minutes by remember { mutableIntStateOf(initialMinutes) }
    var seconds by remember { mutableIntStateOf(initialSeconds) }

    Surface (shape = RoundedCornerShape(16.dp),
        color = GreenLight80,
        border = BorderStroke(3.dp, Sage40),
        modifier = Modifier.padding(20.dp)
        ){

        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
            // Hours wheel (0-99)
            WheelPicker(
                range = 0..99,
                initialValue = hours,
                onValueChange = { hours = it; onTimeChange(hours, minutes, seconds) },
                label = "Hours"
            )

            Text(":", style = MaterialTheme.typography.headlineMedium)

            // Minutes wheel (0-59)
            WheelPicker(
                range = 0..59,
                initialValue = minutes,
                onValueChange = { minutes = it; onTimeChange(hours, minutes, seconds) },
                label = "Min"
            )

            Text(":", style = MaterialTheme.typography.headlineMedium)

            // Seconds wheel (0-59)
            WheelPicker(
                range = 0..59,
                initialValue = seconds,
                onValueChange = { seconds = it; onTimeChange(hours, minutes, seconds) },
                label = "Sec"
            )
        }
    }

}

@Composable
private fun WheelPicker(
    range: IntRange,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
    label: String
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialValue
    )

    // 🔹 Calculate item height for precise guide positioning
    val itemHeight = 56.dp  // 40dp text + 16dp padding (match your Text modifier)
    val containerHeight = 120.dp
    val guideOffset = (containerHeight - itemHeight) / 2  // Center the selection window

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
       ) {
        Text(label, style = MaterialTheme.typography.labelMedium)

        // 🔹 Box allows overlaying guides on top of LazyColumn
        Box(
            modifier = Modifier
                .height(containerHeight)
                .width(60.dp)
                .border(1.dp, Mint40)
        ) {
            // 🔹 Scrollable list
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                items(range.toList())
                { value ->
                    Text(
                        text = value.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (value == listState.firstVisibleItemIndex)
                                Yellow40
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Dim non-selected
                        ),
                        modifier = Modifier
                            .border(1.dp,Forest90)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)  // 8dp top + 8dp bottom = 16dp total
                            .clickable { onValueChange(value) },
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 🔹 Selection guides (overlayed on top)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = guideOffset)  // Position top guide
                    .pointerInput(Unit) { },  // Don't intercept touch events
                color = Sage40,
                thickness = 1.dp
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = guideOffset + itemHeight)  // Position bottom guide
                    .pointerInput(Unit) { },  // Don't intercept touch events
                color = Sage40,
                thickness = 1.dp
            )

            // 🔹 Optional: Subtle highlight for selected item
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.TopCenter)
                    .offset(y = guideOffset)
                    .background(Yellow40.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    //.pointerInput(Unit) { }  // Don't intercept touch events
            )
        }
    }

    // 🔹 Update value when center item changes
    LaunchedEffect(listState.firstVisibleItemIndex) {
        onValueChange(listState.firstVisibleItemIndex)
    }
}