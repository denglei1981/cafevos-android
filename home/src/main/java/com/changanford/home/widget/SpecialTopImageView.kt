package com.changanford.home.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 *Author lcw
 *Time on 2023/12/11
 *Purpose
 */
class SpecialTopImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private val path = Path()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val w = width
        val h = height

        path.reset()
        path.moveTo(0f, 0f)
        path.lineTo(w.toFloat(), 0f)
        path.lineTo(w.toFloat(), h.toFloat())
        path.arcTo(0f, h.toFloat() - 300, w.toFloat(), h.toFloat(), 0f, 180f, false)
        path.lineTo(0f, 0f)
        path.close()

        canvas.clipPath(path)

        super.onDraw(canvas)
    }
}