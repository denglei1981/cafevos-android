package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast
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
        val vin = intent.getStringExtra("vin")
        binding.deleteCarToolbar.toolbarTitle.text = "删除车辆"

        viewModel.carAuthQY { qy->
            val data = qy.data

            binding.deleteCarCompose.setContent {

                DeleteCarScreen(viewModel,data?.removeCarNotice,data?.contactCustomerService, onGetSmsClick = {
                    if (it.isNullOrEmpty()) {
                        showToast("请输入手机号")
                        return@DeleteCarScreen
                    }
                    viewModel.smsCacSmsCode(it)
                }, onSubmitClick = { mobile, sms ->
//                    showToast("提交$mobile  $sms")
                    BuriedUtil.instant?.carDelete(mobile)
                    if (vin != null) {
                        viewModel.deleteCar(vin, mobile, sms) {
                            it.onSuccess {
                                "删除成功".toast()
                                LiveDataBus.get().with(LiveDataBusKey.REMOVE_CAR).postValue(true)
                                this.finish()
                            }.onFailure { fs ->
                                fs?.toast()
                            }
                        }
                    }
                })
            }
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