package com.gmail.cristiandeives.myswitch.listgames.ui

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.gmail.cristiandeives.myswitch.common.data.Game
import kotlinx.parcelize.Parcelize

sealed interface ListGamesUiState : Parcelable {
    @Parcelize
    data object Loading : ListGamesUiState

    @Immutable
    @Parcelize
    data class Data(
        val games: List<GameUiState> = emptyList(),
    ) : ListGamesUiState

    @Parcelize
    data object Error : ListGamesUiState
}

@Immutable
@Parcelize
data class GameUiState(
    val name: String,
    val coverUrl: String?,
) : Parcelable

fun Game.toListGamesUi() = GameUiState(
    name = this.name,
    coverUrl = this.coverUrl,
)