package com.BarkMatch.models

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.DocumentSnapshot

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

    fun getInitialFeedPosts(callback: (MutableList<Post>) -> Unit) {
        firebaseModel.getInitialFeedPosts(callback)
    }

    fun loadMorePostsForFeed(callback: (MutableList<Post>) -> Unit) {
        firebaseModel.loadMorePostsForFeed(callback)
    }

    fun getInitialProfileFeedPostsByUserId(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        firebaseModel.getInitialProfileFeedPostsByUserId(userId, callback)
    }

    fun loadMorePostsForProfileFeed(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        firebaseModel.loadMorePostsForProfileFeed(userId, callback)
    }

    fun createPost(post: Post, imageUri: Uri, callback: () -> Unit) {
        firebaseModel.createPost(post, imageUri, callback)
    }

    fun getUserDetails(userId: String, callback: (User, Int) -> Unit) {
        firebaseModel.getUserDetails(userId) { user, postCount ->
            callback(user, postCount)
        }
    }

    fun getUserContactDetails(userId: String, callback: (String, String) -> Unit) {
        firebaseModel.getUserContactDetails(userId) { phoneNumber, fullName ->
            callback(phoneNumber, fullName)
        }
    }

    fun editUserDetails(user: User, newProfileImageUri: Uri?, callback: (Boolean) -> Unit) {
        firebaseModel.editUserDetails(user, newProfileImageUri) { isSuccess ->
            callback(isSuccess)
        }
    }

    fun getEditPostDetails(postId: String, callback: (Post, String, String) -> Unit) {
        firebaseModel.getEditPostDetails(postId) { post, fullName, phoneNumber ->
            callback(post, fullName, phoneNumber)
        }
    }

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
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