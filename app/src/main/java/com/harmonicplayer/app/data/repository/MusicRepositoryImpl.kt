package com.harmonicplayer.app.data.repository

import com.harmonicplayer.app.data.local.dao.PlaylistDao
import com.harmonicplayer.app.data.local.entity.FavoriteSongEntity
import com.harmonicplayer.app.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : MusicRepository {

    private val sampleSongs = listOf(
        Song(
            id = 101L,
            title = "Acoustic Horizon",
            artist = "Aura Echo",
            album = "Midnight Sessions",
            duration = 210000L, // 3 mins 30 secs
            contentUriString = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
            albumArtUriString = null
        ),
        Song(
            id = 102L,
            title = "Neon Resonance",
            artist = "Synthetica",
            album = "Cyber Dreams",
            duration = 185000L, // 3 mins 05 secs
            contentUriString = "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3",
            albumArtUriString = null
        ),
        Song(
            id = 103L,
            title = "Velvet Serenade",
            artist = "Luna Vibe",
            album = "Chilled Waves",
            duration = 240000L, // 4 mins
            contentUriString = "https://storage.googleapis.com/exoplayer-test-media-0/wave.wav",
            albumArtUriString = null
        )
    )

    override fun getSongs(): Flow<List<Song>> = flow {
        emit(sampleSongs)
    }

    override suspend fun markFavorite(songId: Long) {
        playlistDao.markFavorite(FavoriteSongEntity(songId = songId))
    }

    override suspend fun removeFavorite(songId: Long) {
        playlistDao.removeFavorite(songId)
    }

    override fun isFavorite(songId: Long): Flow<Boolean> {
        return playlistDao.isFavorite(songId)
    }
}
