package com.gmail.cristiandeives.myswitch.common.data

import androidx.annotation.VisibleForTesting
import arrow.core.Either
import arrow.core.raise.either
import com.gmail.cristiandeives.myswitch.common.data.db.GameEntity
import com.gmail.cristiandeives.myswitch.common.data.db.GamesDao
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.data.network.IgdbRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val gamesDao: GamesDao,
    private val igdbRepository: IgdbRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: Logger,
) {
    suspend fun searchGames(query: String): Either<Unit, List<Game>> =
        igdbRepository.searchGames(query)

    suspend fun getGameDetails(gameId: Long): Either<Unit, GameDetails> =
        igdbRepository.getGameDetails(gameId)

    fun getGames(): Flow<List<Game>> =
        gamesDao.readAll().map { allGames -> allGames.map { it.toData() } }

    suspend fun insertGame(game: Game): Either<Unit, Unit> =
        either {
            val gameEntity = game.toEntity()

            try {
                logger.d(TAG, "[insertGame] Inserting game (name=${gameEntity.name})")
                withContext(ioDispatcher) {
                    gamesDao.insert(gameEntity)
                }
                logger.v(TAG, "[insertGame] Game inserted")
            } catch (ex: Exception) {
                logger.e(TAG, "[insertGame] Unexpected error: ${ex.message}", ex)
                raise(Unit)
            }
        }

    companion object {
        private val TAG = GamesRepository::class.simpleName!!

        @VisibleForTesting
        fun Game.toEntity() = GameEntity(
            id = this.id,
            name = this.name,
            coverUrl = this.coverUrl,
            mediaType = this.mediaType,
        )
    }
}