package com.barkMatch.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.barkMatch.dao.AppLocalDatabase
import com.barkMatch.interfaces.AuthenticationCallback
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val firebaseModel = FirebaseModel()
    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private val feedPosts: LiveData<MutableList<Post>>? = null
    private val profilePosts: LiveData<MutableList<Post>>? = null
    private val profileUserDetails: LiveData<User>? = null
    val feedPostsLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)
    val profilePostsLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.LOADED)

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

    fun getPostsForFeed(): LiveData<MutableList<Post>> {
        refreshPostsForFeed()
        return feedPosts ?: database.postDao().getAll()
    }

    fun refreshPostsForFeed() {
        feedPostsLoadingState.value = LoadingState.LOADING
        val lastCreated: Long = Post.creationDate

        firebaseModel.getPostsForFeed(lastCreated) { list ->
            Log.i("TAG", "Firebase returned ${list.size}, lastUpdated: $lastCreated")
            executor.execute {
                var time = lastCreated
                for (post in list) {
                    database.postDao().insert(post)

                    post.creationDate?.let {
                        if (time < it)
                            time = post.creationDate ?: System.currentTimeMillis()
                    }
                }

                Post.creationDate = time
                feedPostsLoadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun getPostsForProfileFeed(userId: String): LiveData<MutableList<Post>> {
        refreshPostsForProfile(userId)
        return profilePosts ?: database.postDao().getAllByUserId(userId)
    }

    fun refreshPostsForProfile(userId: String) {
        profilePostsLoadingState.value = LoadingState.LOADING
        val lastCreated: Long = Post.creationDate

        firebaseModel.getPostsForProfileFeed(userId, lastCreated) { list ->
            Log.i("TAG", "Firebase returned ${list.size}, lastUpdated: $lastCreated")
            executor.execute {
                var time = lastCreated
                for (post in list) {
                    database.postDao().insert(post)

                    post.creationDate?.let {
                        if (time < it)
                            time = post.creationDate ?: System.currentTimeMillis()
                    }
                }

                Post.creationDate = time
                profilePostsLoadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun createPost(
        post: Post,
        imageUri: Uri,
        callback: () -> Unit
    ) {
        firebaseModel.createPost(post, imageUri) {
            refreshPostsForFeed()
            callback()
        }
    }

    fun getUserDetails(
        userId: String,
    ): LiveData<User> {
        refreshUserDetails(userId)
        return profileUserDetails ?: database.userDao().getUserById(userId)
    }

    fun refreshUserDetails(userId: String) {
        val lastUpdated: Long = User.lastUpdated

        firebaseModel.getUserDetails(userId, lastUpdated) { user ->
            executor.execute {
                var time = lastUpdated
                database.userDao().insert(user)

                user.updatedAt?.let {
                    if (time < it)
                        time = user.updatedAt ?: System.currentTimeMillis()
                }

                User.lastUpdated = time
            }
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
            executor.execute {
                database.postDao().delete(postId) // Remove from local
            }
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
        firebaseModel.editPost(
            postId,
            breedName,
            breedId,
            description,
            imageUri
        ) { isSuccess, imageUrl ->
            executor.execute {
                database.postDao()
                    .update(postId, breedName, breedId, description, imageUrl) // Update in local
            }
            callback(isSuccess)
        }
    }
}