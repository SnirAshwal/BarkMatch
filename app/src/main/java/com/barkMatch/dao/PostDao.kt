package com.barkMatch.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barkMatch.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post ORDER BY creationDate DESC")
    fun getAll(): LiveData<MutableList<Post>>

    @Query("SELECT * FROM Post WHERE ownerId = :id ORDER BY creationDate DESC")
    fun getAllByUserId(id: String): LiveData<MutableList<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg posts: Post)

    @Query(
        """
        UPDATE Post
        SET breedName = :breedName, breedId = :breedId, description = :description, image = CASE WHEN :image IS NULL THEN image ELSE :image END
        WHERE id = :postId
    """
    )
    fun update(
        postId: String,
        breedName: String,
        breedId: Int,
        description: String,
        image: String?
    )

    @Query("DELETE FROM Post WHERE id = :postId")
    fun delete(postId: String)

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): LiveData<Post>
}