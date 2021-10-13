package com.changanford.common.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class], version = 11, exportSchema = false)
abstract class PostDatabase : RoomDatabase(){


    abstract fun getPostDao(): PostDao

    companion object {
        private var INSTANCE: PostDatabase? = null

        fun getInstance(context: Context) = INSTANCE
                ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context,
                PostDatabase::class.java,
                "search_post_table"
            ).fallbackToDestructiveMigration().build().also {
                INSTANCE = it
            }
        }
    }




}