package com.changanford.common.widget.smart

import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.listener.OnMultiListener

/**
 * @Author: hpb
 * @Date: 2020/5/8
 * @Des:
 */
abstract class SmartTwoLevelHelper : OnMultiListener {
    override fun onFooterMoving(
        footer: RefreshFooter?,
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        footerHeight: Int,
        maxDragHeight: Int
    ) {
    }

    override fun onHeaderStartAnimator(
        header: RefreshHeader?,
        headerHeight: Int,
        maxDragHeight: Int
    ) {
    }

    override fun onFooterReleased(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
    }

    override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
    }

    override fun onFooterStartAnimator(
        footer: RefreshFooter?,
        footerHeight: Int,
        maxDragHeight: Int
    ) {
    }

    override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
    }

    override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
    }
}