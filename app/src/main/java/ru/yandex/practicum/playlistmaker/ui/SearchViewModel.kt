package ru.yandex.practicum.playlistmaker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.yandex.practicum.playlistmaker.domain.Track
import ru.yandex.practicum.playlistmaker.domain.TracksRepository

internal sealed interface SearchState {
    data object Initial : SearchState
    data object Searching : SearchState
    data class Success(val tracks: List<Track>) : SearchState
    data object Fail : SearchState
}

internal class SearchViewModel(private val repository: TracksRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<SearchState>(SearchState.Initial)
    val state = mutableState.asStateFlow()

    private val mutableQuery = MutableStateFlow("")
    val query = mutableQuery.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        mutableQuery.value = query
    }

    fun search() {
        val term = mutableQuery.value.trim()
        if (term.isEmpty()) {
            clear()
            return
        }

        searchJob?.cancel()
        mutableState.value = SearchState.Searching
        searchJob = viewModelScope.launch {
            try {
                mutableState.value = SearchState.Success(repository.search(term))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = SearchState.Fail
            }
        }
    }

    fun clear() {
        mutableQuery.value = ""
        searchJob?.cancel()
        mutableState.value = SearchState.Initial
    }

    fun findTrack(id: Int): Track? = repository.findById(id)
}
