package com.changanford.common.buried

import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.net.decryResult
import com.google.gson.Gson

/**
 * @Author: hpb
 * @Date: 2020/4/24
 * @Des: 基础数据
 */
open class BaseBean<DATA>(
    val code: Int,
    val data: DATA,
    val encr: Boolean,
    val msg: String,
    val msgId: String,
    val timestamp: Long
) {
    fun <T> getData(key: String, type: Class<T>): T {
        var json = if (encr) {
            decryResult(data as String, key)
        } else {
            data as String
        }
        LogUtil.d("result:", json)
        return Gson().fromJson(json, type)
    }

    override fun toString(): String {
        return "BaseBean(code=$code, data=$data, encr=$encr, msg='$msg', msgId='$msgId', timestamp=$timestamp)"
    }
}