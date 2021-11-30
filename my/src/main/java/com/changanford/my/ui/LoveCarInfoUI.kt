package com.changanford.my.ui

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiLoveCarInfoBinding
import com.changanford.my.viewmodel.CarAuthViewModel

/**
 *  文件名：LoveCarInfoUI
 *  创建者: zcy
 *  创建日期：2021/9/22 17:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineLoveCarInfoUI)
class LoveCarInfoUI : BaseMineUI<UiLoveCarInfoBinding, CarAuthViewModel>() {

    lateinit var auth: CarItemBean

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"

//        binding.tvAuth.isEnabled = true
        intent.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            auth = it as CarItemBean

            viewModel.queryAuthCarDetail(auth?.vin) {
                it.onSuccess {
                    it?.let {
                        auth = it
                    }
                    setAuthInfo()
                }
                it.onWithMsgFailure {
                    it?.let {
                        showToast(it)
                    }
                    setAuthInfo()
                }
            }
        }

        viewModel.carAuthQY() {
            it.onSuccess {
                it?.let {
                    binding.group.visibility =
                        if (it.authDetailRightsIsShow) View.VISIBLE else View.GONE
                    binding.carContent.text = it.authDetailRightsContent
                }
            }
        }
    }

    private fun setAuthInfo() {
        binding.cardVin.text = "${auth.vin ?: ""}"
        binding.cardModel.text = "${auth.modelName ?: ""}"
        binding.cardNum.text = "${auth.plateNum ?: ""}"
        binding.cardTime.text = "${TimeUtils.InputTimetamp(auth.saleDate) ?: ""}"
        binding.cardDealer.text = "${auth.dealerName ?: ""}"
        binding.cardDealerPhone.text = "${auth.dealerPhone ?: ""}"
        binding.carPic.load(auth.modelUrl, R.mipmap.ic_car_auth_ex)
    }
}