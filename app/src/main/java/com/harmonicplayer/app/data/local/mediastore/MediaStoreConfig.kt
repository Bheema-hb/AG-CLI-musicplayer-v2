package com.harmonicplayer.app.data.local.mediastore

import android.provider.MediaStore

object MediaStoreConfig {
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID
    )
    const val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    const val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
}
