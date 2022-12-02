package com.changanford.common.util

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.view.Window

fun BlackWhiteMode(window:Window) {
    var paint = Paint()
    var colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE,paint)

}