package com.changanford.common.wutil

import com.changanford.common.R
import com.changanford.common.widget.pop.FordPaiCirclePop
import com.changanford.common.widget.pop.FordTipsPop
import com.changanford.common.widget.pop.JoinCircleAuPop
import com.changanford.common.widget.pop.LoadingIosPop
import com.qw.soul.permission.SoulPermission

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
        isShowBottomTwo: Boolean,
        cancelContent: String? = null,
        sureListener: (() -> Unit?)? = null,
        cancelListener: (() -> Unit?)? = null,
        title: String? = null
    ) {
        FordTipsPop(
            content,
            bottomContent,
            isShowBottomTwo,
            cancelContent,
            sureListener,
            cancelListener,
            title
        ).apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }

    fun showIosPopLoading(): LoadingIosPop {
        return LoadingIosPop().apply {
            setOutSideDismiss(false)
            setBackPressEnable(false)
            setBackground(R.color.m_pop_bg2)
            showPopupWindow()
        }
    }

    fun showNoAddressLocationPop() {
        showFordTipsPop(
            "您已禁止了定位权限，请到设置中心去打开",
            "确认",
            true,
            "取消",
            sureListener = {
                SoulPermission.getInstance().goApplicationSettings()
            },
            title = "提示"
        )
    }
}