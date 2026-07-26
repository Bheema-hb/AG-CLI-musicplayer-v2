package com.harmonicplayer.app.data.repository

import com.harmonicplayer.app.data.model.Album
import com.harmonicplayer.app.data.model.Artist
import com.harmonicplayer.app.data.model.Playlist
import com.harmonicplayer.app.data.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
    fun getAlbums(): Flow<List<Album>>
    fun getArtists(): Flow<List<Artist>>
    fun getPlaylists(): Flow<List<Playlist>>
    fun getSongsForAlbum(albumName: String): Flow<List<Song>>
    fun getSongsForArtist(artistName: String): Flow<List<Song>>
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    fun getRecentlyPlayed(): Flow<List<Song>>

    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    suspend fun recordRecentlyPlayed(songId: Long)
}
