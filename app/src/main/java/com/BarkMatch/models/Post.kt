package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val description: String,
    var ownerId: String,
    val breedId: Int,
    val breedName: String,
    var image: String
) {
    constructor(
        description: String,
        breedId: Int,
        breedName: String,
        image: String
    ) : this("", description, "", breedId, breedName, image)

    companion object {

        const val DESCRIPTION_KEY = "description"
        const val OWNER_ID_KEY = "ownerId"
        const val BREED_ID_KEY = "breedId"
        const val BREED_NAME_KEY = "breedName"
        const val IMAGE_KEY = "image"

        fun fromJSON(json: Map<String, Any>): Post {
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val ownerId = json[OWNER_ID_KEY] as? String ?: ""
            val breedId = json[BREED_ID_KEY] as? Long ?: 0
            val breedName = json[BREED_NAME_KEY] as? String ?: ""
            val image = json[IMAGE_KEY] as? String ?: ""
            return Post("", description, ownerId, breedId.toInt(), breedName, image)
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                DESCRIPTION_KEY to description,
                OWNER_ID_KEY to ownerId,
                BREED_ID_KEY to breedId,
                BREED_NAME_KEY to breedName,
                IMAGE_KEY to image
            )
        }
}

