package com.changanford.common.wutil

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams

/**
 * @Author : wenke
 * @Time : 2021/9/13 0013
 * @Description : ScreenUtils
 */
object ScreenUtils {
    private var screenWidth = 0

    /**
     * 获取屏幕高度(px)
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取屏幕宽度(px)
     */
    fun getScreenWidth(context: Context): Int {
        if (screenWidth == 0) {
            screenWidth = context.resources.displayMetrics.widthPixels
        }
        return screenWidth
    }
    fun getScreenWidthDp(context: Context): Int {
        return px2dp(context,context.resources.displayMetrics.widthPixels.toFloat())
    }
    fun dp2px(context: Context, dp: Float): Int {
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ) + 0.5f).toInt()
    }
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 计算比例
     */
    fun getCalculatePercentage(context: Context, w: Int, h: Int): Int {
        return getScreenWidth(context) * h / w
    }

    fun setMargin(v: View, l: Int=0, t: Int=0, r: Int=0, b: Int=0) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }
}