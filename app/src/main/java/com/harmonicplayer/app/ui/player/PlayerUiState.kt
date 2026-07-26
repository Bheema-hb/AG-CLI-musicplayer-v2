package com.harmonicplayer.app.ui.player

import com.harmonicplayer.app.data.model.Song

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPlaybackPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)
