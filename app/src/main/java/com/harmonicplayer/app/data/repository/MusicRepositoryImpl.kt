package com.harmonicplayer.app.data.repository

import com.harmonicplayer.app.data.local.mediastore.MediaStoreDataSource
import com.harmonicplayer.app.data.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicRepository {
    override fun getSongs(): Flow<List<Song>> = flow {
        emit(mediaStoreDataSource.fetchLocalSongs())
    }.flowOn(ioDispatcher)
}
