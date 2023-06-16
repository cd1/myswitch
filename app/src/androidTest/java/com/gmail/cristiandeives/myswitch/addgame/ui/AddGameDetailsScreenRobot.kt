package com.gmail.cristiandeives.myswitch.addgame.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gmail.cristiandeives.myswitch.R
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType

class AddGameDetailsScreenRobot(private val rule: AndroidComposeTestRule<*, ComponentActivity>) {
    private val context: Context
        get() = rule.activity.applicationContext

    fun assertLoadingStateIsDisplayed() {
        onLoadingStateIndicator().assertExists()
    }

    fun assertErrorStateIsDisplayed() {
        onErrorStateText().assertExists()
    }

    fun assertGameNameIs(name: String) {
        onGameName().assertTextEquals(name)
    }

    fun assertGameDescriptionIs(description: String) {
        onGameDescription().assertTextEquals(description)
    }

    fun assertGameDescriptionIsEmpty() {
        onGameDescription().assertTextEquals(context.getString(R.string.add_game_details_no_summary_available))
    }

    fun assertGamePublisherIs(publisher: String) {
        onGamePublisher().assertTextEquals(publisher)
    }

    fun assertGamePublisherDoesNotExist() {
        onGamePublisher().assertDoesNotExist()
    }

    fun assertGameYearIs(year: String) {
        onGameYear().assertTextEquals(year)
    }

    fun assertGameYearDoesNotExist() {
        onGameYear().assertDoesNotExist()
    }

    fun assertSelectedMediaTypeIs(mediaType: GameMediaType?) {
        when (mediaType) {
            GameMediaType.Physical -> onPhysicalMediaTypeButton().assertIsOn()
            GameMediaType.Digital -> onDigitalMediaTypeButton().assertIsOn()
            null -> {
                onPhysicalMediaTypeButton().assertIsOff()
                onDigitalMediaTypeButton().assertIsOff()
            }
        }
    }
    private fun onPhysicalMediaTypeButton() =
        rule.onNodeWithTag(AddGameDetailsContentPhysicalMediaTypeTestTag)
            .onChildren().filterToOne(hasClickAction())

    private fun onDigitalMediaTypeButton() =
        rule.onNodeWithTag(AddGameDetailsContentDigitalMediaTypeTestTag)
            .onChildren().filterToOne(hasClickAction())

    fun assertAddButtonIsEnabled() {
        onAddButton().assertIsEnabled()
    }

    fun assertAddButtonIsNotEnabled() {
        onAddButton().assertIsNotEnabled()
    }

    fun assertAddResultLoadingIsDisplayed() {
        onAddResultLoading().assertExists()
    }

    fun clickNavigationBackButton() {
        onNavigationBackButton().performClick()
    }

    fun clickMediaTypeButton(mediaType: GameMediaType) {
        when (mediaType) {
            GameMediaType.Physical -> onPhysicalMediaTypeButton().performClick()
            GameMediaType.Digital -> onDigitalMediaTypeButton().performClick()
        }
    }

    fun clickAddButton() {
        onAddButton().performClick()
    }

    private fun onNavigationBackButton() =
        rule.onNodeWithContentDescription(context.getString(R.string.navigate_back_content_description))

    private fun onLoadingStateIndicator() =
        rule.onNodeWithTag(AddGameDetailsScreenLoadingIndicatorTestTag)

    private fun onErrorStateText() =
        rule.onNodeWithText(context.getString(R.string.add_game_details_load_error))

    private fun onGameName() =
        rule.onNodeWithTag(AddGameDetailsContentNameTestTag)

    private fun onGameDescription() =
        rule.onNodeWithTag(AddGameDetailsContentSummaryTestTag)

    private fun onGamePublisher() =
        rule.onNodeWithTag(AddGameDetailsContentPublisherTestTag)

    private fun onGameYear() =
        rule.onNodeWithTag(AddGameDetailsContentYearTestTag)

    private fun onAddButton() =
        rule.onNodeWithText(context.getString(R.string.add_game_details_button))

    private fun onAddResultLoading() =
        rule.onNodeWithTag(AddGameDetailsContentAddResultLoadingTestTag)
}