package com.BarkMatch.api

import com.google.gson.annotations.SerializedName

class DogImageInfo(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("breeds") val breeds: List<DogBreed>,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("breed_ids") val breedIds: String
)

data class DogBreed(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)