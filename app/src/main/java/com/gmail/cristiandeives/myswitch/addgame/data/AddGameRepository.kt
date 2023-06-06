package com.gmail.cristiandeives.myswitch.addgame.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearch
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchesDao
import com.gmail.cristiandeives.myswitch.addgame.data.db.SimpleRecentGameSearch as DbSimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.network.AccessTokenRepository
import com.gmail.cristiandeives.myswitch.common.data.network.GamesResponse
import com.gmail.cristiandeives.myswitch.common.data.network.IgdbService
import com.gmail.cristiandeives.myswitch.common.data.network.TwitchService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.time.OffsetDateTime
import javax.inject.Inject

class AddGameRepository @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository,
    private val recentGameSearchesDao: RecentGameSearchesDao,
    private val igdbService: IgdbService,
    private val ioDispatcher: CoroutineDispatcher,
) {
    fun getRecentGameSearches(): Flow<List<SimpleRecentGameSearch>> =
        recentGameSearchesDao.readAll().map { allGameSearches -> allGameSearches.map { it.toData() } }

    suspend fun addRecentGameSearch(query: String): Either<Unit, Unit> =
        withContext(ioDispatcher) {
            try {
                val existingRecentGameSearchId = recentGameSearchesDao.readIdByQuery(query)
                val id = existingRecentGameSearchId ?: 0
                val updatedRecentGameSearch = RecentGameSearch(
                    id = id,
                    query = query,
                    lastUpdated = OffsetDateTime.now().toEpochSecond(),
                )

                recentGameSearchesDao.upsert(updatedRecentGameSearch)

                Unit.right()
            } catch (ex: Exception) {
                Unit.left()
            }
        }

    suspend fun removeRecentGameSearchById(searchId: Long): Either<Unit, Unit> =
        withContext(ioDispatcher) {
            try {
                recentGameSearchesDao.deleteById(searchId)

                Unit.right()
            } catch (ex: Exception) {
                Unit.left()
            }
        }

    suspend fun searchGames(query: String): Either<Unit, List<Game>> =
        withContext(ioDispatcher) {
            return@withContext searchGames_(query)
        }

    private suspend fun searchGames_(
        query: String,
        retryIfUnauthorized: Boolean = true,
    ): Either<Unit, List<Game>> = try {
        accessTokenRepository.requireAccessToken().map { accessToken ->
            val epochNow = OffsetDateTime.now().toEpochSecond()

            val response = igdbService.games(
                clientId = TwitchService.CLIENT_ID,
                authorization = "Bearer $accessToken",
                body = """
                        fields name, cover.url;
                        where platforms = (130) 
                            & category = (0, 4, 8, 9, 10, 11) 
                            & version_parent = null
                            & first_release_date < $epochNow
                            & name ~ *"$query"*;
                        sort name;
                    """.trimIndent(),
            )

            if (response.isSuccessful) {
                response.body()!!.map { it.toData() }
            } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && retryIfUnauthorized) {
                accessTokenRepository.clearAccessToken()
                return searchGames_(query, retryIfUnauthorized = false)
            } else {
                return Unit.left()
            }
        }
    } catch (ex: Exception) {
        Unit.left()
    }

    companion object {
        private fun DbSimpleRecentGameSearch.toData() = SimpleRecentGameSearch(
            id = this.id,
            query = this.query,
        )

        private fun GamesResponse.toData() = Game(
            id = this.id,
            title = this.name,
            imageUrl = this.cover?.url?.let { url ->
                "https:${url.replace("t_thumb", "t_thumb_2x")}"
            },
        )
    }
}
