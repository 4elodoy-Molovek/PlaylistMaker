package ru.yandex.practicum.playlistmaker.domain

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val durationMillis: Long,
)
