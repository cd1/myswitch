package com.gmail.cristiandeives.myswitch.listgames.data

import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GameDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListGamesRepository @Inject constructor(
    private val gameDao: GameDao,
) {
    fun getGames(): Flow<List<Game>> =
        gameDao.readAll()
}
