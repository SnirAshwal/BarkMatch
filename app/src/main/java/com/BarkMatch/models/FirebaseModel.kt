package com.BarkMatch.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import com.BarkMatch.HomeActivity
import com.BarkMatch.MainActivity
import com.BarkMatch.adapters.FeedRecyclerAdapter
import com.BarkMatch.adapters.ProfileFeedRecyclerAdapter
import com.BarkMatch.adapters.ProfileFeedRecyclerAdapter.Companion.PROFILE_PAGE_SIZE
import com.BarkMatch.adapters.FeedRecyclerAdapter.Companion.FEED_PAGE_SIZE
import com.BarkMatch.utils.SnackbarUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.Executors

class FirebaseModel {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private var executor = Executors.newSingleThreadExecutor()
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
        const val USERS_COLLECTION_PATH = "users"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }

        db.firestoreSettings = settings

        storage = Firebase.storage
        storageRef = storage!!.reference
    }

    fun loginUser(context: Context, view: View, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    // Login successful - moving to feed
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                } else {
                    SnackbarUtils.showSnackbar(view, "Incorrect email or password")
                }
            }
    }

    fun registerUser(
        context: Context,
        view: View,
        email: String,
        password: String,
        newUser: User,
        imageUri: Uri?
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val userId = user?.uid

                    // Save additional fields to FireStore
                    saveUserDetailsToFireStore(
                        context,
                        userId,
                        newUser,
                        imageUri
                    )
                } else {
                    SnackbarUtils.showSnackbar(view, "Registration failed")
                }
            }
    }

    private fun saveUserDetailsToFireStore(
        context: Context,
        userId: String?,
        newUser: User,
        imageUri: Uri?
    ) {
        if (userId != null) {
            if (imageUri != null) {
                val timestamp = System.currentTimeMillis()
                uploadImage(imageUri, "images/$userId/profile/profile_$timestamp.jpg") { imageUrl ->
                    if (imageUrl != null) {
                        newUser.profileImage = imageUrl
                        newUser.id = userId
                        saveUserInDB(context, userId, newUser)
                    } else {
                        // Handle the case where image upload failed
                        println("Image upload failed")
                    }
                }
            } else {
                saveUserInDB(context, userId, newUser)
            }
        }
    }

    private fun saveUserInDB(
        context: Context,
        userId: String,
        newUser: User
    ) {
        db.collection(USERS_COLLECTION_PATH).document(userId).set(newUser)
            .addOnSuccessListener {
                // User data saved successfully - moving to feed
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    fun isUserWithEmailExists(email: String, callback: (Boolean) -> Unit) {
        db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if any documents match the query
                val isUserExists = !querySnapshot.isEmpty
                callback(isUserExists)
            }
            .addOnFailureListener { e ->
                // Handle the exception or error during the operation
                callback(false)
            }
    }

    fun logoutUser(context: Context) {
        auth.signOut()

        // Go back to the welcome page
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun getInitialFeedPosts(callback: (MutableList<Post>) -> Unit) {
        FeedRecyclerAdapter.isLastPage = false
        FeedRecyclerAdapter.isLoading = true

        db.collection(POSTS_COLLECTION_PATH)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .limit(FEED_PAGE_SIZE)
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }

                        FeedRecyclerAdapter.isLoading = false

                        if (!it.result.isEmpty) {
                            FeedRecyclerAdapter.lastVisiblePost = it.result.last()
                        }

                        callback(posts)
                    }

                    false -> {
                        FeedRecyclerAdapter.isLoading = false
                        callback(mutableListOf())
                    }
                }
            }
    }

    fun loadMorePostsForFeed(callback: (MutableList<Post>) -> Unit) {
        FeedRecyclerAdapter.isLoading = true

        db.collection(POSTS_COLLECTION_PATH)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .startAfter(FeedRecyclerAdapter.lastVisiblePost?.getDate("creationDate"))
            .limit(FEED_PAGE_SIZE)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }

                        FeedRecyclerAdapter.isLoading = false
                        FeedRecyclerAdapter.isLastPage = posts.size < PROFILE_PAGE_SIZE

                        if (!it.result.isEmpty) {
                            FeedRecyclerAdapter.lastVisiblePost = it.result.last()
                        }

                        callback(posts)
                    }

                    false -> {
                        FeedRecyclerAdapter.isLoading = false
                        callback(mutableListOf())
                    }
                }
            }
    }

    fun getInitialProfileFeedPostsByUserId(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        ProfileFeedRecyclerAdapter.isLastPage = false
        ProfileFeedRecyclerAdapter.isLoading = true

        db.collection(POSTS_COLLECTION_PATH)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .whereEqualTo("ownerId", userId)
            .limit(PROFILE_PAGE_SIZE)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }

                        ProfileFeedRecyclerAdapter.isLoading = false

                        if (!it.result.isEmpty) {
                            ProfileFeedRecyclerAdapter.lastVisiblePost = it.result.last()
                        }

                        callback(posts)
                    }

                    false -> {
                        ProfileFeedRecyclerAdapter.isLoading = false
                        callback(mutableListOf())
                    }
                }
            }
    }

    fun loadMorePostsForProfileFeed(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        ProfileFeedRecyclerAdapter.isLoading = true

        db.collection(POSTS_COLLECTION_PATH)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .whereEqualTo("ownerId", userId)
            .startAfter(ProfileFeedRecyclerAdapter.lastVisiblePost?.getDate("creationDate"))
            .limit(PROFILE_PAGE_SIZE)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }

                        ProfileFeedRecyclerAdapter.isLoading = false
                        ProfileFeedRecyclerAdapter.isLastPage = posts.size < PROFILE_PAGE_SIZE

                        if (!it.result.isEmpty) {
                            ProfileFeedRecyclerAdapter.lastVisiblePost = it.result.last()
                        }

                        callback(posts)
                    }

                    false -> {
                        ProfileFeedRecyclerAdapter.isLoading = false
                        callback(mutableListOf())
                    }
                }
            }
    }

    fun createPost(post: Post, imageUri: Uri, callback: () -> Unit) {
        val userId = auth.currentUser?.uid
        val timestamp = System.currentTimeMillis()

        uploadImage(imageUri, "images/$userId/posts/post_$timestamp.jpg") { imageUrl ->
            if (imageUrl != null) {
                post.ownerId = auth.currentUser?.uid ?: ""
                post.image = imageUrl
                createPostInDB(post, callback)
            } else {
                // Handle the case where image upload failed
                println("Image upload failed")
            }
        }
    }

    private fun createPostInDB(post: Post, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).add(post.json)
            .addOnSuccessListener { documentReference ->
                // Update the postId in the local Post instance
                val generatedPostId = documentReference.id
                post.id = generatedPostId

                // Update the postId in the Firestore document
                val postDocumentRef =
                    db.collection(POSTS_COLLECTION_PATH).document(documentReference.id)
                postDocumentRef.update("id", generatedPostId)
                    .addOnSuccessListener {
                        // The postId has been updated in the Firestore document
                        callback()
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure
                        callback()
                    }
            }
            .addOnFailureListener { e ->
                // handle error
            }
    }

    private fun uploadImage(imageUri: Uri, filename: String, callback: (String?) -> Unit) {
        val imageRef = storageRef?.child(filename)

        // Upload file to Firebase Storage
        imageRef?.putFile(imageUri)
            ?.addOnSuccessListener { _ ->
                // Image uploaded successfully
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            ?.addOnFailureListener { e ->
                // Handle errors during the upload
                callback(null)
            }
    }

    fun editUserDetails(user: User, newProfileImageUri: Uri?, callback: (Boolean) -> Unit) {
        user.id = auth.currentUser?.uid.toString()

        if (newProfileImageUri != null) {
            val timestamp = System.currentTimeMillis()
            uploadImage(
                newProfileImageUri,
                "images/${user.id}/profile/profile_$timestamp.jpg"
            ) { imageUrl ->
                if (imageUrl != null) {
                    user.profileImage = imageUrl
                    editUserDetailsInDB(user, true) { isSuccess ->
                        callback(isSuccess)
                    }
                } else {
                    callback(false)
                }
            }
        } else {
            editUserDetailsInDB(user, false) { isSuccess ->
                callback(isSuccess)
            }
        }
    }

    private fun editUserDetailsInDB(
        user: User,
        isProfileImageUpdated: Boolean,
        callback: (Boolean) -> Unit
    ) {
        val userRef: DocumentReference = db.collection(USERS_COLLECTION_PATH).document(user.id)
        val updates =
            if (isProfileImageUpdated) User.getUpdateMapWithImage(user) else User.getUpdateMap(user)

        userRef.update(updates)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUserDetails(userId: String, callback: (User, Int) -> Unit) {
        val userRef: DocumentReference = db.collection(USERS_COLLECTION_PATH).document(userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        getPostCountForUser(userId) { postCount ->
                            callback(user, postCount)
                        }
                    } else {
                        callback(User(), 0)
                    }
                } else {
                    // User document does not exist
                    callback(User(), 0)
                }
            }
            .addOnFailureListener { e ->
                // Handle the exception
                callback(User(), 0)
            }
    }

    fun getEditPostDetails(postId: String, callback: (Post, String, String) -> Unit) {
        val postReference = db.collection(POSTS_COLLECTION_PATH).document(postId)
        postReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val post = documentSnapshot.toObject(Post::class.java)
                if (post != null) {
                    getUserContactDetails(post.ownerId) { phoneNumber, fullName ->
                        callback(post, fullName, phoneNumber)
                    }
                } else {
                    callback(Post(), "", "")
                }
            } else {
                callback(Post(), "", "")
            }
        }.addOnFailureListener { e ->
            callback(Post(), "", "")
        }
    }

    fun getUserContactDetails(userId: String, callback: (String, String) -> Unit) {
        val userRef: DocumentReference = db.collection(USERS_COLLECTION_PATH).document(userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    callback(
                        user?.phoneNumber ?: "",
                        (user?.firstName + " " + user?.lastName)
                    )
                } else {
                    // User document does not exist
                    callback("", "")
                }
            }
            .addOnFailureListener { e ->
                // Handle the exception
                callback("", "")
            }
    }

    private fun getPostCountForUser(userId: String, callback: (Int) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Return the count of posts
                callback(querySnapshot.size())
            }
            .addOnFailureListener { e ->
                // Handle the exception
                callback(0)
            }
    }

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
        val postDocumentRef = db.collection(POSTS_COLLECTION_PATH).document(postId)

        postDocumentRef.delete()
            .addOnSuccessListener {
                Log.i("TAG", "Post with ID $postId deleted successfully")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Failed to delete post with ID $postId, Error: $exception")
                callback(false)
            }
    }

    fun editPost(
        postId: String,
        breedName: String,
        breedId: Int,
        description: String,
        callback: (Boolean) -> Unit
    ) {
        db.collection(POSTS_COLLECTION_PATH).document(postId)
            .update(Post.getUpdateMap(Post(postId, description, breedId, breedName)))
            .addOnSuccessListener {
                Log.i("TAG", "Post with ID $postId updated successfully")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Failed to update post with ID $postId, Error: $exception")
                callback(false)
            }
    }
}