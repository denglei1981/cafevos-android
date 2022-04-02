package com.changanford.common.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

class CustomImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top+4
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

class CustomImageSpanV2(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top+2
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}