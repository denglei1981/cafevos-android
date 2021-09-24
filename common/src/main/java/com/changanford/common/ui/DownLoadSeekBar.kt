package com.changanford.common.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import com.changanford.common.R


/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.widget.view.DownLoadSeekBar
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/6/23 13:12
 * @Description: 　下载进度条
 * *********************************************************************************
 */
class DownLoadSeekBar : AppCompatSeekBar {
    private var mTextPaint //绘制文本的大小
            : TextPaint = TextPaint()
    private var mThumbSize //绘制滑块宽度
            = 0
    private val mSeekBarMin = 0 //滑块开始值
    var fm: Paint.FontMetrics

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarStyle)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        AppCompatSeekBar(context, attrs, defStyleAttr)
        mThumbSize = resources.getDimensionPixelSize(R.dimen.sp_12)
        mTextPaint.setColor(resources.getColor(R.color.appblue))
        mTextPaint.setTextSize(resources.getDimensionPixelSize(R.dimen.sp_12).toFloat())
        mTextPaint.setTypeface(Typeface.DEFAULT)
        mTextPaint.setTextAlign(Paint.Align.CENTER)
        fm = mTextPaint.getFontMetrics()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val unsignedMin: Int = if (mSeekBarMin < 0) mSeekBarMin * -1 else mSeekBarMin
        val progressText = (progress + unsignedMin).toString()
        val bounds = Rect()
        mTextPaint.getTextBounds(progressText, 0, progressText.length, bounds)

        val leftPadding = paddingLeft - thumbOffset
        val rightPadding = paddingRight - thumbOffset
        val width = width - leftPadding - rightPadding
        val progressRatio = progress.toFloat() / max
        val thumbOffset: Float = mThumbSize * (.5f - progressRatio)
        //位置
        val thumbX = progressRatio * width + leftPadding + thumbOffset
        val thumbY: Float = 20 - fm.descent + (fm.descent - fm.ascent) / 2
        canvas!!.drawText("${progressText}%", thumbX, thumbY, mTextPaint)
    }

     fun hintText(){
         mTextPaint.color = resources.getColor(R.color.white)
         invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}