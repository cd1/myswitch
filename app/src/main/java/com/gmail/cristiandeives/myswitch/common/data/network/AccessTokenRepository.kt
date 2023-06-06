package com.gmail.cristiandeives.myswitch.common.data.network

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gmail.cristiandeives.myswitch.addgame.data.AccessToken
import com.gmail.cristiandeives.myswitch.common.data.preferences.MySwitchPreferences
import kotlinx.coroutines.flow.first
import java.time.OffsetDateTime
import javax.inject.Inject

class AccessTokenRepository @Inject constructor(
    private val twitchService: TwitchService,
    private val preferences: MySwitchPreferences,
) {
    suspend fun requireAccessToken(): Either<Unit, String> {
        val accessToken = preferences.getAccessToken().first()

        return if (accessToken.isValid()) {
            accessToken.accessToken.right()
        } else {
            refreshAccessToken()
        }
    }

    private suspend fun refreshAccessToken(): Either<Unit, String> = try {
        val response = twitchService.accessToken(
            clientId = TwitchService.CLIENT_ID,
            clientSecret = TwitchService.CLIENT_SECRET,
        )

        if (response.isSuccessful) {
            val dataAccessToken = response.body()!!.toData()

            preferences.setAccessToken(dataAccessToken)

            dataAccessToken.accessToken.right()
        } else {
            Unit.left()
        }
    } catch (ex: Exception) {
        Unit.left()
    }

    suspend fun clearAccessToken(): Either<Unit, Unit> = try {
        preferences.clearAccessToken()

        Unit.right()
    } catch (ex: Exception) {
        Unit.left()
    }

    companion object {
        private fun AccessTokenResponse.toData() = AccessToken(
            accessToken = this.accessToken,
            expirationTime = OffsetDateTime.now().plusSeconds(this.expiresIn),
            tokenType = this.tokenType,
        )
    }
}