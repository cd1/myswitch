package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.filters.LargeTest
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
@LargeTest
class AddGameScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val robot = AddGameScreenRobot(rule)

    @Test
    fun searchQueryIsDisplayed() {
        val searchQuery = "foo"

        launchScreen(
            uiState = AddGameUiState(
                searchQuery = searchQuery,
            ),
        )

        robot.assertSearchQueryIs(searchQuery)
    }

    @Test
    fun searchBarIsActiveWithRecentGameSearches() {
        val recentGameSearches = listOf(
            RecentGameSearchUiState(0, "foo"),
            RecentGameSearchUiState(1, "bar"),
            RecentGameSearchUiState(2, "baz"),
        )

        launchScreen(
            uiState = AddGameUiState(
                searchBarState = SearchBarState.Active(recentGameSearches),
            ),
        )

        robot.assertRecentGameSearchesAre(recentGameSearches.map { it.query })
    }

    @Test
    fun searchIsLoading() {
        launchScreen(
            uiState = AddGameUiState(
                searchResultState = SearchResultState.Loading,
            ),
        )

        robot.assertSearchResultLoadingIsDisplayed()
    }

    @Test
    fun searchHasEmptyData() {
        val query = "foo"

        launchScreen(
            uiState = AddGameUiState(
                searchQuery = query,
                searchResultState = SearchResultState.Data(),
            ),
        )

        robot.assertSearchResultIsEmpty(query)
    }

    @Test
    fun searchHasNonEmptyData() {
        val searchResults = listOf(
            GameUiState(1, "foo", null),
            GameUiState(2, "bar", null),
            GameUiState(3, "baz", null),
        )
        launchScreen(
            uiState = AddGameUiState(
                searchResultState = SearchResultState.Data(searchResults),
            ),
        )

        robot.assertSearchResultsAre(searchResults.map { it.title })
    }

    @Test
    fun searchHasError() {
        launchScreen(
            uiState = AddGameUiState(
                searchResultState = SearchResultState.Error,
            ),
        )

        robot.assertSearchResultErrorIsDisplayed()
    }

    @Test
    fun navigationBackButtonIsClicked() {
        var navigationBackButtonClicked = false

        launchScreen(
            uiState = AddGameUiState(),
            onNavigationBackClick = { navigationBackButtonClicked = true },
        )

        robot.clickNavigationBackButton()

        assertTrue(navigationBackButtonClicked)
    }

    @Test
    fun recentGameSearchIsClicked() {
        val recentGameSearches = listOf(
            RecentGameSearchUiState(0, "foo"),
            RecentGameSearchUiState(1, "bar"),
            RecentGameSearchUiState(2, "baz"),
        )
        var clickedRecentGameSearchQuery: String? = null
        val recentGameSearchQueryToClick = recentGameSearches.first().query

        launchScreen(
            uiState = AddGameUiState(
                searchBarState = SearchBarState.Active(
                    recentGameSearches = recentGameSearches,
                ),
            ),
            onRecentGameSearchClick = { clickedRecentGameSearchQuery = it },
        )

        robot.clickRecentGameSearch(recentGameSearchQueryToClick)

        assertEquals(recentGameSearchQueryToClick, clickedRecentGameSearchQuery)
    }

    @Test
    fun recentGameSearchRemoveIsClicked() {
        val recentGameSearches = listOf(
            RecentGameSearchUiState(0, "foo"),
            RecentGameSearchUiState(1, "bar"),
            RecentGameSearchUiState(2, "baz"),
        )
        var clickedRecentGameSearchRemoveId: Long? = null
        val recentGameSearchToRemove = recentGameSearches.first()

        launchScreen(
            uiState = AddGameUiState(
                searchBarState = SearchBarState.Active(
                    recentGameSearches = recentGameSearches,
                ),
            ),
            onRecentGameSearchRemoveClick = { clickedRecentGameSearchRemoveId = it },
        )

        robot.clickRecentGameSearchRemove(recentGameSearchToRemove.query)

        assertEquals(recentGameSearchToRemove.id, clickedRecentGameSearchRemoveId)
    }

    @Test
    fun searchResultIsClicked() {
        val searchResults = listOf(
            GameUiState(1, "foo", null),
            GameUiState(2, "bar", null),
            GameUiState(3, "baz", null),
        )
        val searchResultToClick = searchResults.first()
        var clickedSearchResultId: Long? = null

        launchScreen(
            uiState = AddGameUiState(
                searchResultState = SearchResultState.Data(searchResults),
            ),
            onSearchResultClick = { clickedSearchResultId = it },
        )

        robot.clickSearchResult(searchResultToClick.title)

        assertEquals(clickedSearchResultId, searchResultToClick.id)
    }

    private fun launchScreen(
        uiState: AddGameUiState,
        onNavigationBackClick: () -> Unit = {},
        onSearchQueryChange: (String) -> Unit = {},
        onSearchBarActiveChange: (Boolean) -> Unit = {},
        onRecentGameSearchClick: (String) -> Unit = {},
        onRecentGameSearchRemoveClick: (Long) -> Unit = {},
        onSearch: () -> Unit = {},
        onSearchResultClick: (Long) -> Unit = {},
    ) {
        rule.setContent {
            MySwitchTheme {
                AddGameScreen(
                    uiState = uiState,
                    onNavigationBackClick = onNavigationBackClick,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchBarActiveChange = onSearchBarActiveChange,
                    onRecentGameSearchClick = onRecentGameSearchClick,
                    onRecentGameSearchRemoveClick = onRecentGameSearchRemoveClick,
                    onSearch = onSearch,
                    onSearchResultClick = onSearchResultClick,
                )
            }
        }
    }
}