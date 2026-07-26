package com.harmonicplayer.app.data.local.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.harmonicplayer.app.data.model.Song
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun fetchLocalSongs(): List<Song> {
        val songList = mutableListOf<Song>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        contentResolver.query(
            uri,
            MediaStoreConfig.projection,
            MediaStoreConfig.selection,
            null,
            MediaStoreConfig.sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val artworkBaseUri = Uri.parse("content://media/external/audio/albumart")

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Unknown Title"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                ).toString()

                val albumArtUri = ContentUris.withAppendedId(
                    artworkBaseUri, albumId
                ).toString()

                songList.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        contentUriString = contentUri,
                        albumArtUriString = albumArtUri
                    )
                )
            }
        }
        return songList
    }
}
