package com.gamedleuv.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.gamedleuv.BuildConfig

object RetrofitInstance {

    private const val BASE_URL = "https://api.igdb.com/v4/"

    val api: IgdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Client-ID", BuildConfig.IGDB_CLIENT_ID)
                            .addHeader("Authorization", "Bearer ${BuildConfig.IGDB_AUTH_TOKEN}")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(IgdbApiService::class.java)
    }
}