package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.BarkMatch.utils.DateConverter
import java.util.Date

@Entity
data class Post(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val description: String,
    var ownerId: String,
    val breedId: Int,
    val breedName: String,
    var image: String,
    @TypeConverters(DateConverter::class) val creationDate: Date
) {

    constructor() : this("", 0, "")

    constructor(
        description: String,
        breedId: Int,
        breedName: String
    ) : this("", description, "", breedId, breedName, "", Date())

    constructor(
        id: String,
        description: String,
        breedId: Int,
        breedName: String
    ) : this(id, description, "", breedId, breedName, "", Date())

    companion object {

        private const val POST_ID_KEY = "id"
        const val DESCRIPTION_KEY = "description"
        const val OWNER_ID_KEY = "ownerId"
        const val BREED_ID_KEY = "breedId"
        const val BREED_NAME_KEY = "breedName"
        const val IMAGE_KEY = "image"
        const val CREATION_DATE_KEY = "creationDate"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[POST_ID_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val ownerId = json[OWNER_ID_KEY] as? String ?: ""
            val breedId = json[BREED_ID_KEY] as? Long ?: 0
            val breedName = json[BREED_NAME_KEY] as? String ?: ""
            val image = json[IMAGE_KEY] as? String ?: ""
            val date = json[CREATION_DATE_KEY] as? Date ?: Date()
            return Post(id, description, ownerId, breedId.toInt(), breedName, image, date)
        }

        fun getUpdateMap(post: Post): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[DESCRIPTION_KEY] = post.description
            updates[BREED_ID_KEY] = post.breedId
            updates[BREED_NAME_KEY] = post.breedName
            return updates
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                DESCRIPTION_KEY to description,
                OWNER_ID_KEY to ownerId,
                BREED_ID_KEY to breedId,
                BREED_NAME_KEY to breedName,
                IMAGE_KEY to image,
                CREATION_DATE_KEY to creationDate
            )
        }
}

