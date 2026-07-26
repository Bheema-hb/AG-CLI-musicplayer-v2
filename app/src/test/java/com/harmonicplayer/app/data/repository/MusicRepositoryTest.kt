package com.harmonicplayer.app.data.repository

import com.harmonicplayer.app.data.local.dao.PlaylistDao
import com.harmonicplayer.app.data.local.entity.FavoriteSongEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor

@OptIn(ExperimentalCoroutinesApi::class)
class MusicRepositoryTest {

    private lateinit var mockPlaylistDao: PlaylistDao
    private lateinit var repository: MusicRepositoryImpl

    @Before
    fun setUp() {
        mockPlaylistDao = mock(PlaylistDao::class.java)
        repository = MusicRepositoryImpl(mockPlaylistDao)
    }

    @Test
    fun getSongs_returns_sampleSongs() = runTest {
        val songs = repository.getSongs().first()
        assertEquals(3, songs.size)
        assertEquals("Acoustic Horizon", songs[0].title)
    }

    @Test
    fun markFavorite_delegates_to_dao() = runTest {
        repository.markFavorite(101L)
        val captor = argumentCaptor<FavoriteSongEntity>()
        verify(mockPlaylistDao).markFavorite(captor.capture())
        assertEquals(101L, captor.firstValue.songId)
    }

    @Test
    fun removeFavorite_delegates_to_dao() = runTest {
        repository.removeFavorite(101L)
        verify(mockPlaylistDao).removeFavorite(101L)
    }

    @Test
    fun isFavorite_delegates_to_dao() = runTest {
        `when`(mockPlaylistDao.isFavorite(101L)).thenReturn(flowOf(true))
        val result = repository.isFavorite(101L).first()
        assertTrue(result)
        verify(mockPlaylistDao).isFavorite(101L)
    }
}
