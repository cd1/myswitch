package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.lifecycle.SavedStateHandle
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.listgames.data.ListGamesRepository
import com.gmail.cristiandeives.myswitch.listgames.ui.ListGamesViewModel.Companion.toUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ListGamesViewModelTest {
    @Mock
    private lateinit var repository: ListGamesRepository

    private lateinit var mockitoAnnotations: AutoCloseable

    @Before
    fun setup() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        mockitoAnnotations.close()
    }

    @Test
    fun onInitialState_initialStateIsLoading() {
        mockRepoGetGames()
        val viewModel = createViewModel()

        viewModel.uiState.value.assertIsLoading()
    }

    @Test
    fun onRepositoryData_uiStateHasData() {
        val games = List(3) { i ->
            Game(id = i.toLong(), title = "Game $i", imageUrl = "http://www.example.test/$i")
        }
        mockRepoGetGames(games)

        val viewModel = createViewModel()

        with(viewModel.uiState.value) {
            assertIsData()
            assertGamesIs(games.map { it.toUiState() })
        }
    }

    @Test
    fun onRepositoryThrowsErrorDirectly_uiStateHasError() {
        mockRepoGetGamesError()

        val viewModel = createViewModel()

        viewModel.uiState.value.assertIsError()
    }

    @Test
    fun onRepositoryThrowsErrorInside_uiStateHasError() {
        mockRepoGetGamesErrorInside()

        val viewModel = createViewModel()

        viewModel.uiState.value.assertIsError()
    }

    private fun mockRepoGetGames(games: List<Game>? = null) {
        val gamesFlow = if (games != null) flowOf(games) else emptyFlow()

        whenever(repository.getGames())
            .thenReturn(gamesFlow)
    }

    private fun mockRepoGetGamesError() {
        whenever(repository.getGames())
            .thenThrow(RuntimeException("oops"))
    }

    private fun mockRepoGetGamesErrorInside() {
        whenever(repository.getGames())
            .thenReturn(callbackFlow { throw RuntimeException("oops") })
    }

    private fun createViewModel() =
        ListGamesViewModel(SavedStateHandle(), repository, UnconfinedTestDispatcher())

    private fun ListGamesUiState.assertIsLoading() {
        assertTrue(
            "UI state should be 'Loading'; it is '${this.javaClass.name}'",
            this is ListGamesUiState.Loading,
        )
    }

    private fun ListGamesUiState.assertIsData() {
        assertTrue(
            "UI state should be 'Data'; it is '${this.javaClass.name}'",
            this is ListGamesUiState.Data,
        )
    }

    private fun ListGamesUiState.assertIsError() {
        assertTrue(
            "UI state should be 'Error'; it is '${this.javaClass.name}'",
            this is ListGamesUiState.Error,
        )
    }

    private fun ListGamesUiState.assertGamesIs(games: List<GameUiState>) {
        assertEquals(
            "UI state doesn't contain expected games",
            games,
            (this as ListGamesUiState.Data).games,
        )
    }
}
