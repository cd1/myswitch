package com.gmail.cristiandeives.myswitch.common.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface IgdbService {
    @Headers("Accept: application/json")
    @POST("games")
    suspend fun games(
        @Header("Client-ID") clientId: String,
        @Header("Authorization") authorization: String,
        @Body body: String,
    ): Response<List<GamesResponse>>

    companion object {
        // Hardcoded value from IGDB.
        //
        // Ideally, we should query this value from the API
        // (e.g. searching for the platform "Switch") and save the ID into a preference,
        // but maybe this never changes anyway.
        const val PlatformSwitch = 130

        fun clientIdHeader(): String =
            TwitchService.CLIENT_ID

        fun authorizationHeader(accessToken: String): String =
            "Bearer $accessToken"
    }
}