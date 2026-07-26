package com.harmonicplayer.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val songId: Long,
    @ColumnInfo(name = "played_at") val playedAt: Long = System.currentTimeMillis()
)
