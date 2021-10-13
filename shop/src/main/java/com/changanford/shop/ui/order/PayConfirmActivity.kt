package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 确认支付
 */
@Route(path = ARouterShopPath.PayConfirmActivity)
class PayConfirmActivity:BaseActivity<ShopActPayconfirmBinding, OrderViewModel>(){
    companion object{
        fun start(context: Context, orderInfo:String) {
            context.startActivity(Intent(context, PayConfirmActivity::class.java).putExtra("orderInfo",orderInfo))
        }
    }
    private var timeCountControl:PayTimeCountControl?=null
    private var dataBean:OrderItemBean?=null
    private var waitPayCountDown:Long=30*60*1000//支付剩余时间 默认半小时
    private var isPaySuccessful=false//是否支付成功
    override fun initView() {
        binding.topBar.setActivity(this)
        viewModel.responseData.observe(this,{
            ToastUtils.showLongToast(it.msg,this)
            payResults(it.isSuccess)
        })
    }
    override fun initData() {
        val orderInfo=intent.getStringExtra("orderInfo")
        if(null==orderInfo){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        dataBean= Gson().fromJson(orderInfo,OrderItemBean::class.java)
        dataBean?.orderNo?.let { viewModel.getOrderDetail(it) }
        bindingData()
    }
    private fun bindingData(){
        binding.model=dataBean
        binding.tvAccountPoints.setHtmlTxt(getString(R.string.str_Xfb,dataBean?.acountFb),"#00095B")
        val payCountDown=dataBean?.waitPayCountDown?:waitPayCountDown
        timeCountControl=PayTimeCountControl(if(payCountDown>0)payCountDown else waitPayCountDown,binding.tvPayTime,object : OnTimeCountListener {
            override fun onFinish() {
                payResults(false)
            }
        })
        timeCountControl?.start()
    }
    /**
     * [isSuccessful]支付成功、支付失败
    * */
    private fun payResults(isSuccessful:Boolean){
        binding.layoutPay.visibility=View.GONE
        binding.inPayResults.apply {
            model=dataBean
            layoutPayResults.visibility=View.VISIBLE
            tvPayResultsState.setText(if(isSuccessful)R.string.str_paySucces else R.string.str_payFailure)
            val dTop=ContextCompat.getDrawable(this@PayConfirmActivity,if(isSuccessful)R.mipmap.shop_pay_succes else R.mipmap.shop_pay_failure)
            tvPayResultsState.setCompoundDrawablesRelativeWithIntrinsicBounds(null,dTop,null,null)
        }
        binding.btnSubmit.setText(R.string.str_orderDetails)
    }
    fun btnSubmit(v:View){
        if(null==dataBean)return
        if(binding.btnSubmit.text==getString(R.string.str_payConfirm)){//支付
            viewModel.fbPay(dataBean?.orderNo!!)
        }else {
            OrderDetailsActivity.start(this,dataBean?.orderNo)
            if(isPaySuccessful)this.finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
    fun onBack(v: View)=this.finish()
}