package com.barkMatch.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.barkMatch.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
data class Post(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val description: String,
    var ownerId: String,
    val breedId: Int,
    val breedName: String,
    var image: String,
    var creationDate: Long? = null,
    val updatedAt: Long? = null,
) {

    constructor() : this("", "", "", 0, "", "")

    constructor(
        description: String,
        breedId: Int,
        breedName: String
    ) : this(
        "",
        description,
        "",
        breedId,
        breedName,
        "",
        System.currentTimeMillis()
    )

    constructor(
        id: String,
        description: String,
        breedId: Int,
        breedName: String
    ) : this(id, description, "", breedId, breedName, "")

    companion object {

        var creationDate: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_CREATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(GET_LAST_CREATED, value)?.apply()
            }

        const val POST_ID_KEY = "id"
        const val DESCRIPTION_KEY = "description"
        const val OWNER_ID_KEY = "ownerId"
        const val BREED_ID_KEY = "breedId"
        const val BREED_NAME_KEY = "breedName"
        const val IMAGE_KEY = "image"
        const val CREATION_DATE_KEY = "creationDate"
        const val UPDATED_AT_KEY = "updatedAt"
        private const val GET_LAST_CREATED = "get_last_created"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[POST_ID_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val ownerId = json[OWNER_ID_KEY] as? String ?: ""
            val breedId = json[BREED_ID_KEY] as? Long ?: 0
            val breedName = json[BREED_NAME_KEY] as? String ?: ""
            val image = json[IMAGE_KEY] as? String ?: ""
            val updatedAt = json[UPDATED_AT_KEY] as? Long

            val post = Post(
                id,
                description,
                ownerId,
                breedId.toInt(),
                breedName,
                image,
                creationDate,
                updatedAt
            )

            val timestamp: Timestamp? = json[CREATION_DATE_KEY] as? Timestamp
            timestamp?.let {
                post.creationDate = it.seconds
            }

            return post
        }

        fun getUpdateMap(post: Post): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[DESCRIPTION_KEY] = post.description
            updates[BREED_ID_KEY] = post.breedId
            updates[BREED_NAME_KEY] = post.breedName
            updates[UPDATED_AT_KEY] = Timestamp(System.currentTimeMillis() / 1000, 0)
            return updates
        }
    }

    val json: Map<String, Any?>
        get() {
            return hashMapOf(
                DESCRIPTION_KEY to description,
                OWNER_ID_KEY to ownerId,
                BREED_ID_KEY to breedId,
                BREED_NAME_KEY to breedName,
                IMAGE_KEY to image,
                CREATION_DATE_KEY to FieldValue.serverTimestamp(),
                UPDATED_AT_KEY to updatedAt,
            )
        }
}

