package com.changanford.common.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * @author: niubobo
 * @date: 2024/12/6
 * @description：
 */
class SignatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val path = Path()

    // 虚线边框的画笔
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // 设置虚线效果
    }

    private val cornerRadius = 30f // 圆角半径

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制虚线圆角边框
        drawDottedBorder(canvas)

        // 绘制签名路径
        canvas.drawPath(path, paint)
    }

    private fun drawDottedBorder(canvas: Canvas) {
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                notifySignatureComplete()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    private fun notifySignatureComplete() {
        listener?.onSignatureComplete(path.isEmpty.not())
    }

    fun clear() {
        path.reset()
        invalidate()
        listener?.onSignatureComplete(false)
    }

    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    // 定义接口和设置监听器的代码保持不变
    interface OnSignatureCompleteListener {
        fun onSignatureComplete(isSigned: Boolean)
    }

    private var listener: OnSignatureCompleteListener? = null

    fun setOnSignatureCompleteListener(listener: OnSignatureCompleteListener) {
        this.listener = listener
    }
}