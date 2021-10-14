package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.ShopAddressInfoBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ActOrderDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat

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
    private lateinit var dataBean: OrderItemBean
    private var orderNo:String=""
    private var waitPayCountDown:Long=1800//支付剩余时间 默认半小时
    private var timeCountControl:PayTimeCountControl?=null
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
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
    @SuppressLint("SetTextI18n")
    private fun bindingData(dataBean:OrderItemBean){
        val evalStatus=dataBean.evalStatus
        val orderStatus=dataBean.orderStatus
        binding.inAddress.layoutLogistics.visibility=View.GONE
        viewModel.getOrderStatus(orderStatus,evalStatus).apply {
            dataBean.orderStatusName= this
            when(this){
                "待付款"->{
                    //留言
                    dataBean.otherName=getString(R.string.str_leaveMessage)
                    dataBean.otherValue=dataBean.consumerMsg?:""
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
                }
                "待发货"->{
                    //支付时间
                    dataBean.otherName=getString(R.string.str_payTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.updateTime)
                    binding.tvOrderPrompt.apply {
                        visibility= View.VISIBLE
                        setText(R.string.prompt_waitSend)
                    }
                    binding.tvOrderRemainingTime.setText(R.string.prompt_paymentHasBeen)
                }
                "待收货"->{
                    //发货时间
                    dataBean.otherName=getString(R.string.str_deliveryTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.updateTime)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenShipped)
                }
                "待评价"->{
                    //发货时间
                    dataBean.otherName=getString(R.string.str_deliveryTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.orderTime)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                    binding.tvOrderRemainingTime.setText(R.string.prompt_evaluate)
                }
                "已完成"->{
                    //支付时间
                    dataBean.otherName=getString(R.string.str_payTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.updateTime)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenCompleted)
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                }
                "已关闭"->{
                    dataBean.statesVisibility=2
                    binding.tvOrderRemainingTime.text=dataBean.evalStatusDetail
                }
            }
        }
        Gson().fromJson(dataBean.addressInfo,ShopAddressInfoBean::class.java).apply {
            addressInfo="$provinceName$cityName$districtName$addressName"
            userInfo="$consignee   $phone"
            binding.inAddress.addressInfo=this
        }
        //会员优惠
        val preferentialFb=dataBean.preferentialFb
        if(null!=preferentialFb&&"0"!=preferentialFb){
            binding.inGoodsInfo1.tvIntegralVip.visibility=View.VISIBLE
            binding.inGoodsInfo1.tvMemberDiscount.visibility=View.VISIBLE
        }
        binding.model=dataBean
        this.dataBean=dataBean
        binding.inGoodsInfo1.model=dataBean
        binding.inOrderInfo.apply {
            model=dataBean
            if("FB_PAY"!=dataBean.payType)tvPaymentValue.setText(R.string.str_other)
        }
        binding.inGoodsInfo.apply {
            model=dataBean
            inGoodsInfo.model=dataBean
            GlideUtils.loadBD(GlideUtils.handleImgUrl(dataBean.skuImg),inGoodsInfo.imgGoodsCover)
        }
    }
    fun onClick(v:View){
        if(!::dataBean.isInitialized)return
        when(v.id){
            R.id.tv_copy->{
                MTextUtil.copystr(this,dataBean.courierNo)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
}