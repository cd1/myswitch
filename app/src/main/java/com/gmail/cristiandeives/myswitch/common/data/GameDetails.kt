package com.gmail.cristiandeives.myswitch.common.data

import com.gmail.cristiandeives.myswitch.addgame.ui.AddGameDetailsUiState

data class GameDetails(
    val artworkUrl: String?,
    val coverUrl: String?,
    val name: String,
    val publishers: List<String>,
    val summary: String?,
    val year: Int?,
)

fun GameDetails.toUi() = AddGameDetailsUiState.Data(
    artworkUrl = this.artworkUrl,
    name = this.name,
    summary = this.summary,
    publisher = this.publishers.takeIf { it.isNotEmpty() }
        ?.joinToString(", "),
    year = this.year?.toString(),
    mediaType = null,
    isAddButtonEnabled = false,
    addGameDetailsResult = null,
)