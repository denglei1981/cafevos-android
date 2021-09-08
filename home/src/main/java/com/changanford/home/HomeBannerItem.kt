package com.changanford.home

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.AdBean
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.home.databinding.FragmentHomebannerBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.evos.home.HomeBannerItem
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/6 16:13
 * @Description: 　
 * *********************************************************************************
 */
class HomeBannerItem : BaseFragment<FragmentHomebannerBinding, EmptyViewModel>() {

    private var data: AdBean? = null
    override fun setArguments(args: Bundle?) {
        data = args?.getSerializable("data") as AdBean?
        super.setArguments(args)
    }

    override fun initView() {
        binding.homebannerImg.load(data?.adImg)
        binding.homebannerImg.setOnClickListener {
            data?.adName?.toast()
        }
    }

    override fun initData() {
    }
}