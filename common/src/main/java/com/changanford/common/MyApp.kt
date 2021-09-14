package com.changanford.common

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.manger.UserManger
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.isDebug

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.MyApp
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 17:08
 * @Description: 　
 * *********************************************************************************
 */
class MyApp : MultiDexApplication() {
    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        UserManger.getSysUserInfo()?.let {
            MConstant.token = "${it.token}"
        }
        //Arouter Initial
        if (isDebug) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog()     // Print log
            ARouter.openDebug()   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application
    }
}