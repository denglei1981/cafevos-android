package com.changanford.common.widget.title

import android.content.Context
import android.graphics.Typeface
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * 带颜色渐变和缩放的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
class CarScaleTransitionPagerTitleView(context: Context?) : ColorTransitionPagerTitleView(context) {
    private var mMinScale = 0.9f

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        super.onEnter(index, totalCount, enterPercent, leftToRight) // 实现颜色渐变
        scaleX = mMinScale + (1.0f - mMinScale) * enterPercent
        scaleY = mMinScale + (1.0f - mMinScale) * enterPercent
//        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        super.onLeave(index, totalCount, leavePercent, leftToRight) // 实现颜色渐变
        scaleX = 1.0f + (mMinScale - 1.0f) * leavePercent
        scaleY = 1.0f + (mMinScale - 1.0f) * leavePercent
//        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }


    fun getMinScale(): Float {
        return mMinScale
    }

    fun setMinScale(minScale: Float) {
        mMinScale = minScale
    }

}