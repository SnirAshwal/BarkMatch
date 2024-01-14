package com.BarkMatch.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        val client = OkHttpClient.Builder().build()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit!!
    }
}