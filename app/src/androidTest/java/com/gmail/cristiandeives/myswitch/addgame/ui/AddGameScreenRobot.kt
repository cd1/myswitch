package com.gmail.cristiandeives.myswitch.addgame.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.hasRole

class AddGameScreenRobot(private val rule: AndroidComposeTestRule<*, ComponentActivity>) {
    private val context: Context
        get() = rule.activity.applicationContext

    fun assertSearchQueryIs(query: String) {
        onSearchQuery().assertTextEquals(query)
    }

    fun assertRecentGameSearchesAre(recentGameSearches: List<String>) {
        val recentGameSearchNodes = onRecentGameSearches()

        recentGameSearchNodes.assertCountEquals(recentGameSearches.size)
        for ((i, searchQuery) in recentGameSearches.withIndex()) {
            recentGameSearchNodes[i].assertTextEquals(searchQuery)
        }
    }

    fun assertSearchResultLoadingIsDisplayed() {
        onSearchResultLoading().assertExists()
    }

    fun assertSearchResultIsEmpty(query: String) {
        onEmptySearchResultText(query).assertExists()
    }

    fun assertSearchResultsAre(searchResults: List<String>) {
        val searchResultNodes = onSearchResults()

        searchResultNodes.assertCountEquals(searchResults.size)
        for ((i, searchQuery) in searchResults.withIndex()) {
            searchResultNodes[i].assertTextEquals(searchQuery)
        }
    }

    fun assertSearchResultErrorIsDisplayed() {
        onSearchResultError().assertExists()
    }

    fun clickNavigationBackButton() {
        onNavigationBackButton().performClick()
    }

    fun clickRecentGameSearch(query: String) =
        onRecentGameSearch(query).performClick()

    fun clickRecentGameSearchRemove(query: String) =
        onRecentGameSearch(query).onChildren()
            .filterToOne(hasRole(Role.Button))
            .performClick()

    fun clickSearchResult(gameTitle: String) {
        onSearchResult(gameTitle).performClick()
    }

    private fun onNavigationBackButton() =
        rule.onNodeWithContentDescription(context.getString(R.string.navigate_back_content_description))

    private fun onSearchQuery() =
        rule.onNodeWithTag(AddGameContentSearchBarTestTag).onChildren()
            .filterToOne(hasSetTextAction())

    private fun onRecentGameSearches() =
        rule.onAllNodesWithTag(AddGameContentRecentGameSearchTestTag)

    private fun onRecentGameSearch(query: String) =
        rule.onNodeWithText(query)

    private fun onEmptySearchResultText(query: String) =
        rule.onNodeWithText(context.getString(R.string.add_game_search_no_games_found, query))

    private fun onSearchResults() =
        rule.onAllNodesWithTag(AddGameContentSearchResultTestTag)

    private fun onSearchResult(gameTitle: String) =
        rule.onNodeWithText(gameTitle)

    private fun onSearchResultLoading() =
        rule.onNodeWithTag(AddGameContentSearchResultLoadingTestTag)

    private fun onSearchResultError() =
        rule.onNodeWithText(context.getString(R.string.add_game_search_error))
}