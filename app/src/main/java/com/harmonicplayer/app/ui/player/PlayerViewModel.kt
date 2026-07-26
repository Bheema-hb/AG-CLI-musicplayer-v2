package com.harmonicplayer.app.ui.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.harmonicplayer.app.data.model.Song
import com.harmonicplayer.app.data.repository.MusicRepository
import com.harmonicplayer.app.service.MusicPlaybackService
import com.harmonicplayer.app.util.PlaybackTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var tracker: PlaybackTracker? = null
    private var favoriteObservationJob: Job? = null
    private var playlist: List<Song> = emptyList()
    private var currentSongIndex: Int = 0

    init {
        initializeMediaController()
        loadSongs()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                try {
                    val controller = controllerFuture?.get()
                    if (controller != null) {
                        bindMediaController(controller)
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(errorMessage = "Failed to connect to media service: ${e.localizedMessage}") }
                }
            },
            context.mainExecutor
        )
    }

    fun bindMediaController(controller: MediaController) {
        this.mediaController = controller
        controller.addListener(playerListener)

        tracker = PlaybackTracker(
            playerGetter = { mediaController },
            scope = viewModelScope,
            onProgressUpdated = { pos, dur ->
                _uiState.update {
                    it.copy(
                        currentPlaybackPosition = pos,
                        totalDuration = if (dur > 0L) dur else it.totalDuration
                    )
                }
            }
        )

        updateUiFromPlayer()
        if (controller.isPlaying) {
            tracker?.startPolling()
        }
    }

    private fun updateUiFromPlayer() {
        val controller = mediaController ?: return
        _uiState.update {
            it.copy(
                isPlaying = controller.isPlaying,
                currentPlaybackPosition = controller.currentPosition.coerceAtLeast(0L),
                totalDuration = if (controller.duration > 0L) controller.duration else it.totalDuration
            )
        }
    }

    private fun loadSongs() {
        viewModelScope.launch {
            musicRepository.getSongs().collectLatest { songs ->
                playlist = songs
                if (songs.isNotEmpty() && _uiState.value.currentSong == null) {
                    val firstSong = songs[0]
                    _uiState.update {
                        it.copy(
                            currentSong = firstSong,
                            totalDuration = firstSong.duration
                        )
                    }
                    observeFavoriteState(firstSong.id)
                    preparePlaylistInController()
                }
            }
        }
    }

    private fun preparePlaylistInController() {
        val controller = mediaController ?: return
        if (playlist.isEmpty()) return

        val mediaItems = playlist.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(Uri.parse(song.contentUriString))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .apply {
                            song.albumArtUriString?.let { setArtworkUri(Uri.parse(it)) }
                        }
                        .build()
                )
                .build()
        }

        controller.setMediaItems(mediaItems)
        controller.prepare()
    }

    private fun observeFavoriteState(songId: Long) {
        favoriteObservationJob?.cancel()
        favoriteObservationJob = viewModelScope.launch {
            musicRepository.isFavorite(songId).collectLatest { isFav ->
                _uiState.update { currentState ->
                    if (currentState.currentSong?.id == songId) {
                        currentState.copy(
                            isFavorite = isFav,
                            currentSong = currentState.currentSong?.copy(isFavorite = isFav)
                        )
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                tracker?.startPolling()
            } else {
                tracker?.stopPolling()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val mediaId = mediaItem?.mediaId?.toLongOrNull() ?: return
            val song = playlist.find { it.id == mediaId } ?: return
            currentSongIndex = playlist.indexOf(song)
            _uiState.update {
                it.copy(
                    currentSong = song,
                    totalDuration = song.duration
                )
            }
            observeFavoriteState(song.id)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val controller = mediaController ?: return
            if (playbackState == Player.STATE_READY && controller.duration > 0L) {
                _uiState.update { it.copy(totalDuration = controller.duration) }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    errorMessage = "Playback error: ${error.localizedMessage ?: "Failed to play audio URI"}"
                )
            }
            tracker?.stopPolling()
        }
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.mediaItemCount == 0 && playlist.isNotEmpty()) {
                preparePlaylistInController()
            }
            controller.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        val targetPos = positionMs.coerceIn(0L, _uiState.value.totalDuration)
        controller.seekTo(targetPos)
        _uiState.update { it.copy(currentPlaybackPosition = targetPos) }
    }

    fun skipToNext() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        } else if (playlist.isNotEmpty()) {
            val nextIndex = (currentSongIndex + 1) % playlist.size
            currentSongIndex = nextIndex
            val nextSong = playlist[nextIndex]
            playSong(nextSong)
        }
    }

    fun skipToPrevious() {
        val controller = mediaController ?: return
        if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else if (playlist.isNotEmpty()) {
            val prevIndex = if (currentSongIndex - 1 < 0) playlist.size - 1 else currentSongIndex - 1
            currentSongIndex = prevIndex
            val prevSong = playlist[prevIndex]
            playSong(prevSong)
        }
    }

    fun toggleFavorite() {
        val currentSong = _uiState.value.currentSong ?: return
        val isFav = _uiState.value.isFavorite
        viewModelScope.launch {
            if (isFav) {
                musicRepository.removeFavorite(currentSong.id)
            } else {
                musicRepository.markFavorite(currentSong.id)
            }
        }
    }

    fun playSong(song: Song) {
        val index = playlist.indexOfFirst { it.id == song.id }
        if (index != -1) {
            currentSongIndex = index
            val controller = mediaController
            if (controller != null) {
                if (controller.mediaItemCount == 0) {
                    preparePlaylistInController()
                }
                controller.seekTo(index, 0L)
                controller.play()
            }
            _uiState.update {
                it.copy(
                    currentSong = song,
                    totalDuration = song.duration,
                    currentPlaybackPosition = 0L,
                    isPlaying = true
                )
            }
            observeFavoriteState(song.id)
        }
    }

    override fun onCleared() {
        tracker?.stopPolling()
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        super.onCleared()
    }
}
