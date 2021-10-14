package com.changanford.home.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
/**
 * @Author: hpb
 * @Date: 2020/5/22
 * @Des:首页搜索记录列表
 */
@Database(entities = [SearchRecordEntity::class], version = 1, exportSchema = false)
abstract class SearchRecordDatabase : RoomDatabase() {

    abstract fun getSearchRecordDao(): SearchRecordDao

    companion object {
        private var INSTANCE: SearchRecordDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context,
                SearchRecordDatabase::class.java,
                "cafevos_home_db"
            ).fallbackToDestructiveMigration().build().also {
                INSTANCE = it
            }
        }
    }

}