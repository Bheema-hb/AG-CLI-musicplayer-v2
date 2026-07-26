package com.harmonicplayer.app.data.repository

import com.harmonicplayer.app.data.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
    suspend fun markFavorite(songId: Long)
    suspend fun removeFavorite(songId: Long)
    fun isFavorite(songId: Long): Flow<Boolean>
}
