package com.harmonicplayer.app.util

import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * DSP / FFT Signal Processing utility for converting raw Android FFT byte arrays
 * into 8 perceptual pitch/frequency magnitude bins with exponential decay smoothing.
 */
class PitchProcessor {

    companion object {
        const val NUM_BINS = 8
        private const val DECAY_ALPHA = 0.7f
        private const val ATTACK_ALPHA = 0.3f
        private const val DEFAULT_SAMPLE_RATE_HZ = 44100

        // Frequency boundaries in Hz for 8 perceptual audio bands
        private val FREQ_BOUNDARIES = floatArrayOf(
            20f,     // Band 0: Sub-bass (20-60 Hz)
            60f,     // Band 1: Bass (60-250 Hz)
            250f,    // Band 2: Low-Mid (250-500 Hz)
            500f,    // Band 3: Mid (500-2000 Hz)
            2000f,   // Band 4: High-Mid (2000-4000 Hz)
            4000f,   // Band 5: Presence (4000-6000 Hz)
            6000f,   // Band 6: Brilliance (6000-12000 Hz)
            12000f,  // Band 7: Treble (12000-20000 Hz)
            20000f
        )
    }

    private val rawTargets = FloatArray(NUM_BINS)
    private val bandCounts = IntArray(NUM_BINS)

    /**
     * Processes raw Android FFT byte array (Re/Im pairs) into 8 normalized floats [0.0, 1.0].
     * Modifies and returns [currentBins] in-place to prevent loop-level memory allocations.
     *
     * @param fft Raw byte array returned by Visualizer.getFft()
     * @param currentBins Destination array of size 8
     * @param samplingRateMilliHz Sample rate in milliHz (e.g. 44100000 for 44.1kHz), or <=0 if unknown
     */
    fun calculate8Bins(
        fft: ByteArray,
        currentBins: FloatArray,
        samplingRateMilliHz: Int = 0
    ): FloatArray {
        require(currentBins.size >= NUM_BINS) { "currentBins size must be at least $NUM_BINS" }

        if (fft.isEmpty()) {
            // Decay towards zero if empty
            for (i in 0 until NUM_BINS) {
                currentBins[i] = (currentBins[i] * DECAY_ALPHA).coerceIn(0.0f, 1.0f)
            }
            return currentBins
        }

        rawTargets.fill(0f)
        bandCounts.fill(0)

        val sampleRateHz = if (samplingRateMilliHz > 0) samplingRateMilliHz / 1000 else DEFAULT_SAMPLE_RATE_HZ
        val captureSize = fft.size
        val numPoints = captureSize / 2

        // Handle DC Component (index 0)
        val dcMag = fft[0].toFloat().coerceAtLeast(0f)
        accumulateMagnitude(0f, dcMag)

        // Handle Nyquist Component (index 1)
        val nyquistMag = fft[1].toFloat().coerceAtLeast(0f)
        val nyquistFreq = sampleRateHz / 2f
        accumulateMagnitude(nyquistFreq, nyquistMag)

        // Process complex pairs: fft[2k] is Re[k], fft[2k+1] is Im[k]
        val hzPerBin = sampleRateHz.toFloat() / captureSize.toFloat()
        for (k in 1 until numPoints) {
            val re = fft[2 * k].toFloat()
            val im = fft[2 * k + 1].toFloat()
            val mag = hypot(re, im)
            val freq = k * hzPerBin
            accumulateMagnitude(freq, mag)
        }

        // Compute normalized magnitudes per band
        for (i in 0 until NUM_BINS) {
            val count = max(1, bandCounts[i])
            val avgMag = rawTargets[i] / count
            // Scale and log-normalize
            val normalized = logScale(avgMag)
            rawTargets[i] = normalized
        }

        // Apply exponential decay/attack smoothing
        for (i in 0 until NUM_BINS) {
            val target = rawTargets[i]
            val current = currentBins[i]
            val smoothed = if (target > current) {
                // Fast attack for rising transients
                current * ATTACK_ALPHA + target * (1f - ATTACK_ALPHA)
            } else {
                // Smooth decay for falling levels
                current * DECAY_ALPHA + target * (1f - DECAY_ALPHA)
            }
            currentBins[i] = smoothed.coerceIn(0.0f, 1.0f)
        }

        return currentBins
    }

    private fun accumulateMagnitude(freqHz: Float, magnitude: Float) {
        val binIndex = findBinIndex(freqHz)
        rawTargets[binIndex] += magnitude
        bandCounts[binIndex]++
    }

    private fun findBinIndex(freqHz: Float): Int {
        for (i in 0 until NUM_BINS) {
            if (freqHz >= FREQ_BOUNDARIES[i] && freqHz < FREQ_BOUNDARIES[i + 1]) {
                return i
            }
        }
        return if (freqHz < FREQ_BOUNDARIES[0]) 0 else NUM_BINS - 1
    }

    private fun logScale(mag: Float): Float {
        // Max expected magnitude for byte values is ~128.0f
        val maxMag = 128.0f
        val clampedMag = mag.coerceIn(0.0f, maxMag)
        // Logarithmic perception scaling: ln(1 + mag) / ln(1 + maxMag)
        return (ln(1.0f + clampedMag) / ln(1.0f + maxMag)).coerceIn(0.0f, 1.0f)
    }
}
