package com.changanford.common.util

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.annotation.NonNull

fun setCustomDensity(
    @NonNull activity: Activity,
    @NonNull application: Application
) {
    /**
     * 适配：修改设备密度
     */
    val sNoncomPatDensity: Float
    var sNoncomPatScaledDensity: Float
    val appDisplayMetrics: DisplayMetrics = application.resources.displayMetrics
    sNoncomPatDensity = appDisplayMetrics.density
    sNoncomPatScaledDensity = appDisplayMetrics.scaledDensity
    // 防止系统切换后不起作用
    application.registerComponentCallbacks(object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) {
            if (newConfig.fontScale > 0) {
                sNoncomPatScaledDensity =
                    application.resources.displayMetrics.scaledDensity
            }
        }

        override fun onLowMemory() {}
    })
    val targetDensity = appDisplayMetrics.widthPixels / 360.toFloat()
    // 防止字体变小
    val targetScaleDensity: Float =
        targetDensity * (sNoncomPatScaledDensity / sNoncomPatDensity)
    val targetDensityDpi = (160 * targetDensity).toInt()
    appDisplayMetrics.density = targetDensity
    appDisplayMetrics.scaledDensity = targetScaleDensity
    appDisplayMetrics.densityDpi = targetDensityDpi
    val activityDisplayMetrics = activity.resources.displayMetrics
    activityDisplayMetrics.density = targetDensity
    activityDisplayMetrics.scaledDensity = targetScaleDensity
    activityDisplayMetrics.densityDpi = targetDensityDpi
}