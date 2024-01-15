package com.BarkMatch.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.BarkMatch.HomeActivity
import com.BarkMatch.MainActivity
import com.BarkMatch.utils.SnackbarUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.net.URI
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
        email: String, password: String,
        firstName: String, lastName: String,
        profileImage: String, phoneNumber: String,
        description: String
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
                        firstName,
                        lastName,
                        profileImage,
                        phoneNumber,
                        description
                    )
                } else {
                    SnackbarUtils.showSnackbar(view, "Registration failed")
                }
            }
    }

    private fun saveUserDetailsToFireStore(
        context: Context, userId: String?, firstName: String, lastName: String,
        profileImage: String, phoneNumber: String, description: String
    ) {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val user = User(
                id = userId,
                firstName = firstName,
                lastName = lastName,
                profileImage = profileImage,
                phoneNumber = phoneNumber,
                description = description
            )

            db.collection(USERS_COLLECTION_PATH).document(userId).set(user)
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
    }

    fun logoutUser(context: Context) {
        auth.signOut()

        // Go back to the welcome page
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        val post = Post.fromJSON(json.data)
                        posts.add(post)
                    }
                    callback(posts)
                }

                false -> callback(listOf())
            }
        }
    }

    fun getAllPostsByUserId(userId: String, callback: (List<Post>) -> Unit) {
//        db.collection(STUDENTS_COLLECTION_PATH).get().addOnCompleteListener {
//            when (it.isSuccessful) {
//                true -> {
//                    val students: MutableList<Student> = mutableListOf()
//                    for (json in it.result) {
//                        val student = Student.fromJSON(json.data)
//                        students.add(student)
//                    }
//                    callback(students)
//                }
//                false -> callback(listOf())
//            }
//        }
    }

    fun createPost(post: Post, imageUri: Uri, callback: () -> Unit) {
        val userId = auth.currentUser?.uid
        val timestamp = System.currentTimeMillis()
        val imageRef = storageRef?.child("images/$userId/post_$timestamp.jpg")

        // Upload file to Firebase Storage
        imageRef?.putFile(imageUri)
            ?.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    post.ownerId = auth.currentUser?.uid ?: ""
                    post.image = uri.toString()
                    createPostInDB(post, callback)
                }
            }
            ?.addOnFailureListener { e ->
                // Handle errors during the upload
            }
    }

    private fun createPostInDB(post: Post, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).add(post.json)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                // handle error
            }
    }
}