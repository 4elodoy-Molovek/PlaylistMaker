package ru.yandex.practicum.playlistmaker

import android.app.Application
import org.koin.core.context.startKoin
import ru.yandex.practicum.playlistmaker.di.playlistModule

class PlaylistApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(playlistModule) }
    }
}
