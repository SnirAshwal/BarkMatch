package com.BarkMatch.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("breeds")
    fun getBreeds(): Call<List<DogInfo>>
}