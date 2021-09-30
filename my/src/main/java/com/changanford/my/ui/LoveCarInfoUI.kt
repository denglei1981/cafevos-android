package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiLoveCarInfoBinding

/**
 *  文件名：LoveCarInfoUI
 *  创建者: zcy
 *  创建日期：2021/9/22 17:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineLoveCarInfoUI)
class LoveCarInfoUI : BaseMineUI<UiLoveCarInfoBinding, EmptyViewModel>() {

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"

        binding.tvAuth.isEnabled = true
        intent.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            var item = it as CarItemBean
            binding.cardNum.text = item.plateNum
            binding.cardVin.text = item.vin
            binding.cardModel.text = item.seriesName
        }
    }
}