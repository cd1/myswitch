package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@VisibleForTesting const val ListGamesLoadingContentTestTag = "ListGamesLoadingContent"

@VisibleForTesting const val ListGamesDataContentTestTag = "ListGamesDataContent"

@ExperimentalMaterial3Api
@Composable
fun ListGamesScreen(
    uiState: ListGamesUiState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = { ListGamesTopBar(scrollBehavior) },
    ) { scaffoldPadding ->
        val commonModifier = Modifier
            .padding(scaffoldPadding)
            .fillMaxSize()

        when (uiState) {
            ListGamesUiState.Loading -> {
                ListGamesLoadingContent(
                    modifier = commonModifier
                        .wrapContentSize(Alignment.Center)
                        .testTag(ListGamesLoadingContentTestTag),
                )
            }
            is ListGamesUiState.Data -> {
                ListGamesDataContent(
                    games = uiState.games,
                    modifier = commonModifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .testTag(ListGamesDataContentTestTag),
                )
            }
            ListGamesUiState.Error -> {
                ListGamesErrorContent(
                    modifier = commonModifier
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center),
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ListGamesScreen(
    viewModel: ListGamesViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListGamesScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@ExperimentalMaterial3Api
@Composable
private fun ListGamesTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        title = {
            Text(stringResource(R.string.list_games_title))
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
}

// region Previews
@Preview(showBackground = true)
private annotation class ListGamesScreenPreview

@ExperimentalMaterial3Api
@ListGamesScreenPreview
@Composable
private fun DataPreview() {
    MySwitchTheme {
        ListGamesScreen(
            uiState = ListGamesUiState.Data(
                games = listOf(
                    GameUiState("Mario Kart 8 Deluxe", ""),
                    GameUiState("Super Mario Odyssey", ""),
                    GameUiState("The Legend of Zelda: Breath of the Wild", ""),
                ),
            ),
        )
    }
}

@ExperimentalMaterial3Api
@ListGamesScreenPreview
@Composable
private fun LoadingPreview() {
    MySwitchTheme {
        ListGamesScreen(
            uiState = ListGamesUiState.Loading,
        )
    }
}

@ExperimentalMaterial3Api
@ListGamesScreenPreview
@Composable
private fun ErrorPreview() {
    MySwitchTheme {
        ListGamesScreen(
            uiState = ListGamesUiState.Error,
        )
    }
}
// endregion
