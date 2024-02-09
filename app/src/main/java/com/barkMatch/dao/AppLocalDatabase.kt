package com.barkMatch.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.barkMatch.base.MyApplication
import com.barkMatch.models.Post
import com.barkMatch.models.User
import com.barkMatch.utils.DateConverter

@Database(entities = [Post::class, User::class], version = 6)
@TypeConverters(DateConverter::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .build()
    }
}