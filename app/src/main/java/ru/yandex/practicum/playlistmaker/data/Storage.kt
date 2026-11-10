package ru.yandex.practicum.playlistmaker.data

import ru.yandex.practicum.playlistmaker.domain.Track

internal class Storage {
    val tracks: List<Track> = listOf(
        Track(1, "Yesterday", "The Beatles", 125_000),
        Track(2, "Here Comes the Sun", "The Beatles", 187_000),
        Track(3, "No Reply", "The Beatles", 152_000),
        Track(4, "Let It Be", "The Beatles", 243_000),
        Track(5, "Girl", "The Beatles", 138_000),
        Track(6, "Michelle", "The Beatles", 167_000),
        Track(7, "Eleanor Rigby", "The Beatles", 198_000),
        Track(8, "Come Together", "The Beatles", 259_000),
    )
}
