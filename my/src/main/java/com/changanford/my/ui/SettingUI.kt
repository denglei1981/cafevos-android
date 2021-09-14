package com.changanford.my.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.USER_LOGIN_STATUS
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiSeetingBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：SettingUI
 *  创建者: zcy
 *  创建日期：2021/9/9 13:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineSettingUI)
class SettingUI : BaseMineUI<UiSeetingBinding, SignViewModel>() {

    override fun initView() {
        binding.btnLoginOut.setOnClickListener {
            viewModel.loginOut()
        }

        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                finish()
            })

    }

    override fun initData() {

    }
}