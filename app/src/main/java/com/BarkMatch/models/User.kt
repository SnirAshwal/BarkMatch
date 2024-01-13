package com.BarkMatch.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val profileImage: String,
    val phoneNumber: String,
    val description: String
)