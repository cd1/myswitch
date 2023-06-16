package com.gmail.cristiandeives.myswitch.addgame.ui

import android.os.Parcelable
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.ui.UiResult
import kotlinx.parcelize.Parcelize

sealed interface AddGameDetailsUiState : Parcelable {
    @Parcelize
    data object Loading : AddGameDetailsUiState

    @Parcelize
    data class Data(
        val artworkUrl: String?,
        val name: String,
        val summary: String?,
        val publisher: String?,
        val year: String?,
        val mediaType: GameMediaType?,
        val isAddButtonEnabled: Boolean,

        val addGameDetailsResult: UiResult?,
    ) : AddGameDetailsUiState

    @Parcelize
    data object Error: AddGameDetailsUiState
}