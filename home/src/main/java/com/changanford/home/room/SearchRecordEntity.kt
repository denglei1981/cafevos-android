package com.changanford.home.room

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @Author: hpb
 * @Date: 2020/5/22
 * @Des: 首页搜索记录列表
 */
@Entity(tableName = "search_record_table")
data class SearchRecordEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "keyword") val keyword: String
)

@Dao
interface SearchRecordDao {

    @Query("SELECT * FROM search_record_table")
    fun findAll(): LiveData<List<SearchRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchRecordEntity)

    @Query("DELETE  from search_record_table")
    suspend fun clearAll()

    @Query("DELETE  from search_record_table where keyword = :keyword")
    suspend fun delete(keyword: String)
}

