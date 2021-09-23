package com.changanford.common.basic

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.util.MConstant


abstract class BaseApplication :MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        INSTANT = this
        //Arouter Initial
        if (MConstant.isDebug) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application
    }
    override fun onTerminate() {
        super.onTerminate()
        //清理ARouter注册表
        ARouter.getInstance().destroy()
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    companion object {
        lateinit var INSTANT: Application
        lateinit var curActivity: Activity
    }
}