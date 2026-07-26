package com.harmonicplayer.app.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUriString: String,
    val albumArtUriString: String? = null,
    val isFavorite: Boolean = false
)
