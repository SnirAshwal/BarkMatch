package com.BarkMatch.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.BarkMatch.base.MyApplication
import com.BarkMatch.models.Post


@Database(entities = [Post::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
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
            .fallbackToDestructiveMigration() // TODO: change to migration before production
            .build()
    }
}