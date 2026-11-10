package ru.yandex.practicum.playlistmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.yandex.practicum.playlistmaker.ui.PlaylistHost
import ru.yandex.practicum.playlistmaker.ui.SearchViewModel

class MainActivity : ComponentActivity() {
    private val searchViewModel: SearchViewModel by viewModel()
    private val settingsActions by lazy { SettingsActions(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = getSharedPreferences("settings", MODE_PRIVATE)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by remember {
                mutableStateOf(preferences.getBoolean("dark_theme", systemDark))
            }

            MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
                PlaylistHost(
                    searchViewModel = searchViewModel,
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { enabled ->
                        darkTheme = enabled
                        preferences.edit().putBoolean("dark_theme", enabled).apply()
                    },
                    onShare = settingsActions::share,
                    onContactDeveloper = settingsActions::contactDeveloper,
                    onOpenAgreement = settingsActions::openAgreement,
                )
            }
        }
    }

}
