package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGamesViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val savedStateHandle: SavedStateHandle,
    private val logger: Logger,
) : ViewModel(), DefaultLifecycleObserver {

    val uiState: StateFlow<ListGamesUiState> = savedStateHandle.getStateFlow(UI_STATE_KEY, ListGamesUiState.Loading)

    override fun onCreate(owner: LifecycleOwner) {
        logger.v(TAG, "[onCreate]")
        viewModelScope.launch {
            try {
                gamesRepository.getGames().collect { games ->
                    val newState = ListGamesUiState.Data(
                        games = games.map { it.toListGamesUi() },
                    )
                    updateState(newState)
                }
            } catch (ex: Exception) {
                logger.e(TAG, "[getGames] Unexpected error: ${ex.message}", ex)
                updateState(ListGamesUiState.Error)
            }
        }
    }

    private fun updateState(newState: ListGamesUiState) {
        savedStateHandle[UI_STATE_KEY] = newState
    }

    companion object {
        private const val UI_STATE_KEY = "uiState"

        private val TAG = ListGamesViewModel::class.simpleName!!
    }
}
