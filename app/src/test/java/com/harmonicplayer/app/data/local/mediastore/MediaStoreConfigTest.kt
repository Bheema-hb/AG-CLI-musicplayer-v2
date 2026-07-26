package com.harmonicplayer.app.data.local.mediastore

import android.provider.MediaStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaStoreConfigTest {

    @Test
    fun projection_containsRequiredColumns() {
        val projection = MediaStoreConfig.projection
        assertTrue(projection.contains(MediaStore.Audio.Media._ID))
        assertTrue(projection.contains(MediaStore.Audio.Media.TITLE))
        assertTrue(projection.contains(MediaStore.Audio.Media.ARTIST))
        assertTrue(projection.contains(MediaStore.Audio.Media.ALBUM))
        assertTrue(projection.contains(MediaStore.Audio.Media.DURATION))
        assertTrue(projection.contains(MediaStore.Audio.Media.ALBUM_ID))
    }

    @Test
    fun selection_filtersMusicOnly() {
        assertEquals("${MediaStore.Audio.Media.IS_MUSIC} != 0", MediaStoreConfig.selection)
    }

    @Test
    fun sortOrder_sortsByTitleAscending() {
        assertEquals("${MediaStore.Audio.Media.TITLE} ASC", MediaStoreConfig.sortOrder)
    }
}
