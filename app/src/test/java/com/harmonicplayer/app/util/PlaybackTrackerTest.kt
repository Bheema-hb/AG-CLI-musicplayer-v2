package com.harmonicplayer.app.util

import androidx.media3.common.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackTrackerTest {

    @Test
    fun tracker_polls_position_and_duration_every_250ms_when_playing() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val mockPlayer = mock(Player::class.java)
        `when`(mockPlayer.isPlaying).thenReturn(true)
        `when`(mockPlayer.currentPosition).thenReturn(1000L)
        `when`(mockPlayer.duration).thenReturn(200000L)

        var callCount = 0
        var lastPosition = 0L
        var lastDuration = 0L

        val tracker = PlaybackTracker(
            playerGetter = { mockPlayer },
            scope = testScope,
            onProgressUpdated = { pos, dur ->
                callCount++
                lastPosition = pos
                lastDuration = dur
            }
        )

        tracker.startPolling()
        testScheduler.advanceTimeBy(300)

        assertTrue(callCount >= 1)
        assertEquals(1000L, lastPosition)
        assertEquals(200000L, lastDuration)

        tracker.stopPolling()
    }

    @Test
    fun tracker_does_not_poll_when_stopped() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val mockPlayer = mock(Player::class.java)
        `when`(mockPlayer.isPlaying).thenReturn(true)

        var callCount = 0

        val tracker = PlaybackTracker(
            playerGetter = { mockPlayer },
            scope = testScope,
            onProgressUpdated = { _, _ -> callCount++ }
        )

        tracker.startPolling()
        tracker.stopPolling()
        testScheduler.advanceTimeBy(1000)

        assertEquals(0, callCount)
    }
}
