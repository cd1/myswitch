package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.common.ui.ObserveLifecycle
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@ExperimentalMaterial3Api
@Composable
fun AddGameScreen(
    uiState: AddGameUiState,
    onNavigationBackClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onRecentGameSearchClick: (String) -> Unit,
    onRecentGameSearchRemoveClick: (Long) -> Unit,
    onSearch: () -> Unit,
    onSearchResultClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            AddGameTopBar(scrollBehavior, onNavigationBackClick)
        },
        modifier = modifier,
    ) { scaffoldPadding ->
        AddGameContent(
            searchQuery = uiState.searchQuery,
            searchBarState = uiState.searchBarState,
            searchResultState = uiState.searchResultState,
            onSearchQueryChange = onSearchQueryChange,
            onSearchBarActiveChange = onSearchBarActiveChange,
            onRecentGameSearchClick = onRecentGameSearchClick,
            onRecentGameSearchRemoveClick = onRecentGameSearchRemoveClick,
            onSearch = onSearch,
            onSearchResultClick = onSearchResultClick,
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddGameScreen(
    viewModel: AddGameViewModel,
    navigateBack: () -> Unit,
    navigateToAddGameDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    ObserveLifecycle(viewModel)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddGameScreen(
        uiState = uiState,
        onNavigationBackClick = navigateBack,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchBarActiveChange = viewModel::onSearchBarActiveChange,
        onRecentGameSearchClick = viewModel::onRecentGameSearchClick,
        onRecentGameSearchRemoveClick = viewModel::onRecentGameSearchRemoveClick,
        onSearch = viewModel::onSearch,
        onSearchResultClick = navigateToAddGameDetails,
        modifier = modifier,
    )
}

@ExperimentalMaterial3Api
@Composable
private fun AddGameTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.add_game_title)) },
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
@ExperimentalMaterial3Api
@Preview
@Composable
fun DefaultPreview() {
    MySwitchTheme {
        AddGameScreen(
            uiState = AddGameUiState(),
            onNavigationBackClick = {},
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}
// endregion