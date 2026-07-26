package com.harmonicplayer.app.data.model

data class Album(
    val name: String,
    val artist: String,
    val songCount: Int,
    val albumArtUriString: String? = null
)
