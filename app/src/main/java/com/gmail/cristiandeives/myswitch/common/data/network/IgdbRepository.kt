package com.gmail.cristiandeives.myswitch.common.data.network

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GameDetails
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.HttpURLConnection
import java.time.OffsetDateTime
import javax.inject.Inject

class IgdbRepository @Inject constructor(
    private val igdbService: IgdbService,
    private val accessTokenRepository: AccessTokenRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: Logger,
) {
    suspend fun searchGames(query: String): Either<Unit, List<Game>> =
        networkCallWithRetry { accessToken ->
            val epochNow = OffsetDateTime.now().toEpochSecond()

            logger.d(TAG, "[searchGames] HTTP request: /games")
            igdbService.games(
                clientId = IgdbService.clientIdHeader(),
                authorization = IgdbService.authorizationHeader(accessToken),
                body = """
                    fields name, cover.url;
                    where platforms = (${IgdbService.PlatformSwitch}) 
                        & category = (0, 4, 8, 9, 10, 11) 
                        & version_parent = null
                        & first_release_date < $epochNow
                        & name ~ *"$query"*;
                    sort name;
                """.trimIndent(),
            ).also { response ->
                logger.v(TAG, "[searchGames] HTTP request finished: /games (code=${response.code()})")
            }
        }.fold(
            ifRight = { response ->
                either {
                    ensure(response.isSuccessful) {
                        logger.e(TAG, "[searchGames] Unexpected HTTP response code: ${response.code()}")
                    }

                    val body = response.body()
                    ensureNotNull(body) {
                        logger.e(TAG, "[searchGames] HTTP response didn't have a body")
                    }

                    body.map { it.toGame() }
                }
            },
            ifLeft = { Unit.left() },
        )

    suspend fun getGameDetails(gameId: Long): Either<Unit, GameDetails> =
        networkCallWithRetry { accessToken ->
            logger.d(TAG, "[getGameDetails] HTTP request: /games")
            igdbService.games(
                clientId = IgdbService.clientIdHeader(),
                authorization = IgdbService.authorizationHeader(accessToken),
                body = """
                    fields
                        artworks.url,
                        cover.url,
                        involved_companies.company.name,
                        involved_companies.publisher,
                        name,
                        release_dates.y,
                        release_dates.platform,
                        summary;
                    where id = $gameId;
                """.trimIndent(),
            ).also { response ->
                logger.v(TAG, "[getGameDetails] HTTP request finished: /games (code=${response.code()})")
            }
        }.fold(
            ifRight = { response ->
                either {
                    ensure(response.isSuccessful) {
                        logger.e(TAG, "[getGameDetails] Unexpected HTTP response code: ${response.code()}")
                    }

                    val body = response.body()
                    ensureNotNull(body) {
                        logger.e(TAG, "[getGameDetails] HTTP response didn't have a body")
                    }

                    assert(body.isNotEmpty()) {
                        logger.e(TAG, "[getGameDetails] HTTP response body had an empty list")
                    }

                    body.first().toGameDetails()
                }
            },
            ifLeft = { Unit.left() },
        )

    private suspend fun <T> networkCallWithRetry(
        retryIfUnauthorized: Boolean = true,
        networkCall: suspend (String) -> Response<T>,
    ): Either<Unit, Response<T>> =
        accessTokenRepository.requireAccessToken().fold(
            ifRight = { accessToken ->
                val response = try {
                    withContext(ioDispatcher) {
                        networkCall(accessToken)
                    }
                } catch (ex: Exception) {
                    logger.e(TAG, "HTTP request failed: ${ex.message}", ex)
                    return@fold Unit.left()
                }

                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && retryIfUnauthorized) {
                    logger.d(TAG, "HTTP request was unauthorized; trying to get a new access token")
                    accessTokenRepository.clearAccessToken()
                    networkCallWithRetry(retryIfUnauthorized = false, networkCall)
                } else {
                    response.right()
                }
            },
            ifLeft = { Unit.left() },
        )

    companion object {
        private val TAG = IgdbRepository::class.simpleName!!

        private fun GamesResponse.toGame() = Game(
            id = this.id,
            name = this.name,
            coverUrl = this.cover?.url?.toCoverUrl(),
            mediaType = null,
        )

        private fun GamesResponse.toGameDetails() = GameDetails(
            artworkUrl = this.artworks?.firstOrNull()?.url?.toArtworkUrl(),
            coverUrl = this.cover?.url?.toCoverUrl(),
            name = this.name,
            publishers = this.involvedCompanies.orEmpty()
                .filter { it.publisher }
                .map { it.company.name },
            summary = this.summary,
            year = this.releaseDates.orEmpty()
                .filter { it.platform == IgdbService.PlatformSwitch && it.y != null }
                .minOfOrNull { it.y!! }, // "it.y" can't be null here, due to the check above
        )

        private fun String.toCoverUrl(): String =
            "https:${this.replace("t_thumb", "t_thumb_2x")}"

        private fun String.toArtworkUrl(): String =
            "https:${this.replace("t_thumb", "t_720p_2x")}"
    }
}