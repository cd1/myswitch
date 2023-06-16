package com.gmail.cristiandeives.myswitch.common.data.network

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.data.preferences.MySwitchPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccessTokenRepository @Inject constructor(
    private val twitchService: TwitchService,
    private val preferences: MySwitchPreferences,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: Logger,
) {
    suspend fun requireAccessToken(): Either<Unit, String> {
        val accessToken = withContext(ioDispatcher) {
            preferences.getAccessToken().first()
        }

        return if (accessToken.isValid()) {
            accessToken.accessToken.right()
        } else {
            logger.d(TAG, "[requireAccessToken] Access token is invalid (expiration=${accessToken.expirationTime})")
            refreshAccessToken()
        }
    }

    private suspend fun refreshAccessToken(): Either<Unit, String> =
        either {
            val response = try {
                withContext(ioDispatcher) {
                    logger.d(TAG, "[refreshAccessToken] HTTP request: /oauth2/token")
                    twitchService.accessToken(
                        clientId = TwitchService.CLIENT_ID,
                        clientSecret = TwitchService.CLIENT_SECRET,
                    ).also { response ->
                        logger.v(TAG, "[refreshAccessToken] HTTP request finished: /oauth2/token (code=${response.code()})")
                    }
                }
            } catch (ex: Exception) {
                logger.e(TAG, "[refreshAccessToken] HTTP request failed: ${ex.message}", ex)
                raise(Unit)
            }

            ensure(response.isSuccessful) {
                logger.e(TAG, "[refreshAccessToken] Unexpected HTTP response code: ${response.code()}")
            }

            val body = response.body()
            ensureNotNull(body) {
                logger.e(TAG, "[refreshAccessToken] HTTP response didn't have a body")
            }

            val dataAccessToken = body.toAccessToken()

            try {
                preferences.setAccessToken(dataAccessToken)
            } catch (ex: Exception) {
                logger.w(TAG, "[refreshAccessToken] Failed to save access token: ${ex.message}", ex)
            }

            dataAccessToken.accessToken
        }

    suspend fun clearAccessToken(): Either<Unit, Unit> =
        either {
            try {
                preferences.clearAccessToken()
            } catch (ex: Exception) {
                logger.w(TAG, "[clearAccessToken] Failed to clear preferences: ${ex.message}", ex)
                raise(Unit)
            }
        }

    companion object {
        private val TAG = AccessTokenRepository::class.simpleName!!
    }
}