package com.changanford.common.utilext

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.changanford.common.R

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

fun TextView.setDrawableLeft(@DrawableRes res: Int) {
    val drawableLeft = resources.getDrawable(res, null)
    drawableLeft.setBounds(
        0,
        0,
        drawableLeft.intrinsicWidth, drawableLeft.intrinsicHeight,
    )
    this.setCompoundDrawables(drawableLeft, null, null, null)
}

fun TextView.setDrawableRight(@DrawableRes res: Int) {
    val drawableRight = resources.getDrawable(res, null)
    drawableRight.setBounds(
        0,
        0,
        drawableRight.intrinsicWidth, drawableRight.intrinsicHeight,
    )
    this.setCompoundDrawables(null, null, drawableRight, null)
}

fun TextView.setDrawableNull() {
    this.setCompoundDrawables(null, null, null, null)
}

fun TextView.setDrawableTop(context: Context, @DrawableRes res: Int) {
    var drawableTop = ContextCompat.getDrawable(context, res)
    this.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
}

/**
 * 认证页面，上传图片切换样式
 */
fun AppCompatTextView.styleAuthCheck(isCheck: Boolean) {
    this.setTextColor(Color.parseColor(if (isCheck) "#333333" else "#666666"))
    this.typeface = Typeface.defaultFromStyle(if (isCheck) Typeface.BOLD else Typeface.NORMAL)
}

/**
 * 员工认证页面，已认证提示
 */

fun AppCompatTextView.setFordAuthSuccess() {
    setCompoundDrawablesWithIntrinsicBounds(
        R.mipmap.ic_ford_auth_status_success, 0, 0, 0
    )
    visibility = View.VISIBLE
    text = "已认证"
    setTextColor(Color.parseColor("#1B3B89"))
}


fun AppCompatTextView.setFordAuthFail() {
    setCompoundDrawablesWithIntrinsicBounds(
        R.mipmap.ic_ford_auth_fail, 0, 0, 0
    )
    visibility = View.VISIBLE
    text = "认证失败"
    setTextColor(Color.parseColor("#F21C44"))
}