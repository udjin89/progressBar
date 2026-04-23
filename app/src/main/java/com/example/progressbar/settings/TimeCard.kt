package com.example.progressbar.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.GreenGrey80
import com.example.progressbar.ui.theme.GreenGrey90
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.utils.formatDuration

@Composable
fun TimeCard(
    modifier: Modifier = Modifier,
    title: String,
    timeMillis: Long,
    inRange: Boolean,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
        if (onDelete != null){
        val dismissState = rememberSwipeToDismissBoxState(
            positionalThreshold = { totalDistance -> totalDistance * 0.25f }, // 25% swipe to trigger
            confirmValueChange = { dismissValue ->
                when(dismissValue) {
                    SwipeToDismissBoxValue.StartToEnd -> true
                    SwipeToDismissBoxValue.EndToStart -> false
                    SwipeToDismissBoxValue.Settled -> true
                }
            }
        )

            var hasTriggeredDelete by remember { mutableStateOf(false) }

        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = {

                val direction = dismissState.dismissDirection
                if (direction != SwipeToDismissBoxValue.StartToEnd) return@SwipeToDismissBox

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                        .padding(end = 20.dp, start = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            content = { TimeCardContent(title, timeMillis, inRange, onClick) },
        )

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd && !hasTriggeredDelete) {
                Log.i("DBG", "SWIPE !!!")
                hasTriggeredDelete = true
                onDelete.invoke()
                dismissState.reset()
            }
            if (dismissState.currentValue == SwipeToDismissBoxValue.Settled) {
                hasTriggeredDelete = false
            }
        }
    } else {
        Box() { //modifier = Modifier.border(3.dp, Orange60)
            TimeCardContent(title, timeMillis, inRange, onClick)
        }
    }
}

@Composable
private fun TimeCardContent(
    title: String,
    timeMillis: Long,
    inRange: Boolean,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(GreenLight80, RoundedCornerShape(12.dp))
            .border(1.dp, GreenGrey80, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = GreenGrey90
            )
            Text(
                text = formatDuration(timeMillis),
                style = MaterialTheme.typography.titleMedium,
                color = if (inRange) Forest40 else GreenGrey90.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}