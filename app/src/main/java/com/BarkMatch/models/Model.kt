package com.BarkMatch.models

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.view.View
import androidx.core.os.HandlerCompat
import androidx.fragment.app.FragmentActivity
import com.BarkMatch.dao.AppLocalDatabase
import java.net.URI
import java.util.concurrent.Executors

class Model private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel = FirebaseModel()


    companion object {
        val instance: Model = Model()
    }

    fun loginUser(context: Activity, view: View, email: String, password: String) {
        firebaseModel.loginUser(context, view, email, password)
    }

    fun registerUser(
        context: FragmentActivity, view: View,
        email: String, password: String,
        firstName: String, lastName: String,
        profileImage: String, phoneNumber: String,
        description: String
    ) {
        firebaseModel.registerUser(
            context,
            view,
            email,
            password,
            firstName,
            lastName,
            profileImage,
            phoneNumber,
            description
        )
    }

    fun logoutUser(context: Activity) {
        firebaseModel.logoutUser(context)
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        firebaseModel.getAllPosts(callback)
    }

    fun getAllPostsByUserId(userId: Int, callback: (List<Post>) -> Unit) {
        // firebaseModel.getAllPostsByUserId(userId, callback)
        executor.execute {
            val posts = database.postDao().getAllByUserId(userId)
            mainHandler.post {
                // Main Thread
                callback(posts)
            }
        }
    }

    fun createPost(post: Post, imageUri: Uri, callback: () -> Unit) {
        firebaseModel.createPost(post, imageUri, callback)
    }
}