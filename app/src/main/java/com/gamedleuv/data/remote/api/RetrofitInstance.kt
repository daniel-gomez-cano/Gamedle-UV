package com.gamedleuv.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.lazy
import kotlin.jvm.java

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
                            .addHeader("Client-ID", "3dixftig73oh0bj9f5p3ryoc2ce2bl")
                            .addHeader("Authorization", "Bearer kk1pzmq68b1oo4t9mwmn6rn74i0wuc")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(IgdbApiService::class.java)
    }
}
