package com.changanford.common.util

import android.content.Context
import android.os.Build
import android.widget.TextView
import com.changanford.common.R

object TabLayoutFontStyle {
    fun changeFont(context: Context, tv: TextView): TextView {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var typeface = context.resources.getFont(R.font.fontface)
            tv.typeface = typeface
        }
        return tv
    }
}