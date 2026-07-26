package com.harmonicplayer.app.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.harmonicplayer.app.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLibraryScreen(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.dismissNotification()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.dismissNotification()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "HarmonicPlayer",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search songs, artists, albums...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp)
                )

                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    edgePadding = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LibraryTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = { Text(tab.title) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            uiState.currentPlayingSong?.let { playingSong ->
                MiniPlayerBar(
                    song = playingSong,
                    isPlaying = uiState.isPlaying,
                    onPlayPauseClick = { viewModel.togglePlayPause() }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                when (uiState.selectedTab) {
                    LibraryTab.SONGS -> {
                        val filteredSongs = if (uiState.searchQuery.isBlank()) {
                            uiState.songs
                        } else {
                            uiState.songs.filter {
                                it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                        it.artist.contains(uiState.searchQuery, ignoreCase = true) ||
                                        it.album.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }
                        SongsTabContent(
                            songs = filteredSongs,
                            onSongClick = { viewModel.playSong(it) },
                            onSongMenuClick = { viewModel.showAddToPlaylistBottomSheet(it) }
                        )
                    }

                    LibraryTab.ALBUMS -> {
                        val filteredAlbums = if (uiState.searchQuery.isBlank()) {
                            uiState.albums
                        } else {
                            uiState.albums.filter {
                                it.name.contains(uiState.searchQuery, ignoreCase = true) ||
                                        it.artist.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }
                        AlbumsTabContent(
                            albums = filteredAlbums,
                            selectedAlbum = uiState.selectedAlbumDetails,
                            albumSongs = uiState.albumSongs,
                            onAlbumClick = { viewModel.selectAlbum(it) },
                            onBackFromAlbumDetails = { viewModel.selectAlbum(null) },
                            onSongClick = { viewModel.playSong(it) },
                            onSongMenuClick = { viewModel.showAddToPlaylistBottomSheet(it) }
                        )
                    }

                    LibraryTab.ARTISTS -> {
                        val filteredArtists = if (uiState.searchQuery.isBlank()) {
                            uiState.artists
                        } else {
                            uiState.artists.filter {
                                it.name.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }
                        ArtistsTabContent(
                            artists = filteredArtists,
                            selectedArtist = uiState.selectedArtistDetails,
                            artistSongs = uiState.artistSongs,
                            onArtistClick = { viewModel.selectArtist(it) },
                            onBackFromArtistDetails = { viewModel.selectArtist(null) },
                            onSongClick = { viewModel.playSong(it) },
                            onSongMenuClick = { viewModel.showAddToPlaylistBottomSheet(it) }
                        )
                    }

                    LibraryTab.PLAYLISTS -> {
                        val filteredPlaylists = if (uiState.searchQuery.isBlank()) {
                            uiState.playlists
                        } else {
                            uiState.playlists.filter {
                                it.name.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }
                        PlaylistsTabContent(
                            playlists = filteredPlaylists,
                            selectedPlaylist = uiState.selectedPlaylistDetails,
                            playlistSongs = uiState.playlistSongs,
                            onPlaylistClick = { viewModel.selectPlaylist(it) },
                            onBackFromPlaylistDetails = { viewModel.selectPlaylist(null) },
                            onCreatePlaylistClick = { viewModel.showCreatePlaylistDialog(true) },
                            onDeletePlaylist = { viewModel.deletePlaylist(it) },
                            onRemoveSongFromPlaylist = { playlistId, song ->
                                viewModel.removeSongFromPlaylist(playlistId, song)
                            },
                            onSongClick = { viewModel.playSong(it) },
                            onSongMenuClick = { viewModel.showAddToPlaylistBottomSheet(it) }
                        )
                    }

                    LibraryTab.RECENTLY_PLAYED -> {
                        val filteredHistory = if (uiState.searchQuery.isBlank()) {
                            uiState.recentlyPlayed
                        } else {
                            uiState.recentlyPlayed.filter {
                                it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                        it.artist.contains(uiState.searchQuery, ignoreCase = true)
                            }
                        }
                        RecentlyPlayedTabContent(
                            recentlyPlayedSongs = filteredHistory,
                            onSongClick = { viewModel.playSong(it) },
                            onSongMenuClick = { viewModel.showAddToPlaylistBottomSheet(it) }
                        )
                    }
                }
            }

            if (uiState.showCreatePlaylistDialog) {
                CreatePlaylistDialog(
                    onDismiss = { viewModel.showCreatePlaylistDialog(false) },
                    onCreate = { playlistName -> viewModel.createPlaylist(playlistName) }
                )
            }

            uiState.songForAddToPlaylist?.let { song ->
                AddToPlaylistBottomSheet(
                    song = song,
                    playlists = uiState.playlists,
                    onPlaylistSelected = { playlist ->
                        viewModel.addSongToPlaylist(playlist, song)
                    },
                    onCreateNewPlaylistClick = {
                        viewModel.showAddToPlaylistBottomSheet(null)
                        viewModel.showCreatePlaylistDialog(true)
                    },
                    onDismiss = { viewModel.showAddToPlaylistBottomSheet(null) }
                )
            }
        }
    }
}

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.artist} • ${song.album}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
