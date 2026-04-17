package com.example.progressbar.utils

import java.util.Locale

fun formatDuration(totalSeconds: Long): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0L)
    val hours = safeSeconds / 3600
    val minutes = (safeSeconds % 3600) / 60
    val seconds = safeSeconds % 60

    return when {
        hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format(Locale.US, "%02d:%02d", minutes, seconds)
        else -> String.format(Locale.US, "%02d", seconds)
    }
}