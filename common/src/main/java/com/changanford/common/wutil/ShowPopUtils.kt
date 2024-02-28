package com.changanford.common.wutil

import com.changanford.common.R
import com.changanford.common.widget.pop.FordPaiCirclePop
import com.changanford.common.widget.pop.FordTipsPop
import com.changanford.common.widget.pop.JoinCircleAuPop

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose
 */
object ShowPopUtils {

    //加圈子判断不是车主弹出
    fun showJoinCircleAuPop(content: String) {
        JoinCircleAuPop(content).apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }

    //温馨提示弹窗，没有图
    fun showFordPaiCirclePop(title: String, content: String, bottomContent: String) {
        FordPaiCirclePop().apply {
            setBackground(R.color.m_pop_bg)
            setData(title, content, bottomContent)
            showPopupWindow()
        }
    }

    //温馨提示弹窗，有图
    fun showFordTipsPop(
        content: String,
        bottomContent: String,
        isShowBottomTwo: Boolean
    ) {
        FordTipsPop(content, bottomContent, isShowBottomTwo).apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }
}