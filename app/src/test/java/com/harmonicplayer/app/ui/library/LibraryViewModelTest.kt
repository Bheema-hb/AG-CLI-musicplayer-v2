package com.harmonicplayer.app.ui.library

import app.cash.turbine.test
import com.harmonicplayer.app.data.model.Song
import com.harmonicplayer.app.domain.usecase.GetSongsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getSongsUseCase: GetSongsUseCase = mockk()

    private val sampleSongs = listOf(
        Song(1L, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354000L, "content://1", null),
        Song(2L, "Hotel California", "Eagles", "Hotel California", 391000L, "content://2", null),
        Song(3L, "Sweet Child O' Mine", "Guns N' Roses", "Appetite for Destruction", 356000L, "content://3", null)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getSongsUseCase() } returns flowOf(sampleSongs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initialState_transitionsFromLoadingToLoaded() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            if (initialState.isLoading) {
                assertEquals(true, initialState.isLoading)
                assertEquals(0, initialState.songs.size)
                val loadedState = awaitItem()
                assertEquals(3, loadedState.songs.size)
                assertEquals("", loadedState.searchQuery)
                assertEquals(false, loadedState.isLoading)
            } else {
                assertEquals(3, initialState.songs.size)
                assertEquals("", initialState.searchQuery)
                assertEquals(false, initialState.isLoading)
            }
        }
    }

    @Test
    fun uiState_searchByTitle_filtersMatchingSongs() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state.isLoading) {
                awaitItem() // loaded state
            }

            viewModel.onSearchQueryChanged("Bohemian")
            val filteredState = awaitItem()

            assertEquals(1, filteredState.songs.size)
            assertEquals("Bohemian Rhapsody", filteredState.songs[0].title)
        }
    }

    @Test
    fun uiState_searchByArtist_filtersMatchingSongs() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state.isLoading) {
                awaitItem() // loaded state
            }

            viewModel.onSearchQueryChanged("Eagles")
            val filteredState = awaitItem()

            assertEquals(1, filteredState.songs.size)
            assertEquals("Eagles", filteredState.songs[0].artist)
        }
    }

    @Test
    fun uiState_searchByAlbum_filtersMatchingSongs() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state.isLoading) {
                awaitItem() // loaded state
            }

            viewModel.onSearchQueryChanged("Appetite")
            val filteredState = awaitItem()

            assertEquals(1, filteredState.songs.size)
            assertEquals("Appetite for Destruction", filteredState.songs[0].album)
        }
    }

    @Test
    fun uiState_clearSearchQuery_resetsToAllSongs() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state.isLoading) {
                awaitItem() // loaded state
            }

            viewModel.onSearchQueryChanged("Queen")
            awaitItem() // filtered

            viewModel.onSearchQueryChanged("")
            val resetState = awaitItem()

            assertEquals(3, resetState.songs.size)
            assertEquals("", resetState.searchQuery)
        }
    }

    @Test
    fun uiState_noMatchQuery_returnsEmptyList() = runTest {
        val viewModel = LibraryViewModel(getSongsUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state.isLoading) {
                awaitItem() // loaded state
            }

            viewModel.onSearchQueryChanged("NonExistentTrack123")
            val emptyState = awaitItem()

            assertEquals(0, emptyState.songs.size)
            assertEquals("NonExistentTrack123", emptyState.searchQuery)
        }
    }
}
