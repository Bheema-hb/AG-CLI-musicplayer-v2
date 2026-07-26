package com.harmonicplayer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harmonicplayer.app.data.local.entity.PlaylistEntity
import com.harmonicplayer.app.data.local.entity.PlaylistSongCrossRef
import com.harmonicplayer.app.data.local.entity.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY created_at DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllSongsFromPlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT songId FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    fun getSongIdsInPlaylist(playlistId: Long): Flow<List<Long>>

    @Query("SELECT * FROM playlist_song_cross_ref")
    fun getAllPlaylistSongCrossRefs(): Flow<List<PlaylistSongCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordRecentlyPlayed(recentlyPlayed: RecentlyPlayedEntity)

    @Query("SELECT songId FROM recently_played ORDER BY played_at DESC LIMIT 50")
    fun getRecentlyPlayedSongIds(): Flow<List<Long>>
}
