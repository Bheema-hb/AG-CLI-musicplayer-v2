package com.harmonicplayer.app.util

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class AudioVisualizerEngineTest {

    private lateinit var context: Context
    private lateinit var engine: AudioVisualizerEngine

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        engine = AudioVisualizerEngine(context)
    }

    @Test
    fun `attachAudioSession with invalid session enables simulation fallback without crash`() {
        engine.attachAudioSession(-1)
        val state = engine.fftState.value

        assertNotNull(state)
        assertEquals(8, state.size)
        for (value in state) {
            assertTrue("Value $value should be in range [0.0, 1.0]", value in 0.0f..1.0f)
        }
    }

    @Test
    fun `setEnabled toggles state and release cleans up without throwing`() {
        engine.attachAudioSession(0)
        engine.setEnabled(true)
        engine.setEnabled(false)
        engine.release()

        val state = engine.fftState.value
        assertEquals(8, state.size)
    }
}
