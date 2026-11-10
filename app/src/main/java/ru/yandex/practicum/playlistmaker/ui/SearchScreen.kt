package ru.yandex.practicum.playlistmaker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.yandex.practicum.playlistmaker.R
import ru.yandex.practicum.playlistmaker.domain.Track

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier,
    onTrackClick: (Track) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val search = {
        viewModel.search()
        focusManager.clearFocus()
    }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = {
                IconButton(onClick = search, enabled = query.isNotBlank()) {
                    Icon(Icons.Filled.Search, stringResource(R.string.search))
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = viewModel::clear) {
                        Icon(Icons.Filled.Clear, stringResource(R.string.clear_search))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { search() }),
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (val current = state) {
                SearchState.Initial -> Text(stringResource(R.string.enter_query))
                SearchState.Searching -> {
                    val label = stringResource(R.string.loading)
                    CircularProgressIndicator(Modifier.semantics { contentDescription = label })
                }
                is SearchState.Success -> {
                    if (current.tracks.isEmpty()) {
                        Text(stringResource(R.string.no_tracks))
                    } else {
                        TrackList(current.tracks, onTrackClick, Modifier.align(Alignment.TopCenter))
                    }
                }
                SearchState.Fail -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stringResource(R.string.search_error), color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = search) { Text(stringResource(R.string.retry)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackList(tracks: List<Track>, onClick: (Track) -> Unit, modifier: Modifier) {
    LazyColumn(modifier.fillMaxWidth()) {
        items(tracks, key = { it.id }) { track ->
            ListItem(
                modifier = Modifier.clickable { onClick(track) },
                headlineContent = { Text(track.title) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_music), null, Modifier.size(45.dp))
                },
                supportingContent = { Text(track.artist) },
                trailingContent = { Text(formatDuration(track.durationMillis)) },
            )
            HorizontalDivider()
        }
    }
}
