package com.BarkMatch.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("breeds")
    fun getBreeds(): Call<List<DogInfo>>

    @GET("breeds/{breedId}")
    fun getBreedInfo(@Path("breedId") breedId: Int): Call<DogInfo>

    @GET("images/{imageId}")
    fun getBreedImageInfo(@Path("imageId") imageId: String): Call<DogImageInfo>
}