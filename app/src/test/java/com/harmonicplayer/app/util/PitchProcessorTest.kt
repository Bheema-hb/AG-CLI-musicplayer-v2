package com.harmonicplayer.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PitchProcessorTest {

    private lateinit var pitchProcessor: PitchProcessor

    @Before
    fun setUp() {
        pitchProcessor = PitchProcessor()
    }

    @Test
    fun `calculate8Bins with non-zero FFT returns array of 8 normalized floats bounded in 0 to 1`() {
        // Mock FFT byte array of 128 bytes with dummy Re/Im pairs
        val sampleFft = ByteArray(128) { index ->
            when {
                index % 4 == 0 -> 40.toByte()
                index % 4 == 1 -> (-30).toByte()
                index % 2 == 0 -> 80.toByte()
                else -> (-50).toByte()
            }
        }

        val currentBins = FloatArray(PitchProcessor.NUM_BINS)
        val result = pitchProcessor.calculate8Bins(sampleFft, currentBins, samplingRateMilliHz = 44100000)

        assertEquals(PitchProcessor.NUM_BINS, result.size)
        for (i in 0 until PitchProcessor.NUM_BINS) {
            assertTrue("Bin $i value ${result[i]} must be >= 0.0f", result[i] >= 0.0f)
            assertTrue("Bin $i value ${result[i]} must be <= 1.0f", result[i] <= 1.0f)
        }
    }

    @Test
    fun `calculate8Bins with empty array applies decay factor`() {
        val initialBins = floatArrayOf(0.8f, 0.6f, 0.9f, 0.7f, 0.5f, 0.4f, 0.3f, 0.2f)
        val currentBins = initialBins.copyOf()
        val emptyFft = ByteArray(0)

        val result = pitchProcessor.calculate8Bins(emptyFft, currentBins)

        for (i in 0 until PitchProcessor.NUM_BINS) {
            assertTrue("Bin $i decayed value ${result[i]} should be less than initial ${initialBins[i]}", result[i] < initialBins[i])
            assertTrue("Bin $i value ${result[i]} must be >= 0.0f", result[i] >= 0.0f)
        }
    }

    @Test
    fun `calculate8Bins handles variable capture sizes cleanly`() {
        val smallFft = ByteArray(64) { (it * 3).toByte() }
        val currentBins = FloatArray(PitchProcessor.NUM_BINS)

        val result = pitchProcessor.calculate8Bins(smallFft, currentBins)

        assertEquals(PitchProcessor.NUM_BINS, result.size)
        for (value in result) {
            assertTrue(value in 0.0f..1.0f)
        }
    }
}
