package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.cristiandeives.myswitch.addgame.data.AddGameRepository
import com.gmail.cristiandeives.myswitch.addgame.data.SimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addGameRepository: AddGameRepository,
) : ViewModel() {

    val uiState = savedStateHandle.getStateFlow(UI_STATE_KEY, AddGameUiState())

    private val recentGameSearches: StateFlow<List<SimpleRecentGameSearch>> =
        addGameRepository.getRecentGameSearches().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
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
        updateState { it.copy(searchQuery = query.take(SEARCH_QUERY_MAX_LENGTH)) }
    }

    fun onSearchBarActiveChange(active: Boolean) {
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
        onSearchQueryChange(query)
        onSearch()
    }

    fun onRecentGameSearchRemoveClick(searchId: Long) {
        viewModelScope.launch {
            addGameRepository.removeRecentGameSearchById(searchId)
        }
    }

    fun onSearch() {
        val searchQuery = uiState.value.searchQuery.trim()

        if (searchQuery.isEmpty()) {
            return
        }

        updateState { state ->
            state.copy(
                searchBarState = SearchBarState.Inactive,
                searchResultState = SearchResultState.Loading,
            )
        }

        viewModelScope.launch {
            addGameRepository.addRecentGameSearch(searchQuery)

            val updatedState = addGameRepository.searchGames(searchQuery).fold(
                ifRight = { games ->
                    uiState.value.copy(
                        searchResultState = SearchResultState.Data(games.map { it.toUi() }),
                    )
                },
                ifLeft = {
                    uiState.value.copy(searchResultState = SearchResultState.Error)
                },
            )

            updateState { updatedState }
        }
    }

    fun onSearchResultSelect(gameId: Long) {

    }

    companion object {
        private const val UI_STATE_KEY = "uiState"
        private const val SEARCH_QUERY_MAX_LENGTH = 16

        private fun SimpleRecentGameSearch.toUi() = RecentGameSearchUiState(
            id = this.id,
            query = this.query,
        )

        private fun Game.toUi() = GameUiState(
            id = this.id,
            name = this.title,
            imageUrl = this.imageUrl,
        )
    }
}