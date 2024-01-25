package com.barkMatch.models

import android.net.Uri
import android.util.Log
import com.barkMatch.adapters.FeedRecyclerAdapter
import com.barkMatch.adapters.FeedRecyclerAdapter.Companion.FEED_PAGE_SIZE
import com.barkMatch.adapters.ProfileFeedRecyclerAdapter
import com.barkMatch.adapters.ProfileFeedRecyclerAdapter.Companion.PROFILE_PAGE_SIZE
import com.barkMatch.interfaces.AuthenticationCallback
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

    fun loginUser(
        email: String,
        password: String,
        authenticationCallback: AuthenticationCallback,
        callback: (Boolean) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) authenticationCallback.onSuccess()
                else authenticationCallback.onFailure()
                callback(task.isSuccessful)
            }
    }

    fun registerUser(
        email: String,
        password: String,
        newUser: User,
        imageUri: Uri?, callback: (Boolean) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val userId = user?.uid

                    // Save additional fields to FireStore
                    saveUserDetailsToDB(
                        userId,
                        newUser,
                        imageUri
                    ) { isSuccess ->
                        callback(isSuccess)
                    }
                } else {
                    Log.e(
                        "TAG",
                        "Failed to save user credentials in firestore authentication for user with email ${newUser.email}"
                    )
                    callback(false)
                }
            }
    }

    private fun saveUserDetailsToDB(
        userId: String?,
        newUser: User,
        imageUri: Uri?, callback: (Boolean) -> Unit
    ) {
        if (userId != null) {
            newUser.id = userId

            if (imageUri != null) {
                val timestamp = System.currentTimeMillis()
                uploadImage(imageUri, "images/$userId/profile/profile_$timestamp.jpg") { imageUrl ->
                    if (imageUrl != null) {
                        newUser.profileImage = imageUrl
                        saveUserInDB(userId, newUser) { isSuccess ->
                            callback(isSuccess)
                        }
                    } else {
                        Log.e(
                            "TAG",
                            "Failed to upload user profile image for user with email ${newUser.email}"
                        )
                    }
                }
            } else {
                saveUserInDB(userId, newUser) { isSuccess ->
                    callback(isSuccess)
                }
            }
        }
    }

    private fun saveUserInDB(
        userId: String,
        newUser: User, callback: (Boolean) -> Unit
    ) {
        db.collection(USERS_COLLECTION_PATH).document(userId).set(newUser)
            .addOnSuccessListener {
                Log.e("TAG", "Crated user with email ${newUser.email} successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to save user in DB", e)
                callback(false)
            }
    }

    fun isUserWithEmailExists(
        email: String,
        callback: (Boolean) -> Unit
    ) {
        db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val isUserExists = !querySnapshot.isEmpty
                callback(isUserExists)
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to get if user exists", e)
                callback(false)
            }
    }

    fun logoutUser(callback: (Boolean) -> Unit) {
        try {
            auth.signOut()
            Log.i("TAG", "Logged out user successfully")
            callback(true)
        } catch (e: Exception) {
            Log.e("TAG", "Failed to logout user", e)
            callback(false)
        }
    }

    fun getPostsForFeed(callback: (MutableList<Post>) -> Unit) {
        if(FeedRecyclerAdapter.lastVisiblePost  == null) FeedRecyclerAdapter.isLastPage = false
        FeedRecyclerAdapter.isLoading = true

        val query = if (FeedRecyclerAdapter.lastVisiblePost != null) {
            db.collection(POSTS_COLLECTION_PATH)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .startAfter(FeedRecyclerAdapter.lastVisiblePost?.getDate("creationDate"))
                .limit(FEED_PAGE_SIZE)
        } else {
            db.collection(POSTS_COLLECTION_PATH)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .limit(FEED_PAGE_SIZE)
        }

        query.get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }

                        FeedRecyclerAdapter.isLoading = false
                        FeedRecyclerAdapter.isLastPage = posts.size < FEED_PAGE_SIZE

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

    fun getPostsForProfileFeed(
        userId: String,
        callback: (MutableList<Post>) -> Unit
    ) {
        if(ProfileFeedRecyclerAdapter.lastVisiblePost  == null) ProfileFeedRecyclerAdapter.isLastPage = false
        ProfileFeedRecyclerAdapter.isLoading = true

        val query = if (ProfileFeedRecyclerAdapter.lastVisiblePost != null) {
            db.collection(POSTS_COLLECTION_PATH)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .whereEqualTo("ownerId", userId)
                .startAfter(ProfileFeedRecyclerAdapter.lastVisiblePost?.getDate("creationDate"))
                .limit(PROFILE_PAGE_SIZE)
        } else {
            db.collection(POSTS_COLLECTION_PATH)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .whereEqualTo("ownerId", userId)
                .limit(PROFILE_PAGE_SIZE)
        }

        query
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

    fun createPost(
        post: Post,
        imageUri: Uri,
        callback: () -> Unit
    ) {
        val userId = auth.currentUser?.uid
        val timestamp = System.currentTimeMillis()

        uploadImage(imageUri, "images/$userId/posts/post_$timestamp.jpg") { imageUrl ->
            if (imageUrl != null) {
                post.ownerId = auth.currentUser?.uid ?: ""
                post.image = imageUrl
                createPostInDB(post, callback)
            } else {
                Log.e("TAG", "Image upload failed")
            }
        }
    }

    private fun createPostInDB(
        post: Post,
        callback: () -> Unit
    ) {
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
                        callback()
                    }
                    .addOnFailureListener { e ->
                        Log.e("TAG", "Image upload failed", e)
                        callback()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to save post to DB", e)
            }
    }

    private fun uploadImage(
        imageUri: Uri,
        filename: String,
        callback: (String?) -> Unit
    ) {
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
                Log.e("TAG", "Failed to upload image", e)
                callback(null)
            }
    }

    fun editUserDetails(
        user: User,
        newProfileImageUri: Uri?,
        callback: (Boolean) -> Unit
    ) {
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

    fun getUserDetails(
        userId: String,
        callback: (User, Int) -> Unit
    ) {
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
                Log.e("TAG", "Failed to get user details", e)
                callback(User(), 0)
            }
    }

    fun getEditPostDetails(
        postId: String,
        callback: (Post, String, String) -> Unit
    ) {
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
            Log.e("TAG", "Failed to edit post details in DB", e)
            callback(Post(), "", "")
        }
    }

    fun getUserContactDetails(
        userId: String,
        callback: (String, String) -> Unit
    ) {
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
                Log.e("TAG", "Failed to get user contact details", e)
                callback("", "")
            }
    }

    private fun getPostCountForUser(
        userId: String,
        callback: (Int) -> Unit
    ) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Return the count of posts
                callback(querySnapshot.size())
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to get the amount of posts of the user", e)
                callback(0)
            }
    }

    fun deletePost(
        postId: String,
        callback: (Boolean) -> Unit
    ) {
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
        imageUri: Uri?,
        callback: (Boolean) -> Unit
    ) {
        if (imageUri != null) {
            val userId = auth.currentUser?.uid
            val timestamp = System.currentTimeMillis()

            uploadImage(imageUri, "images/$userId/posts/post_$timestamp.jpg") { imageUrl ->
                if (imageUrl != null) {
                    savePostDetails(
                        postId,
                        breedName,
                        breedId,
                        description,
                        imageUrl
                    ) { isSuccess ->
                        callback(isSuccess)
                    }
                } else {
                    Log.e("TAG", "Image upload failed")
                }
            }
        } else {
            savePostDetails(
                postId,
                breedName,
                breedId,
                description,
                null
            ) { isSuccess ->
                callback(isSuccess)
            }
        }
    }

    private fun savePostDetails(
        postId: String,
        breedName: String,
        breedId: Int,
        description: String,
        imageUrl: String?, callback: (Boolean) -> Unit
    ) {
        val updateMap = Post.getUpdateMap(Post(postId, description, breedId, breedName))
        if (imageUrl != null) updateMap[Post.IMAGE_KEY] = imageUrl

        db.collection(POSTS_COLLECTION_PATH).document(postId)
            .update(updateMap)
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