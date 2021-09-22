package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.view.View
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 确认支付
 */
class PayConfirmActivity:BaseActivity<ShopActPayconfirmBinding,OrderViewModel>() {
    companion object{
        fun start(context: Context, orderInfo:String) {
            context.startActivity(Intent(context, PayConfirmActivity::class.java).putExtra("orderInfo",orderInfo))
        }
    }
    override fun initView() {
        PayTimeCountControl(1561615,binding.tvPayTime,object : OnTimeCountListener {
            override fun onFinish() {

            }
        }).start()
    }

    override fun initData() {

    }
    fun onClick(v:View){
        when(v.id){
            //确认支付
            R.id.btn_submit->{

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    fun onBack(v: View)=this.finish()
}