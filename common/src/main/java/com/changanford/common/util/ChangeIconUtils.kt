package com.changanford.common.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
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
        return time in 20240205..20240229
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
//            killApp(activity)
        }

    }

    /**
     * 设置新图标为启动入口
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
//            killApp(activity)
        }
    }

    private fun killApp( activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN;
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
       activity. startActivity(intent);

    }

}