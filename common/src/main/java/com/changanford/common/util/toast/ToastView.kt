package com.changanford.common.util.toast

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.changanford.common.R

/**
 * @Author: lcw
 * @Date: 2020/8/4
 * @Des:
 */
class ToastView : FrameLayout {
    private var toastText: TextView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        addView(View.inflate(context, R.layout.toast_view, null))
        toastText = findViewById(R.id.toastText)
    }

    fun setText(text: String) {
        toastText!!.text = text
    }

    fun setTextSize(size: Int) {
        toastText!!.textSize = size.toFloat()
    }

    fun setTextColor(color: Int) {
        toastText!!.setTextColor(color)
    }
}