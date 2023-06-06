package com.gmail.cristiandeives.myswitch.addgame.ui

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class AddGameUiState(
    val searchQuery: String = "",
    val searchBarState: SearchBarState = SearchBarState.Inactive,
    val searchResultState: SearchResultState? = null,
) : Parcelable

// Immutable?
sealed interface SearchBarState : Parcelable {
    @Parcelize
    object Inactive : SearchBarState

    @Parcelize
    data class Active(
        val recentGameSearches: List<RecentGameSearchUiState> = emptyList(),
    ) : SearchBarState
}

@Immutable
@Parcelize
data class RecentGameSearchUiState(
    val id: Long,
    val query: String,
) : Parcelable

// Immutable?
sealed interface SearchResultState : Parcelable {
    @Parcelize
    object Loading : SearchResultState

    @Parcelize
    data class Data(
        val results: List<GameUiState> = emptyList(),
    ) : SearchResultState

    @Parcelize
    object Error : SearchResultState
}

@Immutable
@Parcelize
data class GameUiState(
    val id: Long,
    val name: String,
    val imageUrl: String?,
) : Parcelable