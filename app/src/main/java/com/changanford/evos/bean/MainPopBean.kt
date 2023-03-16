package com.changanford.evos.bean

import com.changanford.common.bean.BizCodeBean
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.NewEstOneBean
import com.changanford.common.bean.UpdateInfo
import com.changanford.home.bean.FBBean
import com.changanford.home.bean.NewEstRuleBean

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose
 */
data class MainPopBean(
    val updateInfo: UpdateInfo?,
    val fbBean: FBBean?,
    val coupons: MutableList<CouponsItemBean>?,
    val newEstOneBean: NewEstOneBean?,
    val bizCodeBean: BizCodeBean?,
    val popRuleBean: NewEstRuleBean?
)