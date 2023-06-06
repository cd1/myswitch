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
}