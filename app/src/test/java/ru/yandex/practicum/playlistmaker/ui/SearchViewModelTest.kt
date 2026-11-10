package ru.yandex.practicum.playlistmaker.ui

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.yandex.practicum.playlistmaker.domain.Track
import ru.yandex.practicum.playlistmaker.domain.TracksRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val track = Track(1, "Yesterday", "The Beatles", 125_000)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchShowsLoadingThenResults() = runTest(dispatcher) {
        val repository = TestRepository {
            delay(3_000)
            listOf(track)
        }
        val model = SearchViewModel(repository)
        model.updateQuery("  yesterday  ")
        model.search()
        assertEquals(SearchState.Searching, model.state.value)
        advanceUntilIdle()
        assertEquals("yesterday", repository.lastQuery)
        assertEquals(SearchState.Success(listOf(track)), model.state.value)
    }

    @Test
    fun repositoryFailureShowsError() = runTest(dispatcher) {
        val model = SearchViewModel(TestRepository { throw IOException("Unavailable") })
        model.updateQuery("yesterday")
        model.search()
        advanceUntilIdle()
        assertEquals(SearchState.Fail, model.state.value)
    }

    @Test
    fun clearingDuringSearchKeepsInitialState() = runTest(dispatcher) {
        val model = SearchViewModel(TestRepository {
            delay(3_000)
            listOf(track)
        })
        model.updateQuery("yesterday")
        model.search()
        runCurrent()
        model.clear()
        advanceUntilIdle()
        assertEquals("", model.query.value)
        assertEquals(SearchState.Initial, model.state.value)
    }

    @Test
    fun newerSearchCannotBeOverwrittenByEarlierSearch() = runTest(dispatcher) {
        val model = SearchViewModel(TestRepository { query ->
            delay(if (query == "old") 5_000 else 1_000)
            if (query == "old") listOf(track) else emptyList()
        })
        model.updateQuery("old")
        model.search()
        runCurrent()
        advanceTimeBy(100)
        model.updateQuery("new")
        model.search()
        advanceUntilIdle()
        assertTrue((model.state.value as SearchState.Success).tracks.isEmpty())
    }

    private class TestRepository(
        private val searchAction: suspend (String) -> List<Track>,
    ) : TracksRepository {
        var lastQuery: String? = null
            private set

        override suspend fun search(query: String): List<Track> {
            lastQuery = query
            return searchAction(query)
        }

        override fun findById(id: Int): Track? = null
    }
}
