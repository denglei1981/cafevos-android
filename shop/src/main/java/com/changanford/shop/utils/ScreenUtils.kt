package com.changanford.shop.utils

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

    fun dp2px(context: Context, dp: Float): Int {
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ) + 0.5f).toInt()
    }

    /**
     * 计算比例
     */
    fun getCalculatePercentage(context: Context, w: Int, h: Int): Int {
        return getScreenWidth(context) * h / w
    }

    fun setMargin(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }
}