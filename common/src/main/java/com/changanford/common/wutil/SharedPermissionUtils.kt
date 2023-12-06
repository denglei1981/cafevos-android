package com.changanford.common.wutil

import android.content.Context
import android.content.SharedPreferences


object SharedPermissionUtils {
    const val FILE_NAME = "JsLibrary"
    lateinit var sp: SharedPreferences

    fun init(context: Context){
        sp = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }
    /**
     * 添加boolean值
     */
    fun putBoolean(key: String,value: Boolean){
        sp.edit().putBoolean(key,value).apply()
    }
    /**
     * 获取boolean值
     */
    fun getBoolean(key: String,default: Boolean): Boolean{
        return sp.getBoolean(key,default)
    }

    /**
     * 判断是否存在
     */
    fun contains(key: String): Boolean{
        return sp.contains(key)
    }
}