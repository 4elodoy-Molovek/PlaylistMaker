package ru.yandex.practicum.playlistmaker.domain

interface TracksRepository {
    suspend fun search(query: String): List<Track>
    fun findById(id: Int): Track?
}
