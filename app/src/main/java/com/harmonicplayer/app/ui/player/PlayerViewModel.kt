package com.harmonicplayer.app.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonicplayer.app.util.AudioVisualizerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val audioSessionId: Int = 0,
    val trackTitle: String = "Harmonic Soundscape",
    val artistName: String = "Pitch Symphony"
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val visualizerManager: AudioVisualizerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val visualizerBins: StateFlow<FloatArray> = visualizerManager.fftState

    fun attachAudioSession(sessionId: Int) {
        _uiState.value = _uiState.value.copy(audioSessionId = sessionId)
        visualizerManager.attachAudioSession(sessionId)
    }

    fun play() {
        _uiState.value = _uiState.value.copy(isPlaying = true)
        visualizerManager.setEnabled(true)
    }

    fun pause() {
        _uiState.value = _uiState.value.copy(isPlaying = false)
        visualizerManager.setEnabled(false)
    }

    fun togglePlayback() {
        if (_uiState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun nextTrack(newSessionId: Int, title: String) {
        _uiState.value = _uiState.value.copy(
            audioSessionId = newSessionId,
            trackTitle = title,
            isPlaying = true
        )
        visualizerManager.attachAudioSession(newSessionId)
        visualizerManager.setEnabled(true)
    }

    fun onScreenResumed() {
        if (_uiState.value.isPlaying) {
            visualizerManager.setEnabled(true)
        }
    }

    fun onScreenPaused() {
        visualizerManager.setEnabled(false)
    }

    override fun onCleared() {
        super.onCleared()
        visualizerManager.release()
    }
}
