package com.harmonicplayer.app.ui.library

import com.harmonicplayer.app.data.model.Song

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
