package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val ownerId: Int,
    val isLikedByUser: Boolean,
    val breedId: Int,
    val breedName: String,
    val image: String
) {
    constructor(
        description: String,
        ownerId: Int,
        isLikedByUser: Boolean,
        breedId: Int,
        breedName: String,
        image: String
    ) : this(0, description, ownerId, isLikedByUser, breedId, breedName, image)
}

