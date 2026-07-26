package com.harmonicplayer.app.domain.usecase

import com.harmonicplayer.app.data.model.Song
import com.harmonicplayer.app.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getSongs()
}
