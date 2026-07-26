package com.harmonicplayer.app.util

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface contract for the Audio Visualizer Engine.
 * Manages binding to Android's native Visualizer audio effect API
 * and exposes processed 8-bin frequency/pitch magnitudes.
 */
interface AudioVisualizerManager {
    /**
     * Reactive state of 8 normalized frequency/pitch bin magnitudes [0.0, 1.0].
     * Index 0: Sub-bass (20Hz - 60Hz)
     * Index 1: Bass (60Hz - 250Hz)
     * Index 2: Low-Mid (250Hz - 500Hz)
     * Index 3: Mid (500Hz - 2kHz)
     * Index 4: High-Mid (2kHz - 4kHz)
     * Index 5: Presence (4kHz - 6kHz)
     * Index 6: Brilliance (6kHz - 12kHz)
     * Index 7: Treble (12kHz - 20kHz)
     */
    val fftState: StateFlow<FloatArray>

    /**
     * Binds the visualizer engine to a specific audio session ID.
     */
    fun attachAudioSession(audioSessionId: Int)

    /**
     * Detaches the current audio session and releases hardware visualizer resources.
     */
    fun detachAudioSession()

    /**
     * Enables or disables FFT capture listening.
     */
    fun setEnabled(enabled: Boolean)

    /**
     * Completely releases all visualizer resources and cancels internal coroutines.
     */
    fun release()
}
