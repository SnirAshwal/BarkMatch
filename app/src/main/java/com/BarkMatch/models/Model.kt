package com.BarkMatch.models

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.BarkMatch.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val instance: Model = Model()
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        executor.execute {
            val posts = database.postDao().getAll()
            mainHandler.post {
                // Main Thread
                callback(posts)
            }
        }
    }

    fun getAllPostsByUserId(userId: Int, callback: (List<Post>) -> Unit) {
        executor.execute {
            val posts = database.postDao().getAllByUserId(userId)
            mainHandler.post {
                // Main Thread
                callback(posts)
            }
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        executor.execute {
            database.postDao().insert(post)
            mainHandler.post {
                callback()
            }
        }
    }
}