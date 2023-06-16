package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.common.ui.CommonErrorMessage
import com.gmail.cristiandeives.myswitch.common.ui.CommonLoadingIndicator
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@VisibleForTesting const val AddGameContentSearchBarTestTag = "AddGameContentSearchBar"
@VisibleForTesting const val AddGameContentRecentGameSearchTestTag = "AddGameContentRecentGameSearch"
@VisibleForTesting const val AddGameContentSearchResultLoadingTestTag = "AddGameContentSearchResultLoading"
@VisibleForTesting const val AddGameContentSearchResultTestTag = "AddGameContentSearchResult"

@ExperimentalMaterial3Api
@Composable
fun AddGameContent(
    searchQuery: String,
    searchBarState: SearchBarState,
    searchResultState: SearchResultState?,
    onSearchQueryChange: (String) -> Unit,
    onSearchBarActiveChange: (Boolean) -> Unit,
    onRecentGameSearchClick: (String) -> Unit,
    onRecentGameSearchRemoveClick: (Long) -> Unit,
    onSearch: () -> Unit,
    onSearchResultClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { onSearch() },
            active = searchBarState is SearchBarState.Active,
            onActiveChange = onSearchBarActiveChange,
            placeholder = { Text(stringResource(R.string.add_game_type_name_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = if (searchBarState is SearchBarState.Active && searchQuery.isNotEmpty()) {
                {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.add_game_clear_query_content_description),
                        )
                    }
                }
            } else {
                null
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .testTag(AddGameContentSearchBarTestTag),
        ) {
            if (searchBarState is SearchBarState.Active) {
                for (gameSearch in searchBarState.recentGameSearches) {
                    RecentGameSearchItem(
                        query = gameSearch.query,
                        onClick = { onRecentGameSearchClick(gameSearch.query) },
                        onRemoveClick = { onRecentGameSearchRemoveClick(gameSearch.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(AddGameContentRecentGameSearchTestTag),
                    )
                }
            }
        }

        if (searchResultState != null) {
            when (searchResultState) {
                SearchResultState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                    ) {
                        CommonLoadingIndicator(
                            text = stringResource(R.string.add_game_searching),
                            modifier = Modifier.testTag(AddGameContentSearchResultLoadingTestTag),
                        )
                    }
                }

                is SearchResultState.Data -> {
                    if (searchResultState.results.isNotEmpty()) {
                        LazyColumn {
                            items(
                                items = searchResultState.results,
                                key = { it.id },
                            ) { searchResult ->
                                SearchResultItem(
                                    gameTitle = searchResult.title,
                                    gameCoverUrl = searchResult.coverUrl,
                                    onClick = { onSearchResultClick(searchResult.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag(AddGameContentSearchResultTestTag),
                                )
                            }
                        }
                    } else {
                        EmptySearchResult(
                            searchQuery = searchQuery,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                                .padding(16.dp),
                        )
                    }
                }

                SearchResultState.Error -> {
                    CommonErrorMessage(
                        text = stringResource(R.string.add_game_search_error),
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun RecentGameSearchItem(
    query: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_history),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp),
        )

        Text(
            text = query,
            modifier = Modifier.padding(start = 8.dp),
        )

        Spacer(Modifier.weight(1F))

        IconButton(
            onClick = onRemoveClick,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.add_game_remove_search_query_content_description),
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    gameTitle: String,
    gameCoverUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(gameCoverUrl.orEmpty())
                .crossfade(true)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_broken_image)
                .fallback(R.drawable.ic_image)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(72.dp)
                .clip(MaterialTheme.shapes.small),
        )

        Text(
            text = gameTitle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}

@Composable
private fun EmptySearchResult(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.add_game_search_no_games_found, searchQuery),
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

// region Previews
@Preview(showBackground = true)
private annotation class AddGameContentPreview

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun InitialStatePreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "",
            searchBarState = SearchBarState.Inactive,
            searchResultState = null,
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun NoResultsPreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "call of duty",
            searchBarState = SearchBarState.Inactive,
            searchResultState = SearchResultState.Data(),
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun ResultsPreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "zelda",
            searchBarState = SearchBarState.Inactive,
            searchResultState = SearchResultState.Data(
                results = listOf(
                    GameUiState(0, "The Legend of Zelda: Breath of the Wild", ""),
                    GameUiState(1, "The Legend of Zelda: Link's Awakening", ""),
                    GameUiState(2, "The Legend of Zelda: Skyward Sword HD", ""),
                    GameUiState(3, "The Legend of Zelda: Tears of the Kingdom", ""),
                ),
            ),
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun TypingSearchPreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "zelda",
            searchBarState = SearchBarState.Active(
                recentGameSearches = listOf(
                    RecentGameSearchUiState(1, "mario"),
                    RecentGameSearchUiState(2, "donkey kong"),
                ),
            ),
            searchResultState = null,
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun SearchLoadingPreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "zelda",
            searchBarState = SearchBarState.Inactive,
            searchResultState = SearchResultState.Loading,
            onSearchQueryChange = {},
            onSearchBarActiveChange = {},
            onRecentGameSearchClick = {},
            onRecentGameSearchRemoveClick = {},
            onSearch = {},
            onSearchResultClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@AddGameContentPreview
@Composable
private fun SearchErrorPreview() {
    MySwitchTheme {
        AddGameContent(
            searchQuery = "zelda",
            searchBarState = SearchBarState.Inactive,
            searchResultState = SearchResultState.Error,
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