package com.harmonicplayer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harmonicplayer.app.data.local.dao.PlaylistDao
import com.harmonicplayer.app.data.local.entity.FavoriteSongEntity

@Database(entities = [FavoriteSongEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
