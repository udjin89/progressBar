package com.example.progressbar.utils

import java.util.Locale

fun formatDuration(totalMillis: Long): String {
    val safeMillis = totalMillis.coerceAtLeast(0L)
    val hours = safeMillis / 3_600_000
    val minutes = (safeMillis % 3_600_000) / 60_000
    val seconds = (safeMillis % 60_000) / 1_000
    val milliseconds = (safeMillis % 1_000) / 10

    return when {
        hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format(Locale.US, "%02d:%02d", minutes, seconds)
        else -> String.format(Locale.US, "%02d.%02d", seconds, milliseconds)
    }
}