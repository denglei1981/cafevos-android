package com.changanford.common.utilext

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

fun TextView.setDrawableLeft(@DrawableRes res: Int, @DimenRes size: Int) {
    var drawableLeft = resources.getDrawable(res, null)
    drawableLeft.setBounds(
        0,
        0,
        resources.getDimension(size).toInt(),
        resources.getDimension(size).toInt()
    )
    this.setCompoundDrawables(drawableLeft, null, null, null)
}
fun TextView.setDrawableTop(context:Context,@DrawableRes res: Int) {
    var drawableTop =  ContextCompat.getDrawable(context,res)
    this.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
}
/**
 * 认证页面，上传图片切换样式
 */
fun AppCompatTextView.styleAuthCheck(isCheck: Boolean) {
    this.setTextColor(Color.parseColor(if (isCheck) "#333333" else "#666666"))
    this.typeface = Typeface.defaultFromStyle(if (isCheck) Typeface.BOLD else Typeface.NORMAL)
}