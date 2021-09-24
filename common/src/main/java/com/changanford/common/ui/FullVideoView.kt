package com.changanford.common.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import com.changanford.common.R

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.widget.view.FullVideoView
 * @Author:　 　
 * @Version : V1.0
 * @Date: 11/27/20 2:30 PM
 * @Description: 　
 * *********************************************************************************
 */
class FullVideoView : SurfaceView, View.OnTouchListener {
    var percent: Float = 1.0f
    var deviceWidth = 0
    var deviceHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.seekBarStyle)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        deviceWidth = resources.displayMetrics.widthPixels
        deviceHeight = resources.displayMetrics.heightPixels
        percent = (deviceWidth / deviceHeight).toFloat();
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return true
    }
}