package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.databinding.ActOrderDetailsBinding
import com.changanford.shop.viewmodel.OrderViewModel

/**
 * @Author : wenke
 * @Time : 2021/9/26 0026
 * @Description : 商品订单详情
 */
@Route(path = ARouterShopPath.OrderDetailActivity)
class OrderDetailsActivity:BaseActivity<ActOrderDetailsBinding, OrderViewModel>() {
    companion object{
        fun start(context: Context,orderNo:String?) {
            orderNo?.let {context.startActivity(Intent(context, OrderDetailsActivity::class.java).putExtra("orderNo",orderNo))  }
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {
    }
}