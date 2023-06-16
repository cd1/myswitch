package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.cristiandeives.myswitch.addgame.data.RecentGameSearchesRepository
import com.gmail.cristiandeives.myswitch.addgame.data.SimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val recentGameSearchesRepository: RecentGameSearchesRepository,
    private val savedStateHandle: SavedStateHandle,
    private val logger: Logger,
) : ViewModel(), DefaultLifecycleObserver {

    val uiState = savedStateHandle.getStateFlow(UI_STATE_KEY, AddGameUiState())

    private lateinit var recentGameSearches: StateFlow<List<SimpleRecentGameSearch>>

    override fun onCreate(owner: LifecycleOwner) {
        logger.v(TAG, "[onCreate]")

        recentGameSearches = recentGameSearchesRepository.getRecentGameSearches()
            .catch { ex ->
                logger.e(TAG, "[getRecentGameSearches] Unexpected error: ${ex.message}", ex)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

        viewModelScope.launch {
            recentGameSearches.collect { allSearches ->
                if (uiState.value.searchBarState is SearchBarState.Active) {
                    updateState { state ->
                        state.copy(
                            searchBarState = SearchBarState.Active(allSearches.map { it.toUi() }),
                        )
                    }
                }
            }
        }
    }

    private fun updateState(updateFunction: (AddGameUiState) -> AddGameUiState) {
        savedStateHandle[UI_STATE_KEY] = updateFunction(uiState.value)
    }

    fun onSearchQueryChange(query: String) {
        logger.i(TAG, "Search query changed to '$query'")
        updateState { it.copy(searchQuery = query.take(SEARCH_QUERY_MAX_LENGTH)) }
    }

    fun onSearchBarActiveChange(active: Boolean) {
        logger.i(TAG, "Search bar active state changed to $active")

        updateState { state ->
            val newSearchBarState = if (active) {
                SearchBarState.Active(recentGameSearches.value.map { it.toUi() })
            } else {
                SearchBarState.Inactive
            }

            state.copy(searchBarState = newSearchBarState)
        }
    }

    fun onRecentGameSearchClick(query: String) {
        logger.i(TAG, "Recent game search '$query' clicked")

        onSearchQueryChange(query)
        onSearch()
    }

    fun onRecentGameSearchRemoveClick(searchId: Long) {
        logger.i(TAG, "Recent game search '$searchId' removed")

        viewModelScope.launch {
            recentGameSearchesRepository.removeRecentGameSearchById(searchId)
        }
    }

    fun onSearch() {
        val searchQuery = uiState.value.searchQuery.trim()

        if (searchQuery.isEmpty()) {
            logger.w(TAG, "[onSearch] Search query is empty")
            return
        }

        updateState { state ->
            state.copy(
                searchBarState = SearchBarState.Inactive,
                searchResultState = SearchResultState.Loading,
            )
        }

        viewModelScope.launch {
            recentGameSearchesRepository.upsertRecentGameSearch(searchQuery)

            val newState = gamesRepository.searchGames(searchQuery).fold(
                ifRight = { games ->
                    uiState.value.copy(
                        searchResultState = SearchResultState.Data(games.map { it.toAddGameUi() }),
                    )
                },
                ifLeft = {
                    uiState.value.copy(searchResultState = SearchResultState.Error)
                },
            )

            updateState { newState }
        }
    }

    companion object {
        @VisibleForTesting
        const val SEARCH_QUERY_MAX_LENGTH = 16

        private const val UI_STATE_KEY = "uiState"

        private val TAG = AddGameViewModel::class.simpleName!!
    }
}