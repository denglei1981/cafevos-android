package com.changanford.circle.widget

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView

/**
 * @Author: hpb
 * @Date: 2020/6/1
 * @Des: ClickSpan点击事件
 */
class MyLinkMovementMethod private constructor() : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        val b = super.onTouchEvent(widget, buffer, event)
        if (!b && event?.action == MotionEvent.ACTION_UP) {
            val parent = widget?.parent
            if (parent is ViewGroup) {
                parent.performClick()
            }
        }
        return b
    }

    companion object {
        private var INSTANCE: MyLinkMovementMethod? = null
        fun get() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MyLinkMovementMethod().also {
                INSTANCE = it
            }
        }
    }

}