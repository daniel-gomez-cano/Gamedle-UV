package com.gamedleuv.data.remote.api

import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IgdbApiService {
    @POST("games")
    suspend fun searchGames(
        @Body body: RequestBody
    ): List<GameResponse>
}