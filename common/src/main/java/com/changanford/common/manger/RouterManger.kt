package com.changanford.common.manger

import android.os.Bundle
import android.os.Parcelable
import com.changanford.common.utilext.logD
import java.io.Serializable

/**
 *  文件名：RouterManger
 *  创建者: zcy
 *  创建日期：2021/9/9 10:17
 *  描述: TODO
 *  修改描述：TODO
 */
object RouterManger {

    const val KEY_TO_ID = "sys:key_to_id"

    const val KEY_TO_ITEM = "sys:key_to_item"

    const val KEY_TO_OBJ = "sys:key_to_obj"


    /**
     * 存储传递数据
     */
    private var paramMap: HashMap<String, Any> = HashMap()

    /**
     * 默认不需要登录
     */
    private var isNeedLogin: Boolean? = false

    /**
     * 传参数
     */
    @JvmOverloads
    fun param(key: String, value: Any): RouterManger {
        if (key.isNotEmpty()) {
            paramMap[key] = value
        }
        return this
    }

    /**
     * 是否需要登录
     */
    @JvmOverloads
    fun needLogin(isNeedLogin: Boolean): RouterManger {
        RouterManger.isNeedLogin = isNeedLogin
        return this
    }

    /**
     * router链接
     */
    @JvmOverloads
    fun startARouter(url: String) {
        com.changanford.common.router.startARouter(url, setBundle(), isNeedLogin)
    }

    /**
     * 带Bundle的跳转路由
     */
    @JvmOverloads
    fun startARouter(url: String, bundle: Bundle) {
        com.changanford.common.router.startARouter(url, bundle, isNeedLogin)
    }

    /**
     * 带一个参数跳转
     */
    @JvmOverloads
    private fun setBundle(): Bundle {
        var bundle = Bundle()
        paramMap.forEach {
            var key: String = it.key
            var value: Any = it.value
            "${it.key}-->>${it.value}".logD()
            if (it.key.isNotEmpty()) {
                when (value) {
                    is Int -> {
                        bundle.putInt(key, value)
                    }
                    is Boolean -> {
                        bundle.putBoolean(key, value)
                    }
                    is Long -> {
                        bundle.putLong(key, value)
                    }
                    is String -> {
                        bundle.putString(key, value.toString())
                    }
                    is Serializable -> {
                        bundle.putSerializable(key, value)
                    }
                    is Parcelable -> {
                        bundle.putParcelable(key, value)
                    }
                    else -> {
                        "不支持类型".logD()
                    }
                }
            }
        }
        //只有一个，多个已最后一个为准
        paramMap.clear()
        return bundle
    }

}