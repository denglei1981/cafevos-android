package com.changanford.shop.ui.order

import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActOrderEvaluationBinding

/**
 * @Author : wenke
 * @Time : 2021/9/23 0023
 * @Description : 订单评价
 */
class OrderEvaluationActivity:BaseActivity<ActOrderEvaluationBinding,OrderViewModel>() {
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {

    }
}