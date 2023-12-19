package com.changanford.common.util.ext

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.changanford.common.R

/**
 *Author lcw
 *Time on 2023/12/18
 *Purpose
 */
fun ImageView.setAppColor() {
    this.setColorFilter(ContextCompat.getColor(this.context, R.color.color_1700F4))
}