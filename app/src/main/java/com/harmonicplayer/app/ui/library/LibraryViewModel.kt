package com.harmonicplayer.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonicplayer.app.data.model.Album
import com.harmonicplayer.app.data.model.Artist
import com.harmonicplayer.app.data.model.Playlist
import com.harmonicplayer.app.data.model.Song
import com.harmonicplayer.app.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.harmonicplayer.app.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                musicRepository.getSongs(),
                musicRepository.getAlbums(),
                musicRepository.getArtists(),
                musicRepository.getPlaylists(),
                musicRepository.getRecentlyPlayed()
            ) { songs, albums, artists, playlists, recentlyPlayed ->
                LibraryUiState(
                    songs = songs,
                    albums = albums,
                    artists = artists,
                    playlists = playlists,
                    recentlyPlayed = recentlyPlayed,
                    selectedTab = _uiState.value.selectedTab,
                    searchQuery = _uiState.value.searchQuery,
                    currentPlayingSong = _uiState.value.currentPlayingSong,
                    isPlaying = _uiState.value.isPlaying,
                    selectedAlbumDetails = _uiState.value.selectedAlbumDetails,
                    albumSongs = _uiState.value.albumSongs,
                    selectedArtistDetails = _uiState.value.selectedArtistDetails,
                    artistSongs = _uiState.value.artistSongs,
                    selectedPlaylistDetails = _uiState.value.selectedPlaylistDetails,
                    playlistSongs = _uiState.value.playlistSongs,
                    showCreatePlaylistDialog = _uiState.value.showCreatePlaylistDialog,
                    songForAddToPlaylist = _uiState.value.songForAddToPlaylist,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                selectedAlbumDetails = null,
                selectedArtistDetails = null,
                selectedPlaylistDetails = null
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            musicRepository.recordRecentlyPlayed(song.id)
            _uiState.update {
                it.copy(
                    currentPlayingSong = song,
                    isPlaying = true,
                    userNotification = "Playing ${song.title}"
                )
            }
        }
    }

    fun togglePlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun selectAlbum(album: Album?) {
        if (album == null) {
            _uiState.update { it.copy(selectedAlbumDetails = null, albumSongs = emptyList()) }
            return
        }
        viewModelScope.launch {
            musicRepository.getSongsForAlbum(album.name).collect { songs ->
                _uiState.update {
                    it.copy(selectedAlbumDetails = album, albumSongs = songs)
                }
            }
        }
    }

    fun selectArtist(artist: Artist?) {
        if (artist == null) {
            _uiState.update { it.copy(selectedArtistDetails = null, artistSongs = emptyList()) }
            return
        }
        viewModelScope.launch {
            musicRepository.getSongsForArtist(artist.name).collect { songs ->
                _uiState.update {
                    it.copy(selectedArtistDetails = artist, artistSongs = songs)
                }
            }
        }
    }

    fun selectPlaylist(playlist: Playlist?) {
        if (playlist == null) {
            _uiState.update { it.copy(selectedPlaylistDetails = null, playlistSongs = emptyList()) }
            return
        }
        viewModelScope.launch {
            musicRepository.getSongsInPlaylist(playlist.id).collect { songs ->
                _uiState.update {
                    it.copy(selectedPlaylistDetails = playlist, playlistSongs = songs)
                }
            }
        }
    }

    fun showCreatePlaylistDialog(show: Boolean) {
        _uiState.update { it.copy(showCreatePlaylistDialog = show) }
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                musicRepository.createPlaylist(name.trim())
                _uiState.update {
                    it.copy(
                        showCreatePlaylistDialog = false,
                        userNotification = "Playlist '$name' created"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to create playlist: ${e.message}") }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                musicRepository.deletePlaylist(playlist.id)
                if (_uiState.value.selectedPlaylistDetails?.id == playlist.id) {
                    _uiState.update { it.copy(selectedPlaylistDetails = null, playlistSongs = emptyList()) }
                }
                _uiState.update {
                    it.copy(userNotification = "Deleted '${playlist.name}'")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete playlist: ${e.message}") }
            }
        }
    }

    fun showAddToPlaylistBottomSheet(song: Song?) {
        _uiState.update { it.copy(songForAddToPlaylist = song) }
    }

    fun addSongToPlaylist(playlist: Playlist, song: Song) {
        viewModelScope.launch {
            try {
                musicRepository.addSongToPlaylist(playlist.id, song.id)
                _uiState.update {
                    it.copy(
                        songForAddToPlaylist = null,
                        userNotification = "Added '${song.title}' to '${playlist.name}'"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add song to playlist: ${e.message}") }
            }
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            try {
                musicRepository.removeSongFromPlaylist(playlistId, song.id)
                _uiState.update {
                    it.copy(userNotification = "Removed '${song.title}' from playlist")
                }
                // Refresh playlist details if currently open
                _uiState.value.selectedPlaylistDetails?.let { currentPlaylist ->
                    selectPlaylist(currentPlaylist)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to remove song: ${e.message}") }
            }
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(userNotification = null, errorMessage = null) }
    }
}
