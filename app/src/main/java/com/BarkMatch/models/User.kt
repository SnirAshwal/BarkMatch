package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val firstName: String,
    val lastName: String,
    var profileImage: String,
    val phoneNumber: String,
    val description: String,
    val email: String
) {
    constructor(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        description: String,
        email: String
    ) : this("", firstName, lastName, "", phoneNumber, description, email)
}