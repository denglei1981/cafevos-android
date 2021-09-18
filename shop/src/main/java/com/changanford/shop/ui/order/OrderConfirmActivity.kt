package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.view.View
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.R
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
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->PayConfirmActivity.start(this,"orderInfo")
        }
    }
    fun onBack(v: View)=this.finish()
}