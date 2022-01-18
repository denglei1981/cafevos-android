package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.compose.DeleteCarScreen
import com.changanford.my.databinding.UiCarDeleteBinding
import com.changanford.my.viewmodel.CarAuthViewModel

/**
 *  文件名：CarDeleteUI
 *  创建者: zcy
 *  创建日期：2022/1/13 13:49
 *  描述: TODO
 *  修改描述：TODO
 */


@Route(path = ARouterMyPath.CarDeleteUI)
class CarDeleteUI : BaseMineUI<UiCarDeleteBinding, CarAuthViewModel>() {

    override fun initView() {
        binding.deleteCarToolbar.toolbarTitle.text = "删除车辆"
        binding.deleteCarCompose.setContent {

            DeleteCarScreen(viewModel, onGetSmsClick = {
                if (it.isNullOrEmpty()) {
                    showToast("请输入手机号")
                    return@DeleteCarScreen
                }
                viewModel.smsCacSmsCode(it)
            }, onSubmitClick = { mobile, sms ->
                showToast("提交$mobile  $sms")
            })
        }

        viewModel.smsSuccess.observe(this, androidx.lifecycle.Observer {
            viewModel.smsCountDownTimer()
            showToast("验证码获取成功")
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.subscribe?.let {
            it.dispose()
        }
    }
}