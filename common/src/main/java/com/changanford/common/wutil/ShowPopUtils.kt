package com.changanford.common.wutil

import com.changanford.common.R
import com.changanford.common.widget.pop.JoinCircleAuPop

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose
 */
object ShowPopUtils {
    fun showJoinCircleAuPop(content:String){
        JoinCircleAuPop(content).apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }
}