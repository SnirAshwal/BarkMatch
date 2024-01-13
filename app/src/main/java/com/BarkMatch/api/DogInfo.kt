package com.BarkMatch.api
import com.google.gson.annotations.SerializedName

data class DogInfo(
    @SerializedName("weight") val weight: Weight,
    @SerializedName("height") val height: Height,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("bred_for") val bredFor: String,
    @SerializedName("breed_group") val breedGroup: String,
    @SerializedName("life_span") val lifeSpan: String,
    @SerializedName("temperament") val temperament: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("reference_image_id") val referenceImageId: String
)

data class Weight(
    @SerializedName("imperial") val imperial: String,
    @SerializedName("metric") val metric: String
)

data class Height(
    @SerializedName("imperial") val imperial: String,
    @SerializedName("metric") val metric: String
)