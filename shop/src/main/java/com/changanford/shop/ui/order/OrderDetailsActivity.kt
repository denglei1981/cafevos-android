package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActOrderDetailsBinding

/**
 * @Author : wenke
 * @Time : 2021/9/26 0026
 * @Description : 商品订单详情
 */
class OrderDetailsActivity:BaseActivity<ActOrderDetailsBinding,OrderViewModel>() {
    companion object{
        fun start(context: Context,orderId:String) {
            context.startActivity(Intent(context, OrderDetailsActivity::class.java).putExtra("orderId",orderId))
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {
    }
}