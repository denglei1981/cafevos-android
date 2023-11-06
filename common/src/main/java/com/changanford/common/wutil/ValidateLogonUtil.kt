package com.changanford.common.wutil

import com.changanford.common.R
import com.changanford.common.bean.CmcStatePhoneBean
import com.changanford.common.widget.pop.ValidateLogonPop

/**
 *Author lcw
 *Time on 2023/11/6
 *Purpose
 */
object ValidateLogonUtil {

    fun showValidateLogon(bean: CmcStatePhoneBean){
        ValidateLogonPop(bean).apply {
            setBackground(R.color.m_pop_bg)
            showPopupWindow()
        }
    }

}