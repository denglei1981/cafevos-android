package com.changanford.common.util.room

import androidx.room.*
import androidx.room.Dao
import com.changanford.common.bean.StoreData

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.room.Dao
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 17:23
 * @Description: 　
 * *********************************************************************************
 */

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveData(data: StoreData)

    @Query("select * from storeData where storeKey = :key")
    suspend fun getData(key: String): StoreData?

    @Query("delete from storeData where storeKey = :key")
    suspend fun delete(key: String)

    @Delete
    suspend fun delete(data: StoreData)

    @Query("delete from storeData")
    suspend fun deleteAll()
}