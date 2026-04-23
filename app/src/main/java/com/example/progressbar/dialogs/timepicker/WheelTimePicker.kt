package com.example.progressbar.dialogs.timepicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.GreenGrey90
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.ui.theme.MintLight80
import com.example.progressbar.ui.theme.Sage40
import com.example.progressbar.ui.theme.Yellow40
import kotlinx.coroutines.delay

// WheelTimePicker.kt
@Composable
fun WheelTimePicker(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    initialSeconds: Int = 0,
    onTimeChange: (Int, Int, Int) -> Unit,
    onConfirm: (Int, Int, Int) -> Unit,
) {
    var hours by remember { mutableIntStateOf(initialHours) }
    var minutes by remember { mutableIntStateOf(initialMinutes) }
    var seconds by remember { mutableIntStateOf(initialSeconds) }

    Surface (shape = RoundedCornerShape(16.dp),
        color = GreenLight80,
        border = BorderStroke(2.dp, Forest40),
        //modifier = Modifier.padding(20.dp)
        ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ){
            Row(//border = BorderStroke(3.dp, Sage40),
                //modifier = Modifier
                    //.border(
                            //width = 2.dp,
                    //        color = Mint40,
                            //shape = RoundedCornerShape(8.dp
                     //       ), // Скругление углов
                //        )
                    //.padding(20.dp),
                   // .border(1.dp,Forest90),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            )
            {
                // Hours wheel (0-99)
                WheelPicker(
                    range = 0..99,
                    initialValue = hours,
                    onValueChange = { hours = it; onTimeChange(hours, minutes, seconds) },
                    label = "Hours"
                )

                Box(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .height(180.dp)  // ✅ Match WheelPicker's containerHeight
                        .width(15.dp),   // ✅ Fixed width for consistent spacing
                    contentAlignment = Alignment.Center  // ✅ Center colon vertically + horizontally
                ) {
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GreenGrey90  // ✅ Optional: match your theme
                    )
                }

                // Minutes wheel (0-59)
                WheelPicker(
                    range = 0..59,
                    initialValue = minutes,
                    onValueChange = { minutes = it; onTimeChange(hours, minutes, seconds) },
                    label = "Min"
                )

                Box(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .height(180.dp)  // ✅ Match WheelPicker's containerHeight
                        .width(15.dp),   // ✅ Fixed width for consistent spacing
                    contentAlignment = Alignment.Center  // ✅ Center colon vertically + horizontally
                ) {
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GreenGrey90  // ✅ Optional: match your theme
                    )
                }

                // Seconds wheel (0-59)
                WheelPicker(
                    range = 0..59,
                    initialValue = seconds,
                    onValueChange = { seconds = it; onTimeChange(hours, minutes, seconds) },
                    label = "Sec"
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Forest40, // Цвет фона
                    contentColor = MintLight80 // Цвет текста и иконок внутри
                ),
                onClick = {
                    onConfirm(hours, minutes, seconds)
                },
            ) {
                Text("Save")
            }


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
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialValue
    )

    // 🔹 Calculate item height for precise guide positioning
    val itemHeight = 60.dp  // 40dp text + 16dp padding (match your Text modifier)
    val containerHeight = 180.dp
    val guideOffset = (containerHeight - itemHeight) / 2  // Center the selection window

    val selectionCenterY = with(LocalDensity.current) {
        (guideOffset + itemHeight / 2).toPx()
    }

    val extendedList = remember(range) {
        listOf(-1) + range.toList() + listOf(-1)  // -1 = placeholder marker
    }

    val centerItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems: List<LazyListItemInfo> = layoutInfo.visibleItemsInfo

            if (visibleItems.isEmpty()) return@derivedStateOf initialValue

            // Find the item whose center is closest to the center of the selection window
            //val selectionCenterY = guideOffset + itemHeight / 2  // Center of highlighted slot

            visibleItems.minByOrNull { item: LazyListItemInfo ->
                // Calculate distance from item center to selection center
                val itemCenterY = item.offset + item.size / 2f
                kotlin.math.abs(itemCenterY - selectionCenterY)
            }
                ?.index
                ?.takeIf { it in 1 until extendedList.size - 1 }  // Ignore placeholders
                ?.let { extendedList[it] }  // Map back to real value
                ?: initialValue
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
       ) {
        Text(label, style = MaterialTheme.typography.labelMedium)

        // 🔹 Box allows overlaying guides on top of LazyColumn
        Box(
            modifier = Modifier
                .height(containerHeight)
                .width(60.dp)
                //.border(1.dp, Mint40)
        ) {
            // 🔹 Scrollable list
            LazyColumn(
                state = listState,
                flingBehavior = ScrollableDefaults.flingBehavior(),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(extendedList)
                { index, value ->

                    val displayText = if (value == -1) "-" else value.toString().padStart(2, '0')
                    val isPlaceholder = value == -1

                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (value == centerItemIndex)
                                Yellow40
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Dim non-selected
                        ),
                        modifier = Modifier
                            //.border(1.dp,Forest90)
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(vertical = 8.dp)  // 8dp top + 8dp bottom = 16dp total
                            .clickable { onValueChange(value) }
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // 🔹 Selection guides (overlayed on top)
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = guideOffset)  // Position top guide
                    .pointerInput(Unit) { },  // Don't intercept touch events
                thickness = 1.dp,
                color = Sage40
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = guideOffset + itemHeight)  // Position bottom guide
                    .pointerInput(Unit) { },  // Don't intercept touch events
                thickness = 1.dp,
                color = Sage40
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
    LaunchedEffect(centerItemIndex) {
        if (centerItemIndex != -1) {
            onValueChange(centerItemIndex)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val selectionTop = with(LocalDensity.current) { guideOffset.toPx() }
    val selectionBottom = selectionTop + with(LocalDensity.current) { itemHeight.toPx() }
// Add auto-snap when scrolling ends (FIXED for both directions)
    LaunchedEffect(listState.isScrollInProgress) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling) {
                    delay(100L) // Wait for scroll to settle

                    val layoutInfo = listState.layoutInfo
                    val visibleItems = layoutInfo.visibleItemsInfo
                    val targetItem = visibleItems.find { item ->
                        val value = extendedList.getOrNull(item.index)
                        value != null && value != -1 && value == centerItemIndex
                    }

                    if (targetItem != null) {
                        // ✅ Calculate current item center
                        val itemCenterY = targetItem.offset + targetItem.size / 2f

                        // ✅ Calculate offset needed to center the item
                        val scrollDelta = itemCenterY - selectionCenterY

                        // ✅ Only snap if offset is noticeable (> 10px)
                        if (kotlin.math.abs(scrollDelta) > 10f) {
                            // ✅ Scroll by exact delta to center (not scrollToItem!)
                            listState.scrollBy(scrollDelta)
                        }
                    }
                }
            }
    }
}