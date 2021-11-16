package com.changanford.common.util.crash

import android.app.Application
import android.app.Instrumentation
import com.changanford.common.util.crash.IProtect

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.IProtectActivityStartUp
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 5:39 PM
 * @Description: 　
 * *********************************************************************************
 */
class IProtectActivityStartUp : IProtect {
    override fun protect(app: Application) {
        try {
            val aClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = ReflexUtil.getMethod(aClass, "currentActivityThread")
            val activity = currentActivityThread!!.invoke(null)
            val mInstrumentationField = ReflexUtil.getField(aClass, "mInstrumentation")
            mInstrumentationField!!.isAccessible = true
            val mInstrumentation = mInstrumentationField[activity] as Instrumentation
            val evilInstrumentation = InstrumentationProxy(mInstrumentation)
            mInstrumentationField[activity] = evilInstrumentation
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}