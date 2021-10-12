package com.changanford.home.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 *  制定滑动到的位置。
 * */
class TopSmoothScroller (context: Context) : LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START//将返回值设置为SNAP_TO_START
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START//将返回值设置为SNAP_TO_START
    }
}
