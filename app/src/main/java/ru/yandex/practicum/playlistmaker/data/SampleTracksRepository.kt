package ru.yandex.practicum.playlistmaker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.yandex.practicum.playlistmaker.domain.Track
import ru.yandex.practicum.playlistmaker.domain.TracksRepository

internal class SampleTracksRepository(private val storage: Storage) : TracksRepository {
    override suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        delay(3_000)
        storage.tracks.filter { track ->
            track.title.contains(query, ignoreCase = true) ||
                track.artist.contains(query, ignoreCase = true)
        }
    }

    override fun findById(id: Int): Track? = storage.tracks.firstOrNull { it.id == id }
}
