package ru.yandex.practicum.playlistmaker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.yandex.practicum.playlistmaker.R
import ru.yandex.practicum.playlistmaker.domain.Track
import java.util.Locale

@Composable
internal fun PlaylistHost(
    searchViewModel: SearchViewModel,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onShare: () -> Unit,
    onContactDeveloper: () -> Unit,
    onOpenAgreement: () -> Unit,
) {
    val navController = rememberNavController()
    val navigateBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController, startDestination = "main") {
        composable("main") {
            PlaylistScreen(R.string.app_name) { modifier ->
                Column(
                    modifier = modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { navController.navigate("search") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.search))
                    }
                    OutlinedButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.settings))
                    }
                }
            }
        }
        composable("search") {
            PlaylistScreen(R.string.search, navigateBack) { modifier ->
                SearchScreen(searchViewModel, modifier) { track ->
                    navController.navigate("track/${track.id}") { launchSingleTop = true }
                }
            }
        }
        composable(
            route = "track/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { entry ->
            val track = entry.arguments?.getInt("id")?.let(searchViewModel::findTrack)
            PlaylistScreen(R.string.track_details, navigateBack) { modifier ->
                TrackDetails(track, modifier)
            }
        }
        composable("settings") {
            PlaylistScreen(R.string.settings, navigateBack) { modifier ->
                Column(
                    modifier = modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val label = stringResource(R.string.dark_theme)
                        Text(label)
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = onDarkThemeChanged,
                        )
                    }
                    OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.share))
                    }
                    OutlinedButton(onClick = onContactDeveloper, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.contact_developer))
                    }
                    OutlinedButton(onClick = onOpenAgreement, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.agreement))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistScreen(
    title: Int,
    onBack: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(title)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                },
            )
        },
    ) { padding -> content(Modifier.padding(padding)) }
}

@Composable
private fun TrackDetails(track: Track?, modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (track == null) {
            Text(stringResource(R.string.track_unavailable))
        } else {
            Text(track.title, style = MaterialTheme.typography.headlineMedium)
            Text(track.artist, style = MaterialTheme.typography.titleMedium)
            Text(formatDuration(track.durationMillis))
        }
    }
}

internal fun formatDuration(millis: Long): String {
    val seconds = millis / 1_000
    return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60)
}
