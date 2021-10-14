package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ActOrderDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
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
    private var orderNo:String=""
    private var waitPayCountDown:Long=1800//支付剩余时间 默认半小时
    private var timeCountControl:PayTimeCountControl?=null
    override fun initView() {
        binding.topBar.setActivity(this)
        orderNo=intent.getStringExtra("orderNo")?:""
        if(orderNo.isEmpty()){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
    }

    override fun initData() {
        viewModel.getOrderDetail(orderNo)
        viewModel.orderItemLiveData.observe(this,{
            bindingData(it)
        })
    }
    private fun bindingData(dataBean:OrderItemBean){
        val evalStatus=dataBean.evalStatus
        val orderStatus=dataBean.orderStatus
        viewModel.getOrderStatus(orderStatus,evalStatus).apply {
            dataBean.orderStatusName= this
            when(this){
                "待付款"->{
                    binding.tvOrderPrompt.apply {
                        visibility= View.VISIBLE
                        setText(R.string.prompt_orderUpdateAddress)
                    }
                    val payCountDown= dataBean.waitPayCountDown?:waitPayCountDown
                    timeCountControl= PayTimeCountControl(payCountDown*1000, binding.tvOrderRemainingTime,object : OnTimeCountListener {
                        override fun onFinish() {
                            //支付倒计时结束 刷新
                            viewModel.getOrderDetail(orderNo)
                        }
                    })
                    timeCountControl?.start()
                    Log.e("wenke","payCountDown:$payCountDown")

                }
                "待发货"->{
                    binding.tvOrderPrompt.apply {
                        visibility= View.VISIBLE
                        setText(R.string.prompt_waitSend)
                    }
                    binding.tvOrderRemainingTime.setText(R.string.prompt_paymentHasBeen)
                }
                "待收货"->{
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenShipped)
                }
                "已关闭"->{
                    binding.tvOrderRemainingTime.text=dataBean.evalStatusDetail
                }
                "已完成"->{
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenCompleted)
                }
            }
        }
        binding.model=dataBean
    }

    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
}