package com.gmail.cristiandeives.myswitch.addgame.ui

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.gmail.cristiandeives.myswitch.addgame.data.SimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.Game
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
    data object Inactive : SearchBarState

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
    data object Loading : SearchResultState

    @Parcelize
    data class Data(
        val results: List<GameUiState> = emptyList(),
    ) : SearchResultState

    @Parcelize
    data object Error : SearchResultState
}

@Immutable
@Parcelize
data class GameUiState(
    val id: Long,
    val title: String,
    val coverUrl: String?,
) : Parcelable

fun SimpleRecentGameSearch.toUi() = RecentGameSearchUiState(
    id = this.id,
    query = this.query,
)

fun Game.toAddGameUi() = GameUiState(
    id = this.id,
    title = this.name,
    coverUrl = this.coverUrl,
)