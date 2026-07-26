package com.harmonicplayer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harmonicplayer.app.data.local.dao.PlaylistDao
import com.harmonicplayer.app.data.local.entity.FavoriteSongEntity
import com.harmonicplayer.app.data.local.entity.PlaylistEntity
import com.harmonicplayer.app.data.local.entity.PlaylistSongCrossRef
import com.harmonicplayer.app.data.local.entity.RecentlyPlayedEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        RecentlyPlayedEntity::class,
        FavoriteSongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
