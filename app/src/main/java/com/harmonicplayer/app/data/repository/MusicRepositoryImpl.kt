package com.harmonicplayer.app.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.harmonicplayer.app.data.local.dao.PlaylistDao
import com.harmonicplayer.app.data.local.entity.PlaylistEntity
import com.harmonicplayer.app.data.local.entity.PlaylistSongCrossRef
import com.harmonicplayer.app.data.local.entity.RecentlyPlayedEntity
import com.harmonicplayer.app.data.model.Album
import com.harmonicplayer.app.data.model.Artist
import com.harmonicplayer.app.data.model.Playlist
import com.harmonicplayer.app.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playlistDao: PlaylistDao
) : MusicRepository {

    private val sampleSongs = listOf(
        Song(1L, "Midnight City", "M83", "Hurry Up, We're Dreaming", 243000L, "content://media/external/audio/media/1"),
        Song(2L, "Starboy", "The Weeknd", "Starboy", 230000L, "content://media/external/audio/media/2"),
        Song(3L, "Blinding Lights", "The Weeknd", "After Hours", 200000L, "content://media/external/audio/media/3"),
        Song(4L, "Come Together", "The Beatles", "Abbey Road", 259000L, "content://media/external/audio/media/4"),
        Song(5L, "Something", "The Beatles", "Abbey Road", 183000L, "content://media/external/audio/media/5"),
        Song(6L, "Get Back", "The Beatles", "Let It Be", 191000L, "content://media/external/audio/media/6"),
        Song(7L, "Instant Crush", "Daft Punk", "Random Access Memories", 337000L, "content://media/external/audio/media/7"),
        Song(8L, "Get Lucky", "Daft Punk", "Random Access Memories", 248000L, "content://media/external/audio/media/8")
    )

    private fun queryMediaStore(): List<Song> {
        val songList = mutableListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        try {
            val cursor: Cursor? = contentResolver.query(uri, projection, selection, null, null)
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown Title"
                    val artist = it.getString(artistColumn) ?: "Unknown Artist"
                    val album = it.getString(albumColumn) ?: "Unknown Album"
                    val duration = it.getLong(durationColumn)
                    val albumId = it.getLong(albumIdColumn)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                    songList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            contentUriString = contentUri.toString(),
                            albumArtUriString = albumArtUri.toString()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Permission not granted or query error
        }

        return if (songList.isNotEmpty()) songList else sampleSongs
    }

    override fun getSongs(): Flow<List<Song>> = flow {
        emit(queryMediaStore())
    }.flowOn(Dispatchers.IO)

    override fun getAlbums(): Flow<List<Album>> = combine(getSongs()) { songsList ->
        songsList.groupBy { it.album }
            .map { (albumName, songsInAlbum) ->
                val primaryArtist = songsInAlbum.firstOrNull()?.artist ?: "Unknown Artist"
                val artUri = songsInAlbum.firstOrNull()?.albumArtUriString
                Album(
                    name = albumName,
                    artist = primaryArtist,
                    songCount = songsInAlbum.size,
                    albumArtUriString = artUri
                )
            }.sortedBy { it.name }
    }.flowOn(Dispatchers.IO)

    override fun getArtists(): Flow<List<Artist>> = combine(getSongs()) { songsList ->
        songsList.groupBy { it.artist }
            .map { (artistName, songsByArtist) ->
                val uniqueAlbums = songsByArtist.map { it.album }.distinct().size
                Artist(
                    name = artistName,
                    songCount = songsByArtist.size,
                    albumCount = uniqueAlbums
                )
            }.sortedBy { it.name }
    }.flowOn(Dispatchers.IO)

    override fun getPlaylists(): Flow<List<Playlist>> = combine(
        playlistDao.getAllPlaylists(),
        playlistDao.getAllPlaylistSongCrossRefs()
    ) { entities, crossRefs ->
        entities.map { entity ->
            val count = crossRefs.count { it.playlistId == entity.id }
            Playlist(
                id = entity.id,
                name = entity.name,
                songCount = count,
                createdAt = entity.createdAt
            )
        }
    }.flowOn(Dispatchers.IO)

    override fun getSongsForAlbum(albumName: String): Flow<List<Song>> = combine(getSongs()) { songsList ->
        songsList.filter { it.album.equals(albumName, ignoreCase = true) }
    }.flowOn(Dispatchers.IO)

    override fun getSongsForArtist(artistName: String): Flow<List<Song>> = combine(getSongs()) { songsList ->
        songsList.filter { it.artist.equals(artistName, ignoreCase = true) }
    }.flowOn(Dispatchers.IO)

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> = combine(
        getSongs(),
        playlistDao.getSongIdsInPlaylist(playlistId)
    ) { allSongs, playlistSongIds ->
        val songMap = allSongs.associateBy { it.id }
        playlistSongIds.mapNotNull { songMap[it] }
    }.flowOn(Dispatchers.IO)

    override fun getRecentlyPlayed(): Flow<List<Song>> = combine(
        getSongs(),
        playlistDao.getRecentlyPlayedSongIds()
    ) { allSongs, recentlyPlayedIds ->
        val songMap = allSongs.associateBy { it.id }
        recentlyPlayedIds.mapNotNull { songMap[it] }
    }.flowOn(Dispatchers.IO)

    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deleteAllSongsFromPlaylist(playlistId)
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId = playlistId, songId = songId))
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId = playlistId, songId = songId)
    }

    override suspend fun recordRecentlyPlayed(songId: Long) {
        playlistDao.recordRecentlyPlayed(RecentlyPlayedEntity(songId = songId))
    }
}
