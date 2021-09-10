package com.changanford.common.util.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *  文件名：UniUserDatabase
 *  创建者: zcy
 *  创建日期：2021/4/30 9:38
 *  描述: exportSchema 导出Schema文件
 *  修改描述：TODO
 */
@Database(entities = [SysUserInfoBean::class], exportSchema = false, version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUniUserInfoDao(): UserInfoDao

    companion object {
        private var instance: UserDatabase? = null

        fun getUniUserDatabase(context: Context): UserDatabase {
            if (null == instance) {
                synchronized(UserDatabase::class.java) {
                    if (null == instance) {
                        instance = Room.databaseBuilder(
                            context,
                            UserDatabase::class.java,
                            "sys_uni_user.db"
                        ).allowMainThreadQueries() //强制在主线程执行
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}