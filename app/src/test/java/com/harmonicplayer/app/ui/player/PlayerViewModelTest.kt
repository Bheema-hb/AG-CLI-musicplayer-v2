package com.harmonicplayer.app.ui.player

import android.content.Context
import androidx.media3.session.MediaController
import com.harmonicplayer.app.data.model.Song
import com.harmonicplayer.app.data.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockContext: Context
    private lateinit var mockRepository: MusicRepository
    private lateinit var mockController: MediaController

    private val sampleSong = Song(
        id = 101L,
        title = "Acoustic Horizon",
        artist = "Aura Echo",
        album = "Midnight Sessions",
        duration = 210000L,
        contentUriString = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mock(Context::class.java)
        mockRepository = mock(MusicRepository::class.java)
        mockController = mock(MediaController::class.java)

        `when`(mockRepository.getSongs()).thenReturn(flowOf(listOf(sampleSong)))
        `when`(mockRepository.isFavorite(101L)).thenReturn(flowOf(false))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun seekTo_updates_uiState_position() = runTest {
        val viewModel = PlayerViewModel(mockContext, mockRepository)
        viewModel.bindMediaController(mockController)

        viewModel.seekTo(150000L)

        verify(mockController).seekTo(150000L)
        assertEquals(150000L, viewModel.uiState.value.currentPlaybackPosition)
    }

    @Test
    fun togglePlayPause_pauses_when_playing() = runTest {
        `when`(mockController.isPlaying).thenReturn(true)

        val viewModel = PlayerViewModel(mockContext, mockRepository)
        viewModel.bindMediaController(mockController)

        viewModel.togglePlayPause()

        verify(mockController).pause()
    }

    @Test
    fun togglePlayPause_plays_when_paused() = runTest {
        `when`(mockController.isPlaying).thenReturn(false)

        val viewModel = PlayerViewModel(mockContext, mockRepository)
        viewModel.bindMediaController(mockController)

        viewModel.togglePlayPause()

        verify(mockController).play()
    }

    @Test
    fun toggleFavorite_invokes_repository_markFavorite() = runTest {
        val viewModel = PlayerViewModel(mockContext, mockRepository)
        viewModel.bindMediaController(mockController)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockRepository).markFavorite(101L)
    }
}
