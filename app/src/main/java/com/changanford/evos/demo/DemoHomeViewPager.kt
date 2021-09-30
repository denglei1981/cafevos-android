package com.changanford.evos.demo

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.utilext.load
import com.changanford.evos.databinding.FragmentDemohomeviewpagerBinding

class DemoHomeViewPager: BaseFragment<FragmentDemohomeviewpagerBinding, EmptyViewModel>() {
    var img :Int = 0
    override fun setArguments(args: Bundle?) {
        img = args?.getInt("img")!!
        super.setArguments(args)
    }
    override fun initView() {
        binding.img.load(img)
    }

    override fun initData() {
    }
}