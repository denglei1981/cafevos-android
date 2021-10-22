package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.ShopAddressInfoBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.control.OrderControl
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ActOrderDetailsBinding
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author : wenke
 * @Time : 2021/9/26 0026
 * @Description : 商品订单详情
 */
@Route(path = ARouterShopPath.OrderDetailActivity)
class OrderDetailsActivity:BaseActivity<ActOrderDetailsBinding, OrderViewModel>() {
    companion object{
        fun start(orderNo:String?) {
            if(MConstant.token.isEmpty()) JumpUtils.instans?.jump(100)
            else orderNo?.let {JumpUtils.instans?.jump(5,orderNo) }
        }
    }
    private val control by lazy { OrderControl(this,viewModel) }
    private lateinit var dataBean: OrderItemBean
    private var orderNo:String=""
    private var waitPayCountDown:Long=1800//支付剩余时间 默认半小时
    private var timeCountControl:PayTimeCountControl?=null
    private var isInitLiveDataBus=false
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
        viewModel.getOrderDetail(orderNo,true)
        viewModel.orderItemLiveData.observe(this,{
            bindingData(it)
        })
    }
    @SuppressLint("SetTextI18n")
    private fun bindingData(dataBean:OrderItemBean){
        val evalStatus=dataBean.evalStatus
        val orderStatus=dataBean.orderStatus
        //应付总额
        var totalPayName=R.string.str_copeWithTotalAmount
        binding.inOrderInfo.layoutOrderClose.visibility=View.VISIBLE
        binding.inAddress.apply {
            layoutLogistics.visibility=View.GONE
            imgRight.visibility=View.GONE
        }
        binding.inBottom.btnOrderConfirm.visibility=View.VISIBLE
        viewModel.getOrderStatus(orderStatus,evalStatus).apply {
            dataBean.orderStatusName= this
            when(this){
                "待付款","待支付"->{
                    //留言
                    dataBean.otherName=getString(R.string.str_leaveMessage)
                    dataBean.otherValue=dataBean.consumerMsg?:""
                    binding.tvOrderPrompt.apply {
                        visibility= View.GONE
//                        setText(R.string.prompt_orderUpdateAddress)
                    }
                    val payCountDown= dataBean.waitPayCountDown?:waitPayCountDown
                    if(payCountDown>0){
                        timeCountControl= PayTimeCountControl(payCountDown*1000, binding.tvOrderRemainingTime,object : OnTimeCountListener {
                            override fun onFinish() {
                                //支付倒计时结束 刷新
                                viewModel.getOrderDetail(orderNo)
                            }
                        })
                        timeCountControl?.start()
                    }
                    binding.inBottom.apply {
                        btnOrderCancle.visibility=View.VISIBLE
                        btnOrderConfirm.setText(R.string.str_immediatePayment)
                    }
                    binding.inAddress.imgRight.visibility=View.VISIBLE
                }
                "待发货"->{
                    totalPayName=R.string.str_realPayTotalAmount
                    //支付时间
                    dataBean.otherName=getString(R.string.str_payTime)
                    val otherValue=simpleDateFormat.format(dataBean.payTime?:0)
                    dataBean.otherValue=otherValue
                    binding.tvOrderPrompt.apply {
                        visibility= View.VISIBLE
                        setText(R.string.prompt_waitSend)
                    }
                    binding.tvOrderRemainingTime.setText(R.string.prompt_paymentHasBeen)
                    binding.inBottom.btnOrderConfirm.visibility=View.GONE
                }
                "待收货"->{
                    totalPayName=R.string.str_realPayTotalAmount
                    //发货时间
                    dataBean.otherName=getString(R.string.str_deliveryTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.sendTime?:0)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenShipped)
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_confirmGoods)
                }
                "待评价"->{
                    totalPayName=R.string.str_realPayTotalAmount
                    //发货时间
                    dataBean.otherName=getString(R.string.str_deliveryTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.sendTime?:0)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                    binding.tvOrderRemainingTime.setText(R.string.prompt_evaluate)
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_eval)
                }
                "已完成"->{
                    totalPayName=R.string.str_realPayTotalAmount
                    //支付时间
                    dataBean.otherName=getString(R.string.str_payTime)
                    dataBean.otherValue=simpleDateFormat.format(dataBean.payTime?:0)
                    binding.inAddress.layoutLogistics.visibility=View.VISIBLE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenCompleted)
                    binding.inAddress.tvLogisticsNo.text="${dataBean.courierCompany}  ${dataBean.courierNo}"
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_onceAgainToBuy)
                }
                "已关闭"->{
                    binding.inOrderInfo.layoutOrderClose.visibility=View.GONE
                    binding.tvOrderRemainingTime.text=dataBean.evalStatusDetail
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_onceAgainToBuy)
                }
            }
        }
        bindingAddressInfo(dataBean.addressInfo,false)
        //优惠积分
        val preferentialFb=dataBean.preferentialFb
        if(null!=preferentialFb&&"0"!=preferentialFb){
            binding.inGoodsInfo1.apply {
                tvIntegralVip.visibility=View.VISIBLE
                tvMemberDiscount.visibility=View.VISIBLE
            }
        }
        val freightPrice=dataBean.freightPrice
        if("0"==freightPrice)dataBean.freightPrice="0.00"
        dataBean.orderTimeTxt=simpleDateFormat.format(dataBean.orderTime?:0)
        binding.model=dataBean
        this.dataBean=dataBean
        binding.inGoodsInfo1.model=dataBean
        binding.inOrderInfo.apply {
            model=dataBean
            if("FB_PAY"!=dataBean.payType)tvPaymentValue.setText(R.string.str_other)
        }
        binding.inGoodsInfo.apply {
            model=dataBean
            inGoodsInfo.apply {
                model=dataBean
                GlideUtils.loadBD(GlideUtils.handleImgUrl(dataBean.skuImg),inGoodsInfo.imgGoodsCover)
                tvOrderType.apply {
                    visibility = when {
                        "YES"==dataBean.seckill -> {//秒杀
                            setText(R.string.str_seckill)
                            View.VISIBLE
                        }
                        "YES"==dataBean.haggleOrder -> {//砍价
                            setText(R.string.str_bargaining)
                            View.VISIBLE
                        }
                        else -> View.GONE
                    }
                }
                recyclerView.layoutManager= FlowLayoutManager(this@OrderDetailsActivity,false)
                recyclerView.adapter=OrderGoodsAttributeAdapter().apply {
                    val skuCodeTxt=dataBean.specifications.split(",").filter { ""!=it }
                    setList(skuCodeTxt)
                }
            }
        }
        binding.inBottom.apply {
            model=dataBean
            tvTotalPayFb.setText(totalPayName)
        }
    }
    private fun bindingAddressInfo(addressInfo:String,isUpdate:Boolean=false){
        Gson().fromJson(addressInfo,ShopAddressInfoBean::class.java).apply {
            //更新收货地址
            if(isUpdate){
                viewModel.updateAddressByOrderNo(dataBean.orderNo,addressId,object :OnPerformListener{
                    override fun onFinish(code: Int) {
                        dataBean.addressInfo= addressInfo
                        dataBean.addressId=addressId
                        updateAddressInfo(this@apply)
                    }
                })
            }else updateAddressInfo(this)
        }
    }
    private fun updateAddressInfo(item:ShopAddressInfoBean){
        item.apply {
            addressInfo="$provinceName$cityName$districtName$addressName"
            userInfo="$consignee   $phone"
            binding.inAddress.addressInfo=this
        }
    }
    /**
     * 取消订单
    * */
    private fun cancelOrder(){
        control.cancelOrder(dataBean,object :OnPerformListener{
            override fun onFinish(code: Int) {
                binding.inBottom.btnOrderCancle.visibility=View.GONE
                viewModel.getOrderDetail(orderNo)
            }
        })
    }
    private fun confirmOrder(){
        when(binding.inBottom.btnOrderConfirm.text){
            //再次购买
            getString(R.string.str_onceAgainToBuy)->control.onceAgainToBuy(dataBean)
            //评价
            getString(R.string.str_eval)->OrderEvaluationActivity.start(this,orderNo)
            //确认收货
            getString(R.string.str_eval)->control.confirmGoods(dataBean)
            //立即支付
            getString(R.string.str_immediatePayment)->control.toPay(dataBean)

        }
    }
    fun onClick(v:View){
        if(!::dataBean.isInitialized)return
        when(v.id){
            //复制物流信息
            R.id.tv_copy->MTextUtil.copystr(this,dataBean.courierNo)
            //取消订单
            R.id.btn_order_cancle-> cancelOrder()
            //支付、确认收货、评价、再次购买
            R.id.btn_order_confirm->confirmOrder()
            //修改收货地址
            R.id.img_right,R.id.tv_userInfo,R.id.tv_locationInfo->updateAddress()
        }
    }
    private fun updateAddress(){
        dataBean.apply {
            if("WAIT_PAY"==orderStatus){//修改地址
                JumpUtils.instans?.jump(20,"1")
                if(!isInitLiveDataBus){
                    isInitLiveDataBus=true
                    LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this@OrderDetailsActivity, {
                        it?.let {
                            bindingAddressInfo(it,true)
                        }
                    })
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
}