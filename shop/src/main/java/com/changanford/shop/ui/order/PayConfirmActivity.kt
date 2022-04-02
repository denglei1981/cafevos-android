package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayBackBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.ui.compose.UnionPayCompose
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
    TopBar.OnBackClickListener, OnTimeCountListener {
    companion object{
        fun start(orderNo:String) {
            JumpUtils.instans?.jump(110,orderNo)
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
        var orderNo=intent.getStringExtra("orderNo")
        if(TextUtils.isEmpty(orderNo)){
            //兼容以前版本
            intent.getStringExtra("orderInfo")?.apply {
                if(this.startsWith("{")){
                    orderInfoBean= Gson().fromJson(this,OrderInfoBean::class.java)
                    orderNo=orderInfoBean?.orderNo
                }
            }
        }
        if (TextUtils.isEmpty(orderNo)){
            ToastUtils.reToast(R.string.str_parameterIllegal)
            return
        }
        orderNo?.let { viewModel.getOrderDetail(it) }
        viewModel.orderItemLiveData.observe(this) { orderItem ->
            dataBean = orderItem
            bindingData()
        }
        //支付回调
        viewModel.payBackBeanLiveData.observe(this) {
            isClickSubmit = false
            payResults(it)
        }
    }
    private fun bindingData(){
        dataBean?.apply {
            if(waitPayCountDown==null||waitPayCountDown==0L){
                payResults(null)
                return
            }
            when(payType){
                "FB_PAY"->{
                    binding.apply {
                        layoutPay.visibility=View.VISIBLE
                        btnSubmit.visibility=View.VISIBLE
                        composeView.visibility=View.GONE
                        model=dataBean
                        tvAccountPoints.setHtmlTxt(getString(R.string.str_Xfb,totalIntegral),"#00095B")
                        //账户余额小于所支付额度 则余额不足
                        if(totalIntegral!!.toFloat()<fbCost!!.toFloat())btnSubmit.setStates(8)
                        else btnSubmit.setStates(12)
                        val payCountDown=waitPayCountDown?:this@PayConfirmActivity.waitPayCountDown
                        if(payCountDown>0){
                            timeCountControl?.cancel()
                            timeCountControl=PayTimeCountControl(payCountDown*1000,tv=binding.tvPayTime,null,this@PayConfirmActivity)
                            timeCountControl?.start()
                        }
                    }
                }
                //现金支付和混合支付 使用银联支付
                else ->{
                    binding.apply {
                        layoutPay.visibility=View.INVISIBLE
                        btnSubmit.visibility=View.INVISIBLE
                        composeView.visibility=View.VISIBLE
                        composeView.setContent {
                            UnionPayCompose(dataBean,viewModel,this@PayConfirmActivity)
                        }
                    }
                }
            }
        }

    }
    /**
     * isSuccessful支付成功、支付失败
    * */
    @SuppressLint("SetTextI18n")
    private fun payResults(payBackBean:PayBackBean?){
        this.isPaySuccessful=payBackBean!=null
        binding.layoutPay.visibility=View.INVISIBLE
        binding.composeView.visibility=View.GONE
        binding.inPayResults.apply {
            model=dataBean
            layoutPayResults.visibility=View.VISIBLE
            if(dataBean?.payType!="FB_PAY")tvPayResultsPrice.text="共计￥${dataBean?.payRmb}"
            else tvPayResultsPrice.text="共计${dataBean?.payFb}福币"
            tvPayResultsState.setText(if(isPaySuccessful)R.string.str_paySucces else R.string.str_payFailure)
            val dTop=ContextCompat.getDrawable(this@PayConfirmActivity,if(isPaySuccessful)R.mipmap.shop_pay_succes else R.mipmap.shop_pay_failure)
            tvPayResultsState.setCompoundDrawablesRelativeWithIntrinsicBounds(null,dTop,null,null)
        }
        binding.btnSubmit.visibility=View.VISIBLE
        binding.btnSubmit.setStates(11)
    }
    fun btnSubmit(v:View){
        dataBean?.apply {
            if(!isClickSubmit){
                isClickSubmit=true
                when(binding.btnSubmit.text){
                    getString(R.string.str_payConfirm)-> viewModel.fbPay(orderNo)
                    getString(R.string.str_orderDetails)->{
                        val jumpDataType=dataBean?.jumpDataType
                        when {
                            null!=jumpDataType -> {
                                JumpUtils.instans?.jump(jumpDataType,dataBean?.jumpDataValue)
                            }
                            "3"==busSourse -> {//维保商品订单详情
                                JumpUtils.instans?.jump(1,String.format(MConstant.H5_SHOP_MAINTENANCE,orderNo))
                            }
                            else -> OrderDetailsActivity.start(orderNo)
                        }
                        isClickSubmit=false
                        if(isPaySuccessful)this@PayConfirmActivity.finish()
                    }
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
        //确认支付 返回到订单详情
//        if(binding.btnSubmit.text==getString(R.string.str_payConfirm)){
//            dataBean?.apply {
//                val jumpDataType=dataBean?.jumpDataType
//                when {
//                    null!=jumpDataType -> {
//                        JumpUtils.instans?.jump(jumpDataType,dataBean?.jumpDataValue)
//                    }
//                    "3"==busSourse -> {//维保商品订单详情
//                        JumpUtils.instans?.jump(1,String.format(MConstant.H5_SHOP_MAINTENANCE,orderNo))
//                    }
//                    else -> OrderDetailsActivity.start(orderNo)
//                }
//            }
//        }
        this.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick()
        }
        return false
    }
    /**
     * 倒计时 结束回调
    * */
    override fun onFinish() {
        payResults(null)
    }
}