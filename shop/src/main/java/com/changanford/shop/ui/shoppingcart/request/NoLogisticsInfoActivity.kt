package com.changanford.shop.ui.shoppingcart.request

import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActivityMultiplePackageBinding
import com.changanford.shop.view.TopBar

class NoLogisticsInfoActivity:
    BaseActivity<ActivityMultiplePackageBinding, MultiplePackageViewModel>()  {
    override fun initView() {
        binding.topbar.setOnBackClickListener(object :TopBar.OnBackClickListener{
            override fun onBackClick() {
                finish()
            }
        })
    }
    override fun initData() {

    }
}