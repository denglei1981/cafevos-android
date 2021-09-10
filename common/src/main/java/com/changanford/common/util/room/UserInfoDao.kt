package com.changanford.common.util.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

/**
 *  文件名：UniUserInfoDao
 *  创建者: zcy
 *  创建日期：2020/5/14 17:59
 *  描述: TODO
 *  修改描述：TODO
 */

@Dao
interface UserInfoDao {

    //有就更新，无插入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: SysUserInfoBean)

    @Query("Delete from table_sys_uni_user Where uni_user_id = :userId")
    fun delete(userId: String): Int

    @Query("Delete from table_sys_uni_user")
    fun deleteAll()

    //按userId去更新mobile
    @Query("Update table_sys_uni_user set user_mobile = :mobile Where uni_user_id = :userId")
    fun updateMobile(userId: String, mobile: String): Int

    //按userId去更新mobile
    @Query("Update table_sys_uni_user set user_token = :token Where uni_user_id = :userId")
    fun updateToken(userId: String, token: String): Int

    //按userId去更新integral
    @Query("Update table_sys_uni_user set user_integral = :integral Where uni_user_id = :userId")
    fun updateIntegral(userId: String, integral: String): Int

    @Query("Select * from table_sys_uni_user Where uni_user_id = :userId")
    fun getUser(userId: String): LiveData<SysUserInfoBean>

    @Query("SELECT * FROM table_sys_uni_user ORDER BY uni_user_id DESC")
    fun getUserAll(): LiveData<List<SysUserInfoBean>>

    @Query("SELECT * FROM table_sys_uni_user LIMIT 1")
    fun getUser(): LiveData<SysUserInfoBean>

}