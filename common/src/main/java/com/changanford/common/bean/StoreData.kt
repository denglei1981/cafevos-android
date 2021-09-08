package com.changanford.common.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.bean.StoreData
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 17:17
 * @Description: 　保存基础数据
 * *********************************************************************************
 */
@Entity(tableName = "storeData")
data class StoreData(
    @PrimaryKey @ColumnInfo(name = "storeKey") val storeKey: String,
    @ColumnInfo(name = "storeValue") val storeValue: String
)
