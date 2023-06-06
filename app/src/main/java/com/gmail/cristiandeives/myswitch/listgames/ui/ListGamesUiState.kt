package com.gmail.cristiandeives.myswitch.listgames.ui

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

sealed interface ListGamesUiState : Parcelable {
    @Parcelize
    object Loading : ListGamesUiState

    @Immutable
    @Parcelize
    data class Data(
        val games: List<GameUiState> = emptyList(),
    ) : ListGamesUiState

    @Parcelize
    object Error : ListGamesUiState
}

@Immutable
@Parcelize
data class GameUiState(
    val title: String,
    val imageUrl: String?,
) : Parcelable
