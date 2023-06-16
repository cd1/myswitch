package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.gmail.cristiandeives.myswitch.MainDispatcherRule
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.FakeLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ListGamesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: GamesRepository

    private lateinit var mockitoAnnotations: AutoCloseable
    private lateinit var lifecycleOwner: TestLifecycleOwner
    private lateinit var viewModel: ListGamesViewModel

    @Before
    fun setup() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)

        lifecycleOwner = TestLifecycleOwner(Lifecycle.State.INITIALIZED)
        viewModel = ListGamesViewModel(repository, SavedStateHandle(), FakeLogger())
        lifecycleOwner.lifecycle.addObserver(viewModel)
    }

    @After
    fun tearDown() {
        mockitoAnnotations.close()

        lifecycleOwner.lifecycle.removeObserver(viewModel)
    }

    @Test
    fun onInitialState_initialStateIsLoading() {
        mockRepoGetGames()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertIsLoading()
    }

    @Test
    fun onRepositoryData_uiStateHasData() {
        val games = List(3) { i ->
            Game(
                id = i.toLong(),
                coverUrl = "http://www.example.test/$i",
                name = "Game $i",
                mediaType = GameMediaType.Digital,
            )
        }
        mockRepoGetGames(games)
        moveViewModelToOnCreate()

        with(viewModel.uiState.value) {
            assertIsData()
            assertGamesIs(games.map { it.toListGamesUi() })
        }
    }

    @Test
    fun onRepositoryThrowsErrorDirectly_uiStateHasError() {
        mockRepoGetGamesError()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertIsError()
    }

    @Test
    fun onRepositoryThrowsErrorInside_uiStateHasError() {
        mockRepoGetGamesErrorInside()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertIsError()
    }

    private fun moveViewModelToOnCreate() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
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
