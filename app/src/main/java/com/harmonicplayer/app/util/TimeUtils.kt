package com.harmonicplayer.app.util

import java.util.concurrent.TimeUnit

fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0) return "00:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
