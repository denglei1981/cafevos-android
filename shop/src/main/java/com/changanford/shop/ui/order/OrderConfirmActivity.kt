package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.R
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.view.TopBar

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 订单确认
 */
@Route(path = ARouterShopPath.OrderConfirmActivity)
class OrderConfirmActivity:BaseActivity<ActOrderConfirmBinding,OrderViewModel>(),
    TopBar.OnBackClickListener {
    companion object{
        fun start(context: Context, goodsInfo:String) {
            context.startActivity(Intent(context, OrderConfirmActivity::class.java).putExtra("goodsInfo",goodsInfo))
        }
    }
    override fun initView() {
        binding.topBar.setOnBackClickListener(this)
    }

    override fun initData() {

    }
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->PayConfirmActivity.start(this,"orderInfo")
        }
    }

    override fun onBackClick() {
        this.finish()
    }
}