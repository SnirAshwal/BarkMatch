package com.BarkMatch.models

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity

class Model private constructor() {

    private val firebaseModel = FirebaseModel()


    companion object {
        val instance: Model = Model()
    }

    fun loginUser(context: Activity, view: View, email: String, password: String) {
        firebaseModel.loginUser(context, view, email, password)
    }

    fun registerUser(
        context: FragmentActivity, view: View,
        email: String, password: String, newUser: User, imageUri: Uri?
    ) {
        firebaseModel.registerUser(
            context,
            view,
            email,
            password,
            newUser,
            imageUri
        )
    }

    fun logoutUser(context: Activity) {
        firebaseModel.logoutUser(context)
    }

    fun isUserWithEmailExists(email: String, callback: (Boolean) -> Unit) {
        firebaseModel.isUserWithEmailExists(email) { isUserExists ->
            callback(isUserExists)
        }
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        firebaseModel.getAllPosts(callback)
    }

    fun getAllPostsByUserId(userId: String, callback: (List<Post>) -> Unit) {
        firebaseModel.getAllPostsByUserId(userId, callback)
    }

    fun createPost(post: Post, imageUri: Uri, callback: () -> Unit) {
        firebaseModel.createPost(post, imageUri, callback)
    }

    fun getUserDetails(userId: String, callback: (User, Int) -> Unit) {
        firebaseModel.getUserDetails(userId) { user, postCount ->
            callback(user, postCount)
        }
    }

    fun editUserDetails(user: User, newProfileImageUri: Uri?, callback: (Boolean) -> Unit) {
        firebaseModel.editUserDetails(user, newProfileImageUri) { isSuccess ->
            callback(isSuccess)
        }
    }
}