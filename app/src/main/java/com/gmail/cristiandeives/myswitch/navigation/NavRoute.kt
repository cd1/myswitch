package com.gmail.cristiandeives.myswitch.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed interface NavRoute {
    object ListGames : NavRoute {
        const val route = "list-games"
    }

    object AddGame : NavRoute {
        const val route = "add-game"
    }

    object AddGameDetails : NavRoute {
        private const val baseRoute = "add-game-details"
        private const val gameIdArg = "gameId"

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
