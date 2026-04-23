package com.example.progressbar.utils

/** Extract hours (0-99) from total milliseconds */
fun Long.toHours(): Int =
    (this / 3_600_000L).toInt().coerceIn(0, 99)

/** Extract minutes (0-59) from total milliseconds */
fun Long.toMinutes(): Int =
    ((this % 3_600_000L) / 60_000L).toInt().coerceIn(0, 59)

/** Extract seconds (0-59) from total milliseconds */
fun Long.toSeconds(): Int =
    ((this % 60_000L) / 1_000L).toInt().coerceIn(0, 59)

/** Convert all at once — efficient for single call */
fun Long.toHMS(): Triple<Int, Int, Int> {
    val totalSeconds = (this / 1000L).toInt().coerceAtLeast(0)
    val hours = (totalSeconds / 3600).coerceIn(0, 99)
    val minutes = ((totalSeconds % 3600) / 60).coerceIn(0, 59)
    val seconds = (totalSeconds % 60).coerceIn(0, 59)
    return Triple(hours, minutes, seconds)
}