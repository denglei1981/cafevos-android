package com.changanford.common.util.room

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


/**
 *  文件名：UniUserDatabase
 *  创建者: zcy
 *  创建日期：2021/4/30 9:38
 *  描述: exportSchema 导出Schema文件
 *  修改描述：TODO
 */
@Database(entities = [SysUserInfoBean::class], exportSchema = false, version = 2)
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
                        )
                            .addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries() //强制在主线程执行
                            .build()
                    }
                }
            }
            return instance!!
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(@NonNull database: SupportSQLiteDatabase) {
                //使用下面分开的形式,可以正确执行
                database.execSQL("ALTER TABLE table_sys_uni_user " + " ADD COLUMN mine_bind_mobile_jump_data INTEGER " + " NOT NULL DEFAULT 0")
            }
        }
    }
}