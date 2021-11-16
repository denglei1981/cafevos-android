package com.changanford.common.util.crash

import android.app.Application
import android.os.Looper
import com.changanford.common.util.crash.IProtect

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.IProtectThread
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/10/21 11:39 AM
 * @Description: 　
 * *********************************************************************************
 */
class IProtectThread : IProtect {
    override fun protect(app: Application) {
        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            UncaughtExceptionHandlerProxy(
                defaultUncaughtExceptionHandler
            )
        )
    }

    class UncaughtExceptionHandlerProxy(val handlerProxy: Thread.UncaughtExceptionHandler) :
        Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            if (t == Looper.getMainLooper().thread || IProtectApp.CrashException(e)
                    .isSystemCrash()
            ) {
                handlerProxy.uncaughtException(t, e)
            }else{
                CrashProtect().writeLog(e)
            }

        }
    }

}