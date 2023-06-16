package com.gmail.cristiandeives.myswitch.addgame.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.ui.UiResult
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
class AddGameDetailsScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val robot = AddGameDetailsScreenRobot(rule)

    private val defaultDataUiState = AddGameDetailsUiState.Data(
        artworkUrl = "http://www.example.test/image.png",
        name = "Game title",
        summary = "Game description",
        publisher = "Nintendo",
        year = "2023",
        mediaType = null,
        isAddButtonEnabled = false,
        addGameDetailsResult = null,
    )

    @Test
    fun loading_loadingIsDisplayed() {
        launchScreen(uiState = AddGameDetailsUiState.Loading)

        robot.assertLoadingStateIsDisplayed()
    }

    @Test
    fun error_errorIsDisplayed() {
        launchScreen(uiState = AddGameDetailsUiState.Error)

        robot.assertErrorStateIsDisplayed()
    }

    @Test
    fun data_gameNameIsDisplayed() {
        launchScreen(uiState = defaultDataUiState)

        robot.assertGameNameIs(defaultDataUiState.name)
    }

    @Test
    fun data_gameDescriptionExists_gameDescriptionIsDisplayed() {
        launchScreen(uiState = defaultDataUiState)

        robot.assertGameDescriptionIs(defaultDataUiState.summary!!)
    }

    @Test
    fun data_gameDescriptionDoesNotExist_gameDescriptionIsNotDisplayed() {
        launchScreen(uiState = defaultDataUiState.copy(summary = null))

        robot.assertGameDescriptionIsEmpty()
    }

    @Test
    fun data_gamePublisherExists_gamePublisherIsDisplayed() {
        launchScreen(uiState = defaultDataUiState)

        robot.assertGamePublisherIs(defaultDataUiState.publisher!!)
    }

    @Test
    fun data_gamePublisherDoesNotExist_gamePublisherIsNotDisplayed() {
        launchScreen(uiState = defaultDataUiState.copy(publisher = null))

        robot.assertGamePublisherDoesNotExist()
    }

    @Test
    fun data_gameYearExists_gameYearIsDisplayed() {
        launchScreen(uiState = defaultDataUiState)

        robot.assertGameYearIs(defaultDataUiState.year!!)
    }

    @Test
    fun data_gameYearDoesNotExist_gameYearIsNotDisplayed() {
        launchScreen(uiState = defaultDataUiState.copy(year = null))

        robot.assertGameYearDoesNotExist()
    }

    @Test
    fun data_noMediaTypeIsSet_noMediaTypeIsSelected() {
        launchScreen(uiState = defaultDataUiState.copy(mediaType = null))

        robot.assertSelectedMediaTypeIs(null)
    }

    @Test
    fun data_physicalMediaTypeIsSet_physicalMediaTypeIsSelected() {
        launchScreen(uiState = defaultDataUiState.copy(mediaType = GameMediaType.Physical))

        robot.assertSelectedMediaTypeIs(GameMediaType.Physical)
    }

    @Test
    fun data_noMediaTypeIsSet_mediaTypeButtonIsClicked() {
        var clickedMediaType: GameMediaType? = null

        launchScreen(
            uiState = defaultDataUiState.copy(mediaType = null),
            onMediaTypeChange = { clickedMediaType = it },
        )

        val mediaTypeToClick = GameMediaType.Physical

        robot.clickMediaTypeButton(mediaTypeToClick)

        assertEquals(mediaTypeToClick, clickedMediaType)
    }

    @Test
    fun data_physicalMediaTypeIsSet_otherMediaTypeButtonIsClicked() {
        var clickedMediaType: GameMediaType? = null

        launchScreen(
            uiState = defaultDataUiState.copy(mediaType = GameMediaType.Physical),
            onMediaTypeChange = { clickedMediaType = it },
        )

        val mediaTypeToClick = GameMediaType.Digital

        robot.clickMediaTypeButton(mediaTypeToClick)

        assertEquals(mediaTypeToClick, clickedMediaType)
    }

    @Test
    fun data_physicalMediaTypeIsSet_sameMediaTypeButtonIsClicked() {
        val selectedMediaType = GameMediaType.Physical
        var clickedMediaType: GameMediaType? = null

        launchScreen(
            uiState = defaultDataUiState.copy(mediaType = selectedMediaType),
            onMediaTypeChange = { clickedMediaType = it },
        )

        robot.clickMediaTypeButton(selectedMediaType)

        assertEquals(selectedMediaType, clickedMediaType)
    }

    @Test
    fun data_addButtonShouldBeEnabled_addButtonIsEnabled() {
        launchScreen(
            uiState = defaultDataUiState.copy(isAddButtonEnabled = true),
        )

        robot.assertAddButtonIsEnabled()
    }

    @Test
    fun data_addButtonShouldNotBeEnabled_addButtonIsNotEnabled() {
        launchScreen(
            uiState = defaultDataUiState.copy(isAddButtonEnabled = false),
        )

        robot.assertAddButtonIsNotEnabled()
    }

    @Test
    fun data_addResultIsLoading_addResultLoadingIsDisplayed() {
        launchScreen(
            uiState = defaultDataUiState.copy(addGameDetailsResult = UiResult.Loading),
        )

        robot.assertAddResultLoadingIsDisplayed()
    }

    @Ignore("Some issue with coroutine")
    @Test
    fun data_addResultIsError_addResultErrorIsTriggered() {
        var errorMessageDisplayed = false

        launchScreen(
            uiState = defaultDataUiState.copy(addGameDetailsResult = UiResult.Error),
            onErrorMessageDisplayed = { errorMessageDisplayed = true },
        )

        assertTrue(errorMessageDisplayed)
    }

    @Test
    fun data_addResultIsSuccess_addResultSuccessIsTriggered() {
        var gameAdded = false

        launchScreen(
            uiState = defaultDataUiState.copy(addGameDetailsResult = UiResult.Success),
            onGameAdded = { gameAdded = true },
        )

        assertTrue(gameAdded)
    }

    @Test
    fun data_navigationBackButtonIsClicked() {
        var navigationBackButtonClicked = false

        launchScreen(
            uiState = defaultDataUiState,
            onNavigationBackClick = { navigationBackButtonClicked = true },
        )

        robot.clickNavigationBackButton()

        assertTrue(navigationBackButtonClicked)
    }

    @Test
    fun data_addButtonIsClicked() {
        var addButtonClicked = false

        launchScreen(
            uiState = defaultDataUiState.copy(isAddButtonEnabled = true),
            onAddButtonClick = { addButtonClicked = true },
        )

        robot.clickAddButton()

        assertTrue(addButtonClicked)
    }

    private fun launchScreen(
        uiState: AddGameDetailsUiState,
        onNavigationBackClick: () -> Unit = {},
        onMediaTypeChange: (GameMediaType) -> Unit = {},
        onAddButtonClick: () -> Unit = {},
        onErrorMessageDisplayed: () -> Unit = {},
        onGameAdded: () -> Unit = {},
    ) {
        rule.setContent {
            MySwitchTheme {
                AddGameDetailsScreen(
                    uiState = uiState,
                    onNavigationBackClick = onNavigationBackClick,
                    onMediaTypeChange = onMediaTypeChange,
                    onAddButtonClick = onAddButtonClick,
                    onErrorMessageDisplayed = onErrorMessageDisplayed,
                    onGameAdded = onGameAdded,
                )
            }
        }
    }
}