package com.changanford.common.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.Date


/**
 *Author lcw
 *Time on 2023/10/26
 *Purpose
 */
object ChangeIconUtils {

    @SuppressLint("SimpleDateFormat")
    fun isOpenYearIcon(): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val time = sdf.format(Date()).toInt()
        if (time in 20231027..20231027) {
            return true
        }
        return false
    }

    /**
     * 设置默认的别名为启动入口
     */
    fun setDefaultAlias(activity: Activity) {
        activity.apply {
            val packageManager: PackageManager = packageManager
            val name1 = ComponentName(this, "com.changanford.evos.DefaultAliasActivity")
            packageManager.setComponentEnabledSetting(
                name1,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            val name2 = ComponentName(this, "com.changanford.evos.Alias1Activity")
            packageManager.setComponentEnabledSetting(
                name2,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
//            restart(packageManager, activity)
        }

    }

    /**
     * 设置2周年图标为启动入口
     */
    fun setAlias1(activity: Activity) {
        activity.apply {
            val packageManager: PackageManager = packageManager
            val name1 = ComponentName(this, "com.changanford.evos.DefaultAliasActivity")
            packageManager.setComponentEnabledSetting(
                name1,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            val name2 = ComponentName(this, "com.changanford.evos.Alias1Activity")
            packageManager.setComponentEnabledSetting(
                name2,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
//            restart(packageManager, activity)
        }
    }

    private fun restart(pm: PackageManager, activity: Activity) {
        activity.apply {
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                if (resolveInfo.activityInfo != null) {
                    am.killBackgroundProcesses(resolveInfo.activityInfo.packageName)
                }
            }
        }

    }

}