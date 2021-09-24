package com.changanford.common.utilext

import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes

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