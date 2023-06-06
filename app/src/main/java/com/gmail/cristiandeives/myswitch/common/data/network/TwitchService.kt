package com.gmail.cristiandeives.myswitch.common.data.network

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface TwitchService {
    @POST("/oauth2/token?grant_type=client_credentials")
    suspend fun accessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
    ): Response<AccessTokenResponse>

    companion object {
        const val CLIENT_ID = "n137tanluok9iss31bzf4y78mvjs7b"
        const val CLIENT_SECRET = "r7p2myh52mapifcfpht8jjoruluf5b"
    }
}