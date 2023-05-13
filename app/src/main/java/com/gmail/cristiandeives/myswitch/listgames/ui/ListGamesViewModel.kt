package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.listgames.data.ListGamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGamesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ListGamesRepository,
    ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val uiState: StateFlow<ListGamesUiState> = savedStateHandle.getStateFlow(UI_STATE_KEY, ListGamesUiState.Loading)

    init {
        viewModelScope.launch(ioDispatcher) {
            try {
                repository.getGames().collect { games ->
                    savedStateHandle[UI_STATE_KEY] = ListGamesUiState.Data(
                        games = games.map { it.toUiState() },
                    )
                }
            } catch (ex: Exception) {
                savedStateHandle[UI_STATE_KEY] = ListGamesUiState.Error
            }
        }
    }

    companion object {
        private const val UI_STATE_KEY = "uiState"

        @VisibleForTesting
        fun Game.toUiState() = GameUiState(
            title = this.title,
            imageUrl = this.imageUrl,
        )
    }
}
