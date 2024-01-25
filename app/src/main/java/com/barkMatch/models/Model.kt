package com.barkMatch.models

import android.net.Uri
import com.barkMatch.interfaces.AuthenticationCallback

class Model private constructor() {

    private val firebaseModel = FirebaseModel()

    companion object {
        val instance: Model = Model()
    }

    fun loginUser(
        email: String,
        password: String,
        authenticationCallback: AuthenticationCallback,
        callback: (Boolean) -> Unit
    ) {
        firebaseModel.loginUser(email, password, authenticationCallback) { isSuccess ->
            callback(isSuccess)
        }
    }

    fun registerUser(
        email: String,
        password: String,
        newUser: User,
        imageUri: Uri?, callback: (Boolean) -> Unit
    ) {
        firebaseModel.registerUser(
            email,
            password,
            newUser,
            imageUri
        ) { isSuccess ->
            callback(isSuccess)
        }
    }

    fun logoutUser(callback: (Boolean) -> Unit) {
        firebaseModel.logoutUser { isSuccess ->
            callback(isSuccess)
        }
    }

    fun isUserWithEmailExists(
        email: String,
        callback: (Boolean) -> Unit
    ) {
        firebaseModel.isUserWithEmailExists(email) { isUserExists ->
            callback(isUserExists)
        }
    }

    fun getPostsForFeed(callback: (MutableList<Post>) -> Unit) {
        firebaseModel.getPostsForFeed(callback)
    }

    fun getPostsForProfileFeed(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        firebaseModel.getPostsForProfileFeed(userId, callback)
    }

    fun createPost(
        post: Post,
        imageUri: Uri,
        callback: () -> Unit
    ) {
        firebaseModel.createPost(post, imageUri, callback)
    }

    fun getUserDetails(
        userId: String,
        callback: (User, Int) -> Unit
    ) {
        firebaseModel.getUserDetails(userId) { user, postCount ->
            callback(user, postCount)
        }
    }

    fun getUserContactDetails(
        userId: String,
        callback: (String, String) -> Unit
    ) {
        firebaseModel.getUserContactDetails(userId) { phoneNumber, fullName ->
            callback(phoneNumber, fullName)
        }
    }

    fun editUserDetails(
        user: User,
        newProfileImageUri: Uri?,
        callback: (Boolean) -> Unit
    ) {
        firebaseModel.editUserDetails(user, newProfileImageUri) { isSuccess ->
            callback(isSuccess)
        }
    }

    fun getEditPostDetails(
        postId: String,
        callback: (Post, String, String) -> Unit
    ) {
        firebaseModel.getEditPostDetails(postId) { post, fullName, phoneNumber ->
            callback(post, fullName, phoneNumber)
        }
    }

    fun deletePost(
        postId: String,
        callback: (Boolean) -> Unit
    ) {
        firebaseModel.deletePost(postId) { isSuccess ->
            callback(isSuccess)
        }
    }

    fun editPost(
        postId: String,
        breedName: String,
        breedId: Int,
        description: String,
        imageUri: Uri?, callback: (Boolean) -> Unit
    ) {
        firebaseModel.editPost(postId, breedName, breedId, description, imageUri) { isSuccess ->
            callback(isSuccess)
        }
    }
}