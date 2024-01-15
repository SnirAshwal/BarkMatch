package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String,
    val phoneNumber: String,
    val description: String
)