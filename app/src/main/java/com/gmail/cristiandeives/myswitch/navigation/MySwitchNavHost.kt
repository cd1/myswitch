package com.gmail.cristiandeives.myswitch.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gmail.cristiandeives.myswitch.addgame.ui.AddGameDetailsScreen
import com.gmail.cristiandeives.myswitch.addgame.ui.AddGameScreen
import com.gmail.cristiandeives.myswitch.listgames.ui.ListGamesScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun MySwitchNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = NavRoute.ListGames.route,
        modifier = modifier,
    ) {
        composable(
            route = NavRoute.ListGames.route,
            exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Start) },
            popEnterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.End) },
        ) {
            ListGamesScreen(
                viewModel = hiltViewModel(),
                navigateToAddGame = {
                    navController.navigate(NavRoute.AddGame.route)
                },
            )
        }

        composable(
            route = NavRoute.AddGame.route,
            enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
            exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Start) },
            popEnterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.End) },
            popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.End) },
        ) {
            AddGameScreen(
                viewModel = hiltViewModel(),
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToAddGameDetails = { gameId ->
                    navController.navigate(NavRoute.AddGameDetails.routeWithArguments(gameId))
                },
            )
        }

        composable(
            route = NavRoute.AddGameDetails.route,
            arguments = NavRoute.AddGameDetails.arguments,
            enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
            popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.End) },
        ) {
            AddGameDetailsScreen(
                viewModel = hiltViewModel(),
                navigateBack = {
                    navController.popBackStack()
                },
                navigateBackAfterSuccess = {
                    navController.popBackStack(NavRoute.ListGames.route, inclusive = false)
                },
            )
        }

        composable(
            route = NavRoute.AddGameDetails.route,
            arguments = NavRoute.AddGameDetails.arguments,
            enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
            popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.End) },
        ) {
            AddGameDetailsScreen(
                viewModel = hiltViewModel(),
                navigateBack = {
                    navController.popBackStack()
                },
                navigateBackAfterSuccess = {
                    navController.popBackStack(NavRoute.ListGames.route, inclusive = false)
                },
            )
        }
    }
}