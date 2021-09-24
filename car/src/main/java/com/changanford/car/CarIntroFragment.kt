package com.changanford.car

import com.changanford.car.databinding.ItemCarIntroBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.utilext.load

class CarIntroFragment : BaseFragment<ItemCarIntroBinding, CarViewModel>() {
    var imgUrl: String? = ""

    init {
        imgUrl = arguments?.getString("imgUrl")
    }

    override fun initView() {
        binding.imageCarIntro.load(R.mipmap.car_topimg1)
    }

    override fun initData() {
    }
}