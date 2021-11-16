package com.changanford.common.util.crash

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.changanford.common.basic.BaseApplication
import com.luck.picture.lib.tools.ToastUtils

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.IProtectApp
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 4:13 PM
 * @Description: 　
 * *********************************************************************************
 */
class IProtectApp : IProtect {
    override fun protect(app: Application) {
        ActivityStackManager.init(app)
        protectApp(null)
    }

    private fun protectApp(preException: CrashException?) {
        Handler(Looper.getMainLooper()).post {
            try {
                Looper.loop()
            } catch (e: Exception) {
                CrashProtect().writeLog(e)
                var curException = CrashException(e)
                if (curException.analysis(preException)) {
                    ToastUtils.s(BaseApplication.INSTANT, "应用发生异常")
                    ActivityStackManager.exceptionBirthActivity(e)?.finish()
                    protectApp(curException)
                    return@post
                }
                throw e
            }
        }
    }

    internal class CrashException(e: Throwable?) : java.lang.Exception(e) {
        //哪些需要拦截
        val createTime = System.currentTimeMillis()
        fun analysis(pre: CrashException?): Boolean {
            return when {
                isSystemCrash() -> false
                pre == null -> true
                else -> createTime - pre.createTime >= 100
            }
        }

        fun isSystemCrash(): Boolean {
            if (cause?.cause?.stackTrace?.size ?: 0 > 0) {
                val first = cause?.cause?.stackTrace?.first() ?: return false
                val classLoader = Class.forName(first.className).classLoader
                return classLoader == null || classLoader == Exception::class.java.classLoader
            } else {
                return false
            }
        }
    }

}