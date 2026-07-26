package com.harmonicplayer.app.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harmonicplayer.app.data.model.Song

@Composable
fun AllSongsScreen(
    viewModel: LibraryViewModel,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AllSongsContent(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSongClick = onSongClick,
        modifier = modifier
    )
}

@Composable
fun AllSongsContent(
    uiState: LibraryUiState,
    onSearchQueryChanged: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        GlobalSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChanged
        )

        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.songs.isEmpty()) {
            EmptyStateContent(
                searchQuery = uiState.searchQuery
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.songs,
                    key = { song -> song.id }
                ) { song ->
                    SongListItem(
                        song = song,
                        onSongClick = onSongClick
                    )
                }
            }
        }
    }
}
