package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.R


/**
 * @Author: lw
 * @Date: 2020/11/18
 * @Des: post 发帖。
 */
class SelectPostDialog(context: Context, private val listener: CheckedView) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.select_post_dialog

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_post_long).setOnClickListener {
            listener.postLong()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_post_pics).setOnClickListener {
            listener.postPics()
            dismiss()
        }

        findViewById<AppCompatTextView>(R.id.tv_video).setOnClickListener {
            listener.postVideo()
            dismiss()
        }
    }


    interface CheckedView {
        fun postLong()
        fun postPics()
        fun postVideo()
    }
}