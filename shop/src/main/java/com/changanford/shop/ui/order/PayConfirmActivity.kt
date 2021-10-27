package com.changanford.shop.ui.order

import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 确认支付
 */
@Route(path = ARouterShopPath.PayConfirmActivity)
class PayConfirmActivity:BaseActivity<ShopActPayconfirmBinding, OrderViewModel>(),
    TopBar.OnBackClickListener {
    companion object{
        fun start(orderInfo:String) {
            JumpUtils.instans?.jump(110,orderInfo)
        }
    }
    private var timeCountControl:PayTimeCountControl?=null
    private var orderInfoBean:OrderInfoBean?=null
    private var dataBean:OrderItemBean?=null
    private var waitPayCountDown:Long=1800//支付剩余时间 默认半小时
    private var isPaySuccessful=false//是否支付成功
    private var isClickSubmit=false
    override fun initView() {
        binding.topBar.setOnBackClickListener(this)
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
        val payCountDown=dataBean?.waitPayCountDown?:waitPayCountDown
        if(payCountDown>0){
            timeCountControl=PayTimeCountControl(payCountDown*1000,binding.tvPayTime,object : OnTimeCountListener {
                override fun onFinish() {
                    payResults(false)
                }
            })
            timeCountControl?.start()
        }
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

    override fun onBackClick() {
        orderInfoBean?.let {
            when(it.source){
                //商品详情
                "1"->GoodsDetailsActivity.start(this,true)
                //H5商品砍价
                "2"->{
                    if(isPaySuccessful){}
                }
                //订单列表的再次购买
                "3"->{

                }
            }
        }
        this.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick()
        }
        return false
    }
}