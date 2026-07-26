package com.harmonicplayer.app.util

import android.content.Context
import android.media.audiofx.Visualizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Concrete implementation of AudioVisualizerManager.
 * Binds to Android's native Visualizer API (android.media.audiofx.Visualizer)
 * and processes FFT data callbacks into 8 normalized frequency bins.
 * Automatically falls back to synthetic sine-wave simulation if hardware Visualizer is unavailable.
 */
class AudioVisualizerEngine(
    private val context: Context
) : AudioVisualizerManager {

    companion object {
        private const val TAG = "AudioVisualizerEngine"
        private const val FRAME_DELAY_MS = 16L // ~60 FPS
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val pitchProcessor = PitchProcessor()
    private val currentBins = FloatArray(PitchProcessor.NUM_BINS)

    private val _fftState = MutableStateFlow(FloatArray(PitchProcessor.NUM_BINS))
    override val fftState: StateFlow<FloatArray> = _fftState.asStateFlow()

    private var visualizer: Visualizer? = null
    private var activeSessionId: Int = 0
    private var isEngineEnabled: Boolean = false
    private var isFallbackMode: Boolean = false

    private var simulationJob: Job? = null
    private var decayJob: Job? = null

    @Synchronized
    override fun attachAudioSession(audioSessionId: Int) {
        if (activeSessionId == audioSessionId && visualizer != null && !isFallbackMode) {
            Log.d(TAG, "Audio session $audioSessionId already attached.")
            return
        }

        detachAudioSession()
        activeSessionId = audioSessionId

        if (audioSessionId <= 0) {
            Log.w(TAG, "Invalid audioSessionId $audioSessionId provided. Enabling simulated fallback visualizer.")
            enableFallbackMode()
            return
        }

        try {
            Log.d(TAG, "Attaching Visualizer to audio session ID: $audioSessionId")
            val newVisualizer = Visualizer(audioSessionId)

            val range = Visualizer.getCaptureSizeRange()
            if (range != null && range.isNotEmpty()) {
                val captureSize = range[0] // Choose minimum capture size for ultra-low latency (< 50ms)
                newVisualizer.captureSize = captureSize
            }

            val maxRate = Visualizer.getMaxCaptureRate()
            val targetRate = if (maxRate > 0) maxRate / 2 else 30000

            val listener = object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    // Waveform processing if needed
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                    if (isEngineEnabled && !isFallbackMode && fft != null) {
                        processFftData(fft, samplingRate)
                    }
                }
            }

            val status = newVisualizer.setDataCaptureListener(
                listener,
                targetRate,
                false, // waveform
                true   // fft
            )

            if (status != Visualizer.SUCCESS) {
                Log.e(TAG, "setDataCaptureListener failed with status: $status. Falling back to simulation.")
                newVisualizer.release()
                enableFallbackMode()
                return
            }

            newVisualizer.enabled = true
            visualizer = newVisualizer
            isFallbackMode = false
            isEngineEnabled = true
            stopSimulation()
            Log.i(TAG, "Successfully attached Visualizer to session $audioSessionId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Visualizer API for session $audioSessionId: ${e.message}", e)
            enableFallbackMode()
        }
    }

    @Synchronized
    override fun detachAudioSession() {
        stopSimulation()
        stopDecay()

        try {
            visualizer?.let {
                it.enabled = false
                it.release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing Visualizer: ${e.message}")
        } finally {
            visualizer = null
            activeSessionId = 0
            isEngineEnabled = false
        }
    }

    override fun setEnabled(enabled: Boolean) {
        if (isEngineEnabled == enabled) return
        isEngineEnabled = enabled

        try {
            visualizer?.enabled = enabled
        } catch (e: Exception) {
            Log.w(TAG, "Failed to toggle visualizer enabled state: ${e.message}")
        }

        if (enabled) {
            stopDecay()
            if (isFallbackMode || visualizer == null) {
                startSimulation()
            }
        } else {
            stopSimulation()
            startSmoothDecay()
        }
    }

    override fun release() {
        detachAudioSession()
    }

    private fun processFftData(fft: ByteArray, samplingRate: Int) {
        val updatedBins = pitchProcessor.calculate8Bins(fft, currentBins, samplingRate)
        _fftState.value = updatedBins.copyOf()
    }

    private fun enableFallbackMode() {
        isFallbackMode = true
        visualizer = null
        if (isEngineEnabled) {
            startSimulation()
        }
    }

    private fun startSimulation() {
        stopSimulation()
        simulationJob = scope.launch {
            var phase = 0f
            while (isActive) {
                phase += 0.1f
                for (i in 0 until PitchProcessor.NUM_BINS) {
                    val sineVal = (sin(phase + i * 0.8) + 1.0) / 2.0
                    val mag = (sineVal * 0.7 + 0.1).toFloat().coerceIn(0.0f, 1.0f)
                    currentBins[i] = currentBins[i] * 0.7f + mag * 0.3f
                }
                _fftState.value = currentBins.copyOf()
                delay(FRAME_DELAY_MS)
            }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }

    private fun startSmoothDecay() {
        stopDecay()
        decayJob = scope.launch {
            var active = true
            while (isActive && active) {
                active = false
                for (i in 0 until PitchProcessor.NUM_BINS) {
                    currentBins[i] = (currentBins[i] * 0.7f).coerceAtLeast(0f)
                    if (currentBins[i] > 0.001f) {
                        active = true
                    }
                }
                _fftState.value = currentBins.copyOf()
                delay(FRAME_DELAY_MS)
            }
        }
    }

    private fun stopDecay() {
        decayJob?.cancel()
        decayJob = null
    }
}
