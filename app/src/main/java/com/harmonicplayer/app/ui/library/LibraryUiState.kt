package com.harmonicplayer.app.ui.library

import com.harmonicplayer.app.data.model.Album
import com.harmonicplayer.app.data.model.Artist
import com.harmonicplayer.app.data.model.Playlist
import com.harmonicplayer.app.data.model.Song

enum class LibraryTab(val title: String) {
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists"),
    RECENTLY_PLAYED("Recently Played")
}

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val selectedTab: LibraryTab = LibraryTab.SONGS,
    val searchQuery: String = "",
    val currentPlayingSong: Song? = null,
    val isPlaying: Boolean = false,
    val selectedAlbumDetails: Album? = null,
    val albumSongs: List<Song> = emptyList(),
    val selectedArtistDetails: Artist? = null,
    val artistSongs: List<Song> = emptyList(),
    val selectedPlaylistDetails: Playlist? = null,
    val playlistSongs: List<Song> = emptyList(),
    val showCreatePlaylistDialog: Boolean = false,
    val songForAddToPlaylist: Song? = null,
    val isLoading: Boolean = false,
    val userNotification: String? = null,
    val errorMessage: String? = null
)
