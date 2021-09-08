package com.changanford.common.util.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.changanford.common.MyApp
import com.changanford.common.bean.StoreData

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.room.RoomDataBase
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 17:16
 * @Description: 数据库
 * *********************************************************************************
 */

@Database(entities = [StoreData::class], version = 1)
abstract class OSDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        val db: OSDataBase by lazy {
                Room
                    .databaseBuilder(
                        MyApp.mContext,
                        OSDataBase::class.java,
                        "store_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}

class DbRepository(private val dao: Dao) {
    suspend fun getData(key: String) =
        dao.getData(key)

    suspend fun saveData(key: String,value:String) {
        val data = StoreData(key,value)
        dao.saveData(data)
    }

}

/**
 * singleton
 */
object Db{
    val myDb by lazy {
        DbRepository(OSDataBase.db.getDao())
    }
}
