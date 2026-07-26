package com.harmonicplayer.app.util

import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaybackTracker(
    private val playerGetter: () -> Player?,
    private val scope: CoroutineScope,
    private val onProgressUpdated: (position: Long, duration: Long) -> Unit
) {
    private var job: Job? = null

    fun startPolling() {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                val player = playerGetter()
                if (player != null && player.isPlaying) {
                    val currentPos = player.currentPosition.coerceAtLeast(0L)
                    val totalDur = player.duration.coerceAtLeast(0L)
                    onProgressUpdated(currentPos, totalDur)
                }
                delay(250)
            }
        }
    }

    fun stopPolling() {
        job?.cancel()
        job = null
    }
}
