package com.changanford.common.util.crash

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.ActivityStackManager
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 5:27 PM
 * @Description: 　
 * *********************************************************************************
 */
object ActivityStackManager {
    private val activityList = LinkedList<Activity>()
    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.addFirst(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })
    }

    fun exceptionBirthActivity(exception: Exception): Activity? {
        if (activityList.isNullOrEmpty())
            return null
        var className = activityList.first.javaClass.name
        if (exception.cause?.stackTrace?.first()?.className?.contains(className) == true)
            return activityList.first
        return null
    }
}