package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.ui.CommonErrorMessage
import com.gmail.cristiandeives.myswitch.common.ui.CommonLoadingIndicator
import com.gmail.cristiandeives.myswitch.common.ui.ObserveLifecycle
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@VisibleForTesting const val AddGameDetailsScreenLoadingIndicatorTestTag = "AddGameDetailsScreenLoadingIndicator"

@ExperimentalMaterial3Api
@Composable
fun AddGameDetailsScreen(
    uiState: AddGameDetailsUiState,
    onNavigationBackClick: () -> Unit,
    onMediaTypeChange: (GameMediaType) -> Unit,
    onAddButtonClick: () -> Unit,
    onErrorMessageDisplayed: () -> Unit,
    onGameAdded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AddGameDetailsTopBar(scrollBehavior, onNavigationBackClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { scaffoldPadding ->
        val commonModifier = Modifier
            .padding(scaffoldPadding)
            .fillMaxSize()

        when (uiState) {
            AddGameDetailsUiState.Loading -> {
                CommonLoadingIndicator(
                    text = stringResource(R.string.loading),
                    modifier = commonModifier
                        .wrapContentSize(Alignment.Center)
                        .testTag(AddGameDetailsScreenLoadingIndicatorTestTag),
                )
            }
            is AddGameDetailsUiState.Data -> {
                AddGameDetailsContent(
                    name = uiState.name,
                    artworkUrl = uiState.artworkUrl,
                    summary = uiState.summary,
                    publisher = uiState.publisher,
                    year = uiState.year,
                    mediaType = uiState.mediaType,
                    isAddButtonEnabled = uiState.isAddButtonEnabled,
                    addGameDetailsResult = uiState.addGameDetailsResult,
                    displayErrorMessage = { message -> snackbarHostState.showSnackbar(message) },
                    onMediaTypeChange = onMediaTypeChange,
                    onAddButtonClick = onAddButtonClick,
                    onErrorMessageDisplayed = onErrorMessageDisplayed,
                    onGameAdded = onGameAdded,
                    modifier = commonModifier,
                )
            }
            AddGameDetailsUiState.Error -> {
                CommonErrorMessage(
                    text = stringResource(R.string.add_game_details_load_error),
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
fun AddGameDetailsScreen(
    viewModel: AddGameDetailsViewModel,
    navigateBack: () -> Unit,
    navigateBackAfterSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ObserveLifecycle(viewModel)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddGameDetailsScreen(
        uiState = uiState,
        onNavigationBackClick = navigateBack,
        onMediaTypeChange = viewModel::onMediaTypeChange,
        onAddButtonClick = viewModel::onAddButtonClick,
        onErrorMessageDisplayed = viewModel::markErrorAsDisplayed,
        onGameAdded = navigateBackAfterSuccess,
        modifier = modifier,
    )
}

@ExperimentalMaterial3Api
@Composable
private fun AddGameDetailsTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.add_game_details_title)) },
        navigationIcon = {
            IconButton(
                onClick = onNavigationBackClick,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back_content_description),
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

// region Previews
@Preview
private annotation class AddGameDetailsScreenPreview

@ExperimentalMaterial3Api
@AddGameDetailsScreenPreview
@Composable
private fun DataPreview() {
    MySwitchTheme {
        AddGameDetailsScreen(
            uiState = AddGameDetailsUiState.Data(
                name = "Game title",
                summary = "Game summary",
                artworkUrl = "",
                publisher = "Nintendo",
                year = "2023",
                mediaType = null,
                isAddButtonEnabled = false,
                addGameDetailsResult = null,
            ),
            onNavigationBackClick = {},
            onMediaTypeChange = {},
            onAddButtonClick = {},
            onErrorMessageDisplayed = {},
            onGameAdded = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameDetailsScreenPreview
@Composable
private fun LoadingPreview() {
    MySwitchTheme {
        AddGameDetailsScreen(
            uiState = AddGameDetailsUiState.Loading,
            onNavigationBackClick = {},
            onMediaTypeChange = {},
            onAddButtonClick = {},
            onErrorMessageDisplayed = {},
            onGameAdded = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameDetailsScreenPreview
@Composable
private fun ErrorPreview() {
    MySwitchTheme {
        AddGameDetailsScreen(
            uiState = AddGameDetailsUiState.Error,
            onNavigationBackClick = {},
            onMediaTypeChange = {},
            onAddButtonClick = {},
            onErrorMessageDisplayed = {},
            onGameAdded = {},
        )
    }
}
// endregion