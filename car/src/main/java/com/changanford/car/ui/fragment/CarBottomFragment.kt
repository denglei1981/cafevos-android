package com.changanford.car.ui.fragment

import com.changanford.car.CarViewModel
import com.changanford.car.databinding.CarFragmentBottomBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.bus.LiveDataBus

/**
 *Author lcw
 *Time on 2024/1/22
 *Purpose
 */
class CarBottomFragment : BaseFragment<CarFragmentBottomBinding, CarViewModel>() {

    var carBottomBinding: CarFragmentBottomBinding? = null

    override fun initView() {
        carBottomBinding = binding
        LiveDataBus.get().with("carBottom").postValue(binding)
    }

//    fun setPadding(padding: Int) {
//        binding.recyclerView.setPadding(0,padding,0,0)
//    }

    override fun initData() {

    }
}