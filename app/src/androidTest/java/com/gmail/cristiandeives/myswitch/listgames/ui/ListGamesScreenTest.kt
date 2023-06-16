package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.filters.LargeTest
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
@LargeTest
class ListGamesScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val robot = ListGamesScreenRobot(rule)

    @Test
    fun loading_loadingStateIsDisplayed() {
        launchScreen(ListGamesUiState.Loading)

        robot.assertLoadingStateIsDisplayed()
    }

    @Test
    fun error_errorStateIsDisplayed() {
        launchScreen(ListGamesUiState.Error)

        robot.assertErrorStateIsDisplayed()
    }

    @Test
    fun data_noGames_emptyCaseIsDisplayed() {
        launchScreen(ListGamesUiState.Data())

        robot.assertDataEmptyCaseIsDisplayed()
    }

    @Test
    fun data_someGames_gameDataIsDisplayed() {
        val games = List(3) { i ->
            GameUiState(name = "Game #$i", coverUrl = "http://www.example.test/$i")
        }

        launchScreen(ListGamesUiState.Data(games))

        for ((index, game) in games.withIndex()) {
            robot.assertGameTitleIs(game.name, index)
        }
    }

    @Test
    fun data_manyGames_gameListIsScrollable() {
        val games = List(100) { i ->
            GameUiState(name = "Game #$i", coverUrl = "http://www.example.test/$i")
        }

        launchScreen(ListGamesUiState.Data(games))

        robot.assertDataContentIsScrollable()
    }

    @Test
    fun data_clickAddGameButton_addGameButtonIsClicked() {
        var addGameButtonClicked = false

        launchScreen(
            uiState = ListGamesUiState.Data(),
            onAddGameClick = { addGameButtonClicked = true },
        )

        robot.clickAddGameButton()

        assertTrue(addGameButtonClicked)
    }

    private fun launchScreen(
        uiState: ListGamesUiState,
        onAddGameClick: () -> Unit = {},
    ) {
        rule.setContent {
            MySwitchTheme {
                ListGamesScreen(
                    uiState = uiState,
                    onAddGameClick = onAddGameClick,
                )
            }
        }
    }
}
