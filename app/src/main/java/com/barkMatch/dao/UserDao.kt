package com.barkMatch.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barkMatch.models.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE id=:id")
    fun getUserById(id: String): LiveData<User>

    @Query("SELECT * FROM User")
    fun getAll(): LiveData<MutableList<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: User)
}