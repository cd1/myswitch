package com.gmail.cristiandeives.myswitch.listgames.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.gmail.cristiandeives.myswitch.HiltTestActivity
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GamesRepository
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltAndroidTest
class ListGamesScreenIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: GamesRepository

    @Inject
    lateinit var savedStateHandle: SavedStateHandle

    @Inject
    lateinit var logger: Logger

    private val robot = ListGamesScreenRobot(composeRule)

    @Before
    fun setupHilt() {
        hiltRule.inject()
    }

    @Test
    fun loading_loadingStateIsDisplayed() {
        launchScreen()
        robot.assertLoadingStateIsDisplayed()
    }

    @Test
    fun error_errorStateIsDisplayed() {
        launchScreen()
        robot.assertErrorStateIsDisplayed()
    }

    @Test
    fun data_noGames_emptyCaseIsDisplayed() {
        launchScreen()
        robot.assertDataEmptyCaseIsDisplayed()
    }

    @Test
    fun data_someGames_gameDataIsDisplayed() {
        launchScreen()
        val games = emptyList<Game>()

        for ((index, game) in games.withIndex()) {
            robot.assertGameTitleIs(game.name, index)
        }
    }

    private fun launchScreen() {
        composeRule.setContent {
            MySwitchTheme {
                ListGamesScreen(
                    viewModel = hiltViewModel(),
                    navigateToAddGame = {},
                )
            }
        }
    }
}