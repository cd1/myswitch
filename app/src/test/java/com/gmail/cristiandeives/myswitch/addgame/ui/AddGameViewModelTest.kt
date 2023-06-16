package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.testing.TestLifecycleOwner
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.gmail.cristiandeives.myswitch.MainDispatcherRule
import com.gmail.cristiandeives.myswitch.addgame.data.RecentGameSearchesRepository
import com.gmail.cristiandeives.myswitch.addgame.data.SimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.FakeLogger
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AddGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var gamesRepository: GamesRepository

    @Mock
    lateinit var recentGameSearchesRepository: RecentGameSearchesRepository

    private lateinit var mockitoAnnotations: AutoCloseable
    private lateinit var viewModel: AddGameViewModel
    private lateinit var lifecycleOwner: TestLifecycleOwner

    private val defaultRecentGameSearches = List(3) { i ->
        SimpleRecentGameSearch(i.toLong(), "Recent game search $i")
    }

    private val defaultGames = List(3) { i ->
        Game(i.toLong(), "http://www.example.test/image$i.png", "Game $i", mediaType = null)
    }

    @Before
    fun setup() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)

        viewModel = AddGameViewModel(gamesRepository, recentGameSearchesRepository, SavedStateHandle(), FakeLogger())
        lifecycleOwner = TestLifecycleOwner(Lifecycle.State.INITIALIZED).apply {
            lifecycle.addObserver(viewModel)
        }
    }

    @After
    fun tearDown() {
        mockitoAnnotations.close()

        lifecycleOwner.lifecycle.removeObserver(viewModel)
    }

    @Test
    fun onInit_initialStateIsSet() = runTest {
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.uiState.test {
            val state = awaitItem()
            with(state) {
                assertSearchQueryIs("")
                assertSearchBarStateIsInactive()
                assertSearchResultIs(null)
            }
        }
    }

    private fun moveViewModelToOnCreate() {
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @Test
    fun onInit_whenRecentGameSearchRepositoryReturnsError_noGameSearchesAreReturned() {
        mockRecentGameSearchesError()
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertSearchBarStateIsInactive()
    }

    @Test
    fun whenRecentGameSearchRepositoryReturnsSuccessWhileSearchBarIsActive_gameSearchesAreReturned() {
        mockRecentGameSearchesSuccess(defaultRecentGameSearches)
        moveViewModelToOnCreate()

        viewModel.onSearchBarActiveChange(true)

        viewModel.uiState.value.assertSearchBarStateIsActive(defaultRecentGameSearches.map { it.toUi() })
    }

    @Test
    fun onInit_whenRecentGameSearchRepositoryReturnsSuccessWhileSearchBarIsInactive_nothingHappens() {
        mockRecentGameSearchesSuccess(defaultRecentGameSearches)
        moveViewModelToOnCreate()

        viewModel.uiState.value.assertSearchBarStateIsInactive()
    }

    @Test
    fun onQueryChange_queryIsChanged() {
        val query = "foo"

        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.onSearchQueryChange(query)

        viewModel.uiState.value.assertSearchQueryIs(query)
    }

    @Test
    fun onQueryChange_whenQueryExceedsLimit_queryIsTrimmed() {
        val query = "x".repeat(AddGameViewModel.SEARCH_QUERY_MAX_LENGTH * 2)

        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.onSearchQueryChange(query)

        val trimmedQuery = query.take(AddGameViewModel.SEARCH_QUERY_MAX_LENGTH)
        viewModel.uiState.value.assertSearchQueryIs(trimmedQuery)
    }

    @Test
    fun onRecentGameSearchClick_searchQueryIsUpdated() = runTest {
        val recentGameSearch = "foo"

        mockGamesRepositorySearchSuccess(emptyList())
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.onRecentGameSearchClick(recentGameSearch)

        viewModel.uiState.value.assertSearchQueryIs(recentGameSearch)
    }

    @Test
    fun onRecentGameSearchClick_searchIsPerformed() = runTest {
        mockGamesRepositorySearchSuccess(emptyList())
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.onRecentGameSearchClick("foo")

        viewModel.uiState.value.assertSearchResultIs(SearchResultState.Data())
    }

    @Test
    fun onRecentGameSearchRemoveClick_whenGameSearchIsNotInHistory_nothingHappens() {
        mockRecentGameSearchesSuccess(defaultRecentGameSearches)
        moveViewModelToOnCreate()

        with(viewModel) {
            onRecentGameSearchRemoveClick(999)
            onSearchBarActiveChange(true)
        }

        viewModel.uiState.value.assertSearchBarStateIsActive(defaultRecentGameSearches.map { it.toUi() })
    }

    @Test
    fun onRecentGameSearchRemoveClick_whenRepositoryReturnsError_nothingHappens() = runTest {
        mockRecentGameSearchesSuccess(defaultRecentGameSearches)
        mockRecentGameSearchesRemoveError()
        moveViewModelToOnCreate()

        with(viewModel) {
            onRecentGameSearchRemoveClick(defaultRecentGameSearches.first().id)
            onSearchBarActiveChange(true)
        }

        viewModel.uiState.value.assertSearchBarStateIsActive(defaultRecentGameSearches.map { it.toUi() })
    }

    @Test
    fun onSearch_searchBarIsInactive() = runTest {
        val query = "foo"

        mockGamesRepositorySearchSuccess(emptyList())
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.uiState.test {
            with(viewModel) {
                onSearchBarActiveChange(true)
                onSearchQueryChange(query)
                onSearch()
            }

            // 1. Initial state
            // 2. With search bar active
            // 3. With query change
            skipItems(3)

            val state = awaitItem()
            state.assertSearchBarStateIsInactive()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_searchResultIsLoadingThenSuccess() = runTest {
        val query = "foo"

        mockGamesRepositorySearchSuccess(defaultGames)
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.uiState.test {
            with(viewModel) {
                onSearchBarActiveChange(true)
                onSearchQueryChange(query)
                onSearch()
            }

            // 1. Initial state
            // 2. With search bar active
            // 3. With query change
            skipItems(3)

            val loadingState = awaitItem()
            loadingState.assertSearchResultIs(SearchResultState.Loading)

            val dataState = awaitItem()
            dataState.assertSearchResultIs(SearchResultState.Data(defaultGames.map { it.toAddGameUi() }))
        }
    }

    @Test
    fun onSearch_searchResultsAreCorrect() = runTest {
        val query = "foo"

        mockGamesRepositorySearchSuccess(defaultGames)
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        with(viewModel) {
            onSearchQueryChange(query)
            onSearch()
        }

        verify(gamesRepository).searchGames(query)
        viewModel.uiState.value.assertSearchResultIs(SearchResultState.Data(defaultGames.map { it.toAddGameUi() }))
    }

    @Test
    fun onSearch_whenQueryHasBlankSpaces_queryIsTrimmed() = runTest {
        val query = " foo "

        mockGamesRepositorySearchSuccess(emptyList())
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        with(viewModel) {
            onSearchQueryChange(query)
            onSearch()
        }

        verify(gamesRepository).searchGames(query.trim())
    }

    @Test
    fun onSearch_whenQueryIsEmpty_nothingHappens() {
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        viewModel.onSearch()

        viewModel.uiState.value.assertSearchResultIs(null)
    }

    @Test
    fun onSearch_whenRecentGameSearchRepositoryUpsertReturnsError_nothingHappens() = runTest {
        val games = emptyList<Game>()

        mockGamesRepositorySearchSuccess(games)
        mockRecentGameSearchesError()
        moveViewModelToOnCreate()

        with(viewModel) {
            onSearchQueryChange("foo")
            onSearch()
        }

        viewModel.uiState.value.assertSearchResultIs(SearchResultState.Data(games.map { it.toAddGameUi() }))
    }

    @Test
    fun onSearch_whenGamesRepositoryReturnsError_searchResultIsError() = runTest {
        mockGamesRepositorySearchError()
        mockRecentGameSearchesSuccess(emptyList())
        moveViewModelToOnCreate()

        with(viewModel) {
            onSearchQueryChange("foo")
            onSearch()
        }

        viewModel.uiState.value.assertSearchResultIs(SearchResultState.Error)
    }

    private suspend fun mockGamesRepositorySearchSuccess(games: List<Game>) {
        whenever(gamesRepository.searchGames(any()))
            .thenReturn(games.right())
    }

    private suspend fun mockGamesRepositorySearchError() {
        whenever(gamesRepository.searchGames(any()))
            .thenReturn(Unit.left())
    }

    private fun mockRecentGameSearchesSuccess(gameSearches: List<SimpleRecentGameSearch>) {
        whenever(recentGameSearchesRepository.getRecentGameSearches())
            .thenReturn(flowOf(gameSearches))
    }

    private fun mockRecentGameSearchesError() {
        whenever(recentGameSearchesRepository.getRecentGameSearches())
            .then {
                flow<List<SimpleRecentGameSearch>> {
                    throw IllegalStateException("ups - test error")
                }
            }
    }

    private suspend fun mockRecentGameSearchesRemoveError() {
        whenever(recentGameSearchesRepository.removeRecentGameSearchById(any()))
            .thenReturn(Unit.left())
    }

    private fun AddGameUiState.assertSearchQueryIs(expectedSearchQuery: String) {
        assertThat(this.searchQuery).isEqualTo(expectedSearchQuery)
    }

    private fun AddGameUiState.assertSearchBarStateIsInactive() {
        assertThat(this.searchBarState).isInstanceOf(SearchBarState.Inactive::class.java)
    }

    private fun AddGameUiState.assertSearchBarStateIsActive(expectedRecentGameSearches: List<RecentGameSearchUiState>) {
        assertThat(this.searchBarState).isInstanceOf(SearchBarState.Active::class.java)
        assertThat((this.searchBarState as SearchBarState.Active).recentGameSearches).isEqualTo(expectedRecentGameSearches)
    }

    private fun AddGameUiState.assertSearchResultIs(expectedSearchResultState: SearchResultState?) {
        if (expectedSearchResultState == null) {
            assertThat(this.searchResultState).isNull()
        } else {
            assertThat(this.searchResultState).isEqualTo(expectedSearchResultState)
        }
    }
}