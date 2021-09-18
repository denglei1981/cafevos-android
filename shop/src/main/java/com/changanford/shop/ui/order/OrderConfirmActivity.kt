package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActOrderConfirmBinding

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 订单确认
 */
class OrderConfirmActivity:BaseActivity<ActOrderConfirmBinding,OrderViewModel>() {
    companion object{
        fun start(context: Context, goodsInfo:String) {
            context.startActivity(Intent(context, OrderConfirmActivity::class.java).putExtra("goodsInfo",goodsInfo))
        }
    }
    override fun initView() {

    }

    override fun initData() {

    }
}