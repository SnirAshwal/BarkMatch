package com.BarkMatch.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.BarkMatch.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    fun getAll(): List<Post>

    @Query("SELECT * FROM Post WHERE ownerId = :id")
    fun getAllByUserId(id: Int): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg posts: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): Post
}