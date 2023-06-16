package com.gmail.cristiandeives.myswitch.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed interface NavRoute {
    data object ListGames : NavRoute {
        const val route = "list-games"
    }

    data object AddGame : NavRoute {
        const val route = "add-game"
    }

    data object AddGameDetails : NavRoute {
        private const val baseRoute = "add-game-details"

        const val gameIdArg = "gameId"
        const val route = "$baseRoute/{$gameIdArg}"

        val arguments = listOf(
            navArgument(gameIdArg) {
                type = NavType.LongType
            },
        )

        fun routeWithArguments(gameId: Long) =
            "$baseRoute/$gameId"
    }
}