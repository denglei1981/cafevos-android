package com.changanford.common

import android.content.Context
import com.changanford.common.basic.BaseApplication

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.MyApp
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 17:08
 * @Description: 　
 * *********************************************************************************
 */
class MyApp : BaseApplication() {
    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}