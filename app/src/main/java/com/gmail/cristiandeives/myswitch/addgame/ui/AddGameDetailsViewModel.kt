package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.data.toUi
import com.gmail.cristiandeives.myswitch.common.ui.UiResult
import com.gmail.cristiandeives.myswitch.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameDetailsViewModel @Inject constructor(
    private val repository: GamesRepository,
    private val savedStateHandle: SavedStateHandle,
    private val logger: Logger,
) : ViewModel(), DefaultLifecycleObserver {

    private var gameCoverUrl: String?
        get() = savedStateHandle[GAME_COVER_URL_KEY]
        set(value) = savedStateHandle.set(GAME_COVER_URL_KEY, value)

    val uiState: StateFlow<AddGameDetailsUiState> =
        savedStateHandle.getStateFlow(UI_STATE_KEY, AddGameDetailsUiState.Loading)

    override fun onCreate(owner: LifecycleOwner) {
        logger.v(TAG, "[onCreate]")

        val gameId: Long? = savedStateHandle[NavRoute.AddGameDetails.gameIdArg]

        if (gameId != null) {
            viewModelScope.launch {
                val newState = repository.getGameDetails(gameId).fold(
                    ifRight = { gameDetails ->
                        gameCoverUrl = gameDetails.coverUrl
                        gameDetails.toUi()
                    },
                    ifLeft = { AddGameDetailsUiState.Error },
                )

                updateState { newState }
            }
        } else {
            logger.w(TAG, "[onCreate] gameId is null")
            updateState { AddGameDetailsUiState.Error }
        }
    }

    private fun updateState(updateFunction: (AddGameDetailsUiState) -> AddGameDetailsUiState) {
        savedStateHandle[UI_STATE_KEY] = updateFunction(uiState.value)
    }

    fun onMediaTypeChange(mediaType: GameMediaType) {
        logger.i(TAG, "Game media type changed to $mediaType")

        updateState { state ->
            if (state is AddGameDetailsUiState.Data) {
                state.copy(
                    mediaType = mediaType,
                    isAddButtonEnabled = true,
                )
            } else {
                logger.w(TAG, "[onMediaTypeChange] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${state::class.simpleName}")
                AddGameDetailsUiState.Error
            }
        }
    }

    fun onAddButtonClick() {
        logger.i(TAG, "Add button clicked")

        val currentState = uiState.value

        if (currentState is AddGameDetailsUiState.Data) {
            if (currentState.mediaType == null) {
                logger.w(TAG, "[onAddButtonClick] Media type is null; cannot add a game without selecting a its media type.")
                return
            }

            val game = Game(
                name = currentState.name,
                coverUrl = gameCoverUrl,
                mediaType = currentState.mediaType,
            )

            updateState { state ->
                if (state is AddGameDetailsUiState.Data) {
                    state.copy(
                        addGameDetailsResult = UiResult.Loading,
                        isAddButtonEnabled = false,
                    )
                } else {
                    logger.w(TAG, "[onAddButtonClick] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${currentState::class.simpleName}")
                    AddGameDetailsUiState.Error
                }
            }

            viewModelScope.launch {
                val result = repository.insertGame(game).fold(
                    ifRight = { UiResult.Success },
                    ifLeft = { UiResult.Error },
                )

                updateState { state ->
                    if (state is AddGameDetailsUiState.Data) {
                        state.copy(
                            addGameDetailsResult = result,
                            isAddButtonEnabled = true,
                        )
                    } else {
                        logger.w(TAG, "[onAddButtonClick] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${currentState::class.simpleName}")
                        AddGameDetailsUiState.Error
                    }
                }
            }
        } else {
            logger.w(TAG, "[onAddButtonClick] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${currentState::class.simpleName}")
        }
    }

    fun markErrorAsDisplayed() {
        logger.i(TAG, "Marked error as displayed")

        val currentState = uiState.value

        if (currentState is AddGameDetailsUiState.Data) {
            if (currentState.addGameDetailsResult == UiResult.Error) {
                updateState { state ->
                    if (state is AddGameDetailsUiState.Data) {
                        state.copy(addGameDetailsResult = null)
                    } else  {
                        logger.w(TAG, "[markErrorAsDisplayed] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${currentState::class.simpleName}")
                        AddGameDetailsUiState.Error
                    }
                }
            } else {
                logger.w(TAG, "[markErrorAsDisplayed] Current state result was supposed to be ${UiResult.Error}, but was ${currentState.addGameDetailsResult}")
            }
        } else {
            logger.w(TAG, "[markErrorAsDisplayed] Current state was supposed to be ${AddGameDetailsUiState.Data::class.simpleName}, but was ${currentState::class.simpleName}")
            AddGameDetailsUiState.Error
        }
    }

    companion object {
        private const val UI_STATE_KEY = "uiState"
        private const val GAME_COVER_URL_KEY = "gameCoverUrl"

        private val TAG = AddGameDetailsViewModel::class.simpleName!!
    }
}