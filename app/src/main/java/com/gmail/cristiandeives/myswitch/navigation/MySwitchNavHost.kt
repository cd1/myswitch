package com.gmail.cristiandeives.myswitch.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gmail.cristiandeives.myswitch.addgame.ui.AddGameDetailsScreen
import com.gmail.cristiandeives.myswitch.addgame.ui.AddGameScreen
import com.gmail.cristiandeives.myswitch.listgames.ui.ListGamesScreen

@ExperimentalAnimationApi
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
        composable(
            route = NavRoute.ListGames.route,
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
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
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
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
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
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
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
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