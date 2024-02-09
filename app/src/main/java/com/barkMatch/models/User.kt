package com.barkMatch.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.barkMatch.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val firstName: String,
    val lastName: String,
    var profileImage: String,
    val phoneNumber: String,
    val description: String,
    val email: String,
    var updatedAt: Long?,
    val creationDate: Long?
) {

    constructor() : this("", "", "", "", "", "", "", null, null)

    constructor(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        description: String,
        email: String
    ) : this(
        "",
        firstName,
        lastName,
        "",
        phoneNumber,
        description,
        email,
        System.currentTimeMillis(),
        System.currentTimeMillis()
    )

    constructor(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        description: String,
    ) : this("", firstName, lastName, "", phoneNumber, description, "", null, null)

    companion object {

        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(GET_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
        private const val DESCRIPTION_KEY = "description"
        private const val PHONE_NUMBER_KEY = "phoneNumber"
        private const val PROFILE_IMAGE_KEY = "profileImage"
        private const val EMAIL_KEY = "email"
        const val UPDATED_AT_KEY = "updatedAt"
        const val CREATION_DATE_KEY = "creationDate"
        private const val GET_LAST_UPDATED = "get_last_updated"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val firstName = json[FIRST_NAME_KEY] as? String ?: ""
            val lastName = json[LAST_NAME_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val phoneNumber = json[PHONE_NUMBER_KEY] as? String ?: ""
            val profileImage = json[PROFILE_IMAGE_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val creationDate: Timestamp? = json[CREATION_DATE_KEY] as? Timestamp

            val user = User(
                id,
                firstName,
                lastName,
                profileImage,
                phoneNumber,
                description,
                email,
                lastUpdated,
                creationDate?.seconds
            )

            val timestamp: Timestamp? = json[UPDATED_AT_KEY] as? Timestamp
            timestamp?.let {
                user.updatedAt = it.seconds
            }

            return user
        }

        fun getUpdateMapWithImage(user: User): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[FIRST_NAME_KEY] = user.firstName
            updates[LAST_NAME_KEY] = user.lastName
            updates[DESCRIPTION_KEY] = user.description
            updates[PHONE_NUMBER_KEY] = user.phoneNumber
            updates[PROFILE_IMAGE_KEY] = user.profileImage
            updates[UPDATED_AT_KEY] = Timestamp(System.currentTimeMillis() / 1000, 0)
            return updates
        }

        fun getUpdateMap(user: User): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[FIRST_NAME_KEY] = user.firstName
            updates[LAST_NAME_KEY] = user.lastName
            updates[DESCRIPTION_KEY] = user.description
            updates[PHONE_NUMBER_KEY] = user.phoneNumber
            updates[UPDATED_AT_KEY] = Timestamp(System.currentTimeMillis() / 1000, 0)
            return updates
        }
    }

    val json: Map<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                DESCRIPTION_KEY to description,
                PHONE_NUMBER_KEY to phoneNumber,
                PROFILE_IMAGE_KEY to profileImage,
                EMAIL_KEY to email,
                CREATION_DATE_KEY to FieldValue.serverTimestamp(),
                UPDATED_AT_KEY to FieldValue.serverTimestamp()
            )
        }
}