package ru.yandex.practicum.playlistmaker.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.yandex.practicum.playlistmaker.data.SampleTracksRepository
import ru.yandex.practicum.playlistmaker.data.Storage
import ru.yandex.practicum.playlistmaker.domain.TracksRepository
import ru.yandex.practicum.playlistmaker.ui.SearchViewModel

internal val playlistModule = module {
    single { Storage() }
    single<TracksRepository> { SampleTracksRepository(get()) }
    viewModel { SearchViewModel(get()) }
}
