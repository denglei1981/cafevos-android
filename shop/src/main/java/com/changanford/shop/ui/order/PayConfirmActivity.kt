package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 确认支付
 */
@Route(path = ARouterShopPath.PayConfirmActivity)
class PayConfirmActivity:BaseActivity<ShopActPayconfirmBinding, OrderViewModel>(){
    companion object{
        fun start(context: Context, orderInfo:String) {
            if(MConstant.token.isEmpty()) JumpUtils.instans?.jump(100)
            else context.startActivity(Intent(context, PayConfirmActivity::class.java).putExtra("orderInfo",orderInfo))
        }
    }
    private var timeCountControl:PayTimeCountControl?=null
    private var orderInfoBean:OrderInfoBean?=null
    private var dataBean:OrderItemBean?=null
    private var waitPayCountDown:Long=1800//支付剩余时间 默认半小时
    private var isPaySuccessful=false//是否支付成功
    private var isClickSubmit=false
    override fun initView() {
        binding.topBar.setActivity(this)
    }
    override fun initData() {
        val orderInfo=intent.getStringExtra("orderInfo")
        if(null==orderInfo){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        orderInfoBean= Gson().fromJson(orderInfo,OrderInfoBean::class.java)
        orderInfoBean?.orderNo?.let { viewModel.getOrderDetail(it) }
        viewModel.orderItemLiveData.observe(this,{orderItem->
            orderInfoBean?.accountFb?.let {orderItem.acountFb=it }
            dataBean= orderItem
            bindingData()
        })
        viewModel.responseData.observe(this,{
            ToastUtils.showLongToast(it.msg,this)
            isClickSubmit=false
            payResults(it.isSuccess)
        })
    }
    private fun bindingData(){
        binding.model=dataBean
        binding.tvAccountPoints.setHtmlTxt(getString(R.string.str_Xfb,dataBean?.acountFb),"#00095B")
        var payCountDown=dataBean?.waitPayCountDown?:waitPayCountDown
        if(payCountDown<0)payCountDown=waitPayCountDown
        timeCountControl=PayTimeCountControl(payCountDown*1000,binding.tvPayTime,object : OnTimeCountListener {
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
        this.isPaySuccessful=isSuccessful
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
        dataBean?.let {
            if(!isClickSubmit){
                isClickSubmit=true
                if(binding.btnSubmit.text==getString(R.string.str_payConfirm)){//支付
                    viewModel.fbPay(it.orderNo)
                }else {
                    OrderDetailsActivity.start(it.orderNo)
                    isClickSubmit=false
                    if(isPaySuccessful)this.finish()
                }
            }
            GlobalScope.launch {
                delay(3000L)
                isClickSubmit=false
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
    fun onBack(v: View)=this.finish()
}