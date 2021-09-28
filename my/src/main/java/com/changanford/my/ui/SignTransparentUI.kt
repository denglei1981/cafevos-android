package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.DaySignBean
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.SignTransparentUiBinding
import com.changanford.my.widget.TodaySignPop
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changan.my.ui.activity.SignTransparentUI
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/27 10:26
 * @Description: 　签到成功页面，透明
 * *********************************************************************************
 */
@Route(path = ARouterMyPath.SignTransparentUI)
class SignTransparentUI : BaseMineUI<SignTransparentUiBinding, EmptyViewModel>() {


    override fun initView() {
        var todaySignPop = TodaySignPop(this)
        var bundle = intent.extras;
        var daySignBean = Gson().fromJson(
            bundle?.getString("signInfo"),
            DaySignBean::class.java
        )
        todaySignPop.initDayBean(daySignBean)
        todaySignPop.showPopupWindow()
        todaySignPop.onDismissListener = object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
                finish()
            }

        }
    }

    override fun initData() {
    }
}