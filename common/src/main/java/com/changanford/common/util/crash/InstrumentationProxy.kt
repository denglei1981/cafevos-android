package com.changanford.common.util.crash

import android.app.Activity
import android.app.Instrumentation
import android.os.Bundle
import android.os.PersistableBundle

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.InstrumentationProxy
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 5:57 PM
 * @Description: 　
 * *********************************************************************************
 */
class InstrumentationProxy(base: Instrumentation) : Instrumentation() {
    init {
        ReflexUtil.copyTo(base, this)
    }

    fun onException(e: Exception, activity: Activity?) {
        e.printStackTrace()
        activity?.finish()
    }

    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        try {
            super.callActivityOnCreate(activity, icicle)
        } catch (e: Exception) {
            onException(e, activity)
        }

    }

    override fun callActivityOnCreate(
        activity: Activity?,
        icicle: Bundle?,
        persistentState: PersistableBundle?
    ) {
        try {
            super.callActivityOnCreate(activity, icicle, persistentState)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

    override fun callActivityOnResume(activity: Activity?) {
        try {
            super.callActivityOnResume(activity)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

    override fun callActivityOnPause(activity: Activity?) {
        try {
            super.callActivityOnPause(activity)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

    override fun callActivityOnDestroy(activity: Activity?) {
        try {
            super.callActivityOnDestroy(activity)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

    override fun callActivityOnStop(activity: Activity?) {
        try {
            super.callActivityOnStop(activity)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

    override fun callActivityOnStart(activity: Activity?) {
        try {
            super.callActivityOnStart(activity)
        } catch (e: Exception) {
            onException(e, activity)
        }
    }

}