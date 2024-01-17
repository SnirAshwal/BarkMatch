package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val firstName: String,
    val lastName: String,
    var profileImage: String,
    val phoneNumber: String,
    val description: String,
    val email: String
) {

    constructor() : this("", "", "", "", "")

    constructor(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        description: String,
        email: String
    ) : this("", firstName, lastName, "", phoneNumber, description, email)

    constructor(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        description: String,
    ) : this("", firstName, lastName, "", phoneNumber, description, "")

    companion object {

        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
        private const val DESCRIPTION_KEY = "description"
        private const val PHONE_NUMBER_KEY = "phoneNumber"
        private const val PROFILE_IMAGE_KEY = "profileImage"

        fun getUpdateMapWithImage(user: User): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[FIRST_NAME_KEY] = user.firstName
            updates[LAST_NAME_KEY] = user.lastName
            updates[DESCRIPTION_KEY] = user.description
            updates[PHONE_NUMBER_KEY] = user.phoneNumber
            updates[PROFILE_IMAGE_KEY] = user.profileImage
            return updates
        }

        fun getUpdateMap(user: User): MutableMap<String, Any> {
            val updates = mutableMapOf<String, Any>()
            updates[FIRST_NAME_KEY] = user.firstName
            updates[LAST_NAME_KEY] = user.lastName
            updates[DESCRIPTION_KEY] = user.description
            updates[PHONE_NUMBER_KEY] = user.phoneNumber
            return updates
        }
    }
}