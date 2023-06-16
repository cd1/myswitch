package com.gmail.cristiandeives.myswitch.listgames.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gmail.cristiandeives.myswitch.R

class ListGamesScreenRobot(private val rule: AndroidComposeTestRule<*, out ComponentActivity>) {
    private val context: Context
        get() = rule.activity.applicationContext

    fun assertLoadingStateIsDisplayed() {
        onLoadingIndicator().assertExists()
    }

    fun assertErrorStateIsDisplayed() {
        onErrorText().assertExists()
    }

    fun assertDataEmptyCaseIsDisplayed() {
        onEmptyDataText().assertExists()
    }

    fun assertGameTitleIs(title: String, index: Int) {
        onGameItem(index).assertTextEquals(title)
    }

    fun assertDataContentIsScrollable() {
        onDataContent().assert(hasScrollAction())
    }

    fun clickAddGameButton() {
        onAddGameButton().performClick()
    }

    private fun onLoadingIndicator() =
        rule.onNodeWithTag(ListGamesLoadingContentTestTag)

    private fun onErrorText() =
        rule.onNodeWithText(context.getString(R.string.list_games_error))

    private fun onEmptyDataText() =
        rule.onNodeWithText(context.getString(R.string.list_games_empty_message))

    private fun onDataContent() =
        rule.onNodeWithTag(ListGamesDataContentTestTag)

    private fun onGameItem(index: Int) =
        rule.onAllNodesWithTag(ListGamesGameItemTestTag)[index]

    private fun onAddGameButton() =
        rule.onNodeWithContentDescription(context.getString(R.string.add_game_content_description))
}
