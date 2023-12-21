package com.changanford.circle.widget.titles

import android.content.Context
import android.graphics.Typeface
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * 带颜色渐变和缩放的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
class ScaleTransitionPagerTitleView(context: Context?) : ColorTransitionPagerTitleView(context) {
    var minScale = 0.85f
    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        super.onEnter(index, totalCount, enterPercent, leftToRight) // 实现颜色渐变
        //        setScaleX(mMinScale + enterPercent);
//        setScaleY(mMinScale + enterPercent);
        textSize = 18f
//        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        paint.isFakeBoldText =true
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        super.onLeave(index, totalCount, leavePercent, leftToRight) // 实现颜色渐变
        //        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
//        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
        textSize = 15f
//        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        paint.isFakeBoldText =false
    }
}