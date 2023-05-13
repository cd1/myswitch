package com.gmail.cristiandeives.myswitch.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gmail.cristiandeives.myswitch.listgames.ui.ListGamesScreen

@ExperimentalMaterial3Api
@Composable
fun MySwitchNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.ListGames.route,
        modifier = modifier,
    ) {
        composable(NavRoute.ListGames.route) {
            ListGamesScreen(
                viewModel = hiltViewModel(),
            )
        }
    }
}
