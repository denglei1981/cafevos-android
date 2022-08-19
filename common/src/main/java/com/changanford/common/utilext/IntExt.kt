package com.changanford.common.utilext

import android.content.Context
import com.changanford.common.MyApp


fun Int.toPx() = dpToPx(MyApp.mContext, this.toFloat())

fun Int.toIntPx() = dpToPx(MyApp.mContext, this.toFloat()).toInt()

fun Int.toDp() = pxToDp(MyApp.mContext, this.toFloat())
fun Int.toIntDp() = pxToDp(MyApp.mContext, this.toFloat()).toInt()

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun pxToDp(context: Context, px: Float): Float {
    return px / context.resources.displayMetrics.density
}