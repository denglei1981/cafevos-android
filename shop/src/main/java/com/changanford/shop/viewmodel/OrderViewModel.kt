package com.changanford.shop.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.net.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.base.ResponseBean
import com.changanford.shop.bean.PostEvaluationBean
import com.changanford.shop.bean.ShopConfigBean
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : OrderViewModel
 */
class OrderViewModel : BaseViewModel() {
    init {
        isLogin()
    }

    //订单列表
    var shopOrderData = MutableLiveData<ShopOrderBean?>()

    /**
     * 获取地址列表
     */
    var addressList: MutableLiveData<ArrayList<AddressBeanItem>?> = MutableLiveData()

    //订单
    var orderInfoLiveData: MutableLiveData<OrderInfoBean> = MutableLiveData()
    var orderItemLiveData: MutableLiveData<OrderItemBean> = MutableLiveData()

    //我的积分
    var myFbLiveData: MutableLiveData<Int> = MutableLiveData()

    //订单类型
    var orderTypesLiveData: MutableLiveData<OrderTypesBean?> = MutableLiveData()

    //确认订单
    var createOrderBean = MutableLiveData<CreateOrderBean?>()

    //下单支付返回
    var payBackBeanLiveData = MutableLiveData<PayBackBean?>()

    //退款售后订单列表
    var refundBeanLiveData = MutableLiveData<ShopOrderRefundBean?>()
    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }
    /**
     * 下单
     * [addressId]收货地址id
     * [buyNum]数量
     * [consumerMsg]买家留言
     * [payType]支付方式(0纯积分/1纯现金/2混合支付)
     * [spuPageType]可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT,HAGGLE
     * [mallMallSkuSpuSeckillRangeId]秒杀的skuId
     * [mallMallHaggleUserGoodsId]发起砍价id
     * [vinCode]维保商品 vin码
     * [couponId]优惠卷id
     * buySource 业务来源 0普通商品 1秒杀商品 2砍价商品 3维保商品
     * */
//    fun orderCreate(_skuId:String,addressId:Int?,spuPageType:String?,buyNum:Int,consumerMsg:String?="",
//                    mallMallSkuSpuSeckillRangeId:String?=null,mallMallHaggleUserGoodsId:String?=null,
//                    vinCode:String?=null,mallMallWbVinSpuId:String?=null,payType:Int=0,couponId:String?=null){
//        body.clear()
//        var buySource=0
//        var skuId=_skuId
//        when(spuPageType){
//            //秒杀
//            "SECKILL"->{
//                buySource=1
//                skuId=mallMallSkuSpuSeckillRangeId?:skuId
//            }
//            //砍价
//            "2"->{
//                buySource=2
//                mallMallHaggleUserGoodsId?.let {
//                    if("0"!=it)body["mallMallHaggleUserGoodsId"]= it
//                }
//            }
//            WConstant.maintenanceType->{//维保商品
//                buySource=3
//                body["mallMallWbVinSpuId"]= mallMallWbVinSpuId?:""
//                body["vin"]= vinCode?:""
//            }
//        }
//        viewModelScope.launch {
//          fetchRequest (true){
//                body["skuId"]=skuId
//                body["busSourse"]=buySource
//                body["buyNum"]=buyNum
//                body["consumerMsg"]=consumerMsg?:""
//                body["payType"]=payType
//                body["addressId"]=addressId?:"0"
//                couponId?.let {
//                    body["couponId"]=it
//                }
//                val randomKey = getRandomKey()
//                shopApiService.orderCreate(body.header(randomKey), body.body(randomKey))
//            }.onSuccess {
//              orderInfoLiveData.postValue(it)
//            }.onWithMsgFailure {
//                ToastUtils.showLongToast(it,MyApp.mContext)
//          }
//        }
//    }
    /**
     * 下单
     * [orderConfirmType]确认订单来源 0 商品详情  1购物车
     * [payFb]支付总福币
     * [payRmb]支付总金额(人民币)
     * [addressId]收货地址id
     * [consumerMsg]买家留言
     * [skuItems]sku项
     * [couponId]优惠卷id
     * [couponRecordId]优惠卷领取id
     * [freight]运费
     * [payBfb]支付现金百分比
     * payType:支付方式(0纯积分/1纯现金/2混合支付)
     *
     * */
    fun createOrder(
        orderConfirmType: Int = 0,
        payFb: String? = null,
        payRmb: String? = null,
        addressId: Int? = 0,
        consumerMsg: String? = null,
        skuItems: ArrayList<OrderSkuItem>?,
        couponId: String? = null,
        couponRecordId: String? = "0",
        freight: String? = "0",
        payBfb: String? = null
    ) {
        body.clear()
        if (skuItems == null || skuItems.size < 1) return
        val fbPrice = if (TextUtils.isEmpty(payFb)) "0" else payFb
        val rmbPrice = if (TextUtils.isEmpty(payRmb)) "0" else payRmb
        var payType = 0
        if (fbPrice!!.toFloat() > 0 && rmbPrice!!.toFloat() > 0) {
            payType = 2
        } else if (rmbPrice!!.toFloat() > 0f) {
            payType = 1
        } else if (rmbPrice.toFloat() == 0f) {
            payType = 0
        }
        viewModelScope.launch {
            fetchRequest(false) {
                body["orderConfirmType"] = orderConfirmType
                body["zfb"] = fbPrice
                body["zje"] = rmbPrice
                body["freight"] = (freight ?: "0").toFloat()
                body["consumerMsg"] = consumerMsg ?: ""
                body["payBfb"] = payBfb ?: "0"
                body["payType"] = payType
                body["addressId"] = addressId ?: 0
                body["skuItems"] = skuItems
                couponId?.let {
                    body["couponId"] = it
                    body["couponRecordId"] = couponRecordId ?: "0"
                }
                val randomKey = getRandomKey()
                shopApiService.orderCreate(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                orderInfoLiveData.postValue(it)
                LiveDataBus.get().with(LiveDataBusKey.DISMISS_PAY_WAITING).postValue("")
            }.onWithMsgFailure {
                LiveDataBus.get().with(LiveDataBusKey.DISMISS_PAY_WAITING).postValue("")
//                if (it?.contains("地址") == true) {
////                    LiveDataBus.get().with(LiveDataBusKey.SHOW_ERROR_ADDRESS)
////                        .postValue(it.toString())
//                } else {
                    ToastUtils.showLongToast(it, MyApp.mContext)
//                }
            }
        }
    }

    val jdCheckBean = MutableLiveData<ArrayList<OrderConfirmSkuItems>>()

    fun jdOrderCreateBeforeCheck(addressId: String, skuItems: ArrayList<OrderConfirmSkuItems>) {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["addressId"] = addressId
                body["skuItems"] = skuItems
                val randomKey = getRandomKey()
                shopApiService.jdOrderCreateBeforeCheck(
                    body.header(randomKey),
                    body.body(randomKey)
                )
            }.onWithMsgFailure {
                LiveDataBus.get().with(LiveDataBusKey.DISMISS_PAY_WAITING).postValue("")
            }.onSuccess {
                jdCheckBean.value = it
                LiveDataBus.get().with(LiveDataBusKey.DISMISS_PAY_WAITING).postValue("")
            }
        }
    }

    /**
     * 确认订单
     * [orderConfirmType]确认订单来源 0商品详情 1购物车
     * */
    fun confirmOrder(orderConfirmType: Int, skuItems: ArrayList<ConfirmOrderInfoBean>) {
        viewModelScope.launch {
            fetchRequest(false) {
                body.clear()
                body["orderConfirmType"] = orderConfirmType
                body["skuItems"] = skuItems
                val randomKey = getRandomKey()
                shopApiService.confirmOrder(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
            }.onSuccess {
                formattingCouponsData(it)
//                createOrderBean.postValue(it)
            }
        }
    }

    private fun formattingCouponsData(bean: CreateOrderBean?) {
        bean?.apply {
            if (coupons != null && coupons!!.size > 0) {
                //判断每个优惠券是否可用
                for ((i, item) in coupons!!.withIndex()) {
                    item.isAvailable = false
                    item.mallMallSkuIds?.forEach { skuId ->
                        //首先查询skuId是否在订单中
                        skuItems?.find { skuId == it.skuId }?.apply {
                            item.isAvailable = true
                        }
                    }
                    bean.coupons?.set(i, item)
                }
            }
        }
        createOrderBean.postValue(bean)
    }

    private val queryType =
        arrayOf("ALL", "WAIT_PAY", "WAIT_SEND", "WAIT_RECEIVE", "WATI_EVAL", "REFUND")

    /**
     * 商品订单列表
     * [orderStatus]0全部 1待付款,2待发货,3待收货,4待评价 5售后退款
     * evalStatus 0待评价
     * */
    fun getShopOrderList(
        orderStatus: Int,
        pageNo: Int,
        pageSize: Int = this.pageSize,
        showLoading: Boolean = false
    ) {
        var typeI = orderStatus + 1
        if (typeI < 0 || typeI >= queryType.size) typeI = 0
        if (typeI == 5) {
            getShopOrderRefundList(pageNo, pageSize, showLoading)
            return
        }
        viewModelScope.launch {
            val responseBean = fetchRequest(showLoading) {
                body.clear()
                body["pageNo"] = pageNo
                body["pageSize"] = pageSize
                body["queryParams"] = HashMap<String, Any>().also {
                    it["queryType"] = queryType[typeI]
                }
                val randomKey = getRandomKey()
                shopApiService.shopOrderListV2(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                shopOrderData.postValue(null)
                it?.toast()
            }
            responseBean.onSuccess {
                val timestamp = responseBean.timestamp ?: System.currentTimeMillis().toString()
                it?.nowTime = timestamp.toLong()
                shopOrderData.postValue(it)
            }
        }
    }

    /**
     * 退款订单列表
     * */
    fun getShopOrderRefundList(
        pageNo: Int,
        pageSize: Int = this.pageSize,
        showLoading: Boolean = false
    ) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                body.clear()
                body["pageNo"] = pageNo
                body["pageSize"] = pageSize
                val randomKey = getRandomKey()
                shopApiService.shopOrderRefundListV2(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                refundBeanLiveData.postValue(null)
                it?.toast()
            }.onSuccess {
                refundBeanLiveData.postValue(it)
            }
        }
    }

    /**
     * 所有订单
     * */
    fun getAllOrderList(pageNo: Int, pageSize: Int = this.pageSize, showLoading: Boolean = false) {
        viewModelScope.launch {
            fetchRequest(showLoading) {
                body.clear()
                body["pageNo"] = pageNo
                body["pageSize"] = pageSize
                val randomKey = getRandomKey()
                shopApiService.getAllOrderList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                shopOrderData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it, MyApp.mContext)
            }
        }
    }

    /**
     * 获取地址列表
     * */
    fun getAddressList() {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                apiService.getAddressList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                addressList.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it, MyApp.mContext)
                addressList.postValue(null)
            }
        }
    }

    /**
     * 订单详情
     * [orderNo]订单号
     * */
    fun getOrderDetail(orderNo: String, showLoading: Boolean = false) {
        viewModelScope.launch {
            val con = fetchRequest(showLoading) {
                body.clear()
                body["orderNo"] = orderNo
                val randomKey = getRandomKey()
                shopApiService.orderDetail(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
            }
            con.onSuccess {
                it?.timestamp = con.timestamp?.toLong()
                orderItemLiveData.postValue(it)
            }
        }
    }

    /**
     * 取消订单
     * [orderNo]订单号
     * */
    fun orderCancel(orderNo: String, listener: OnPerformListener? = null) {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["orderNo"] = orderNo
                val randomKey = getRandomKey()
                shopApiService.orderCancel(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                listener?.onFinish(0)
            }
        }
    }

    /**
     * fb支付
     * [orderNo]订单号
     * */
    fun fbPay(orderNo: String, fordPayType: String = "INTEGRAL") {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["orderNo"] = orderNo
                body["fordPayType"] = fordPayType
                val randomKey = getRandomKey()
                shopApiService.fbPay(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                responseData.postValue(ResponseBean(true))
            }.onWithMsgFailure {
                responseData.postValue(ResponseBean(false, msg = it))
            }
        }
    }

    fun fbPayBatch(orderNo: String, payFb: String) {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["privatePayNo"] = orderNo
                body["payFb"] = payFb
                val randomKey = getRandomKey()
                shopApiService.fbPayBatch(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                responseData.postValue(ResponseBean(true))
            }.onWithMsgFailure {
                responseData.postValue(ResponseBean(false, msg = it))
            }
        }
    }

    /**
     * 人民币支付
     * [orderNo]订单号
     * [payType] 支付方式 1支付宝 2微信  3银联
     * */
    fun rmbPay(orderNo: String, payType: String = "1") {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["orderNo"] = orderNo
                body["payType"] = payType
                val randomKey = getRandomKey()
                shopApiService.rmbPay(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                payBackBeanLiveData.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
                payBackBeanLiveData.postValue(null)
            }
        }
    }

    fun rmbPayBatch(orderNo: String, payRmb: String, payType: String = "1") {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["privatePayNo"] = orderNo
                body["payType"] = payType
                body["payRmb"] = payRmb
                val randomKey = getRandomKey()
                shopApiService.rmbPayBatch(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                payBackBeanLiveData.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
                payBackBeanLiveData.postValue(null)
            }
        }
    }

    /**
     * 获取我的积分
     * */
    fun getMyIntegral() {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getMyIntegral(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it, MyApp.mContext)
            }.onSuccess {
                myFbLiveData.postValue(it as Int?)
            }
        }
    }

    /**
     * 订单确认收货
     * */
    fun confirmReceipt(orderNo: String, listener: OnPerformListener?) {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["orderNo"] = orderNo
                val randomKey = getRandomKey()
                shopApiService.confirmReceipt(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                listener?.onFinish(0)
            }
        }
    }

    /**
     * 获取订单类型
     * */
    fun getOrderKey() {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["configKey"] = "my_order_type"
                body["obj"] = true
                val randomKey = getRandomKey()
                shopApiService.getOrderKey(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                orderTypesLiveData.postValue(null)
//                ToastUtils.showLongToast(it)
            }.onSuccess {
                orderTypesLiveData.postValue(it)
            }
        }
    }

    /**
     * 修改商品待支付状态的收货地址
     * */
    fun updateAddressByOrderNo(orderNo: String, addressId: Int, listener: OnPerformListener?) {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["orderNo"] = orderNo
                body["addressId"] = addressId
                val randomKey = getRandomKey()
                shopApiService.updateAddressByOrderNo(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                ToastUtils.reToast(R.string.str_addressChangedSuccessfully)
                listener?.onFinish(0)
            }
        }
    }

    fun updateAddressByOrderNoV2(orderNo: String, addressId: Int, listener: OnPerformListener?) {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["mallMallOrderId"] = orderNo
                body["addrId"] = addressId
                val randomKey = getRandomKey()
                shopApiService.updateAddress(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                ToastUtils.reToast(R.string.str_addressChangedSuccessfully)
                listener?.onFinish(0)
            }
        }
    }

    /**
     * 申请退货
     * [orderNo]订单号
     * */
    fun applyRefund(orderNo: String, listener: OnPerformListener? = null) {
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.applyRefund(orderNo, body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                listener?.onFinish(0)
            }
        }
    }

    /**
     * 订单状态(WAIT_PAY 待付款,WAIT_SEND 待发货,WAIT_RECEIVE 待收货,FINISH 已完成,CLOSED 已关闭)
     * */
    fun getOrderStatus(orderStatus: String, evalStatus: String?): String {
        return when (orderStatus) {
            "WAIT_PAY" -> "待付款"
            "WAIT_SEND", "WAIT_SYS_REGIST" -> "待发货"
            "WAIT_RECEIVE" -> "待收货"
            "FINISH" -> {
                if (evalStatus != null && "WAIT_EVAL" == evalStatus) "待评价"
                else "已完成"
            }

            "WAIT_EVAL" -> "待评价"
            "CLOSED" -> "已关闭"
            "REFUNDING" -> "退款中"
            "AFERT_SALE_FINISH" -> "售后已处理"
            else -> "未知"
        }
    }

    /**
     * 发布评价、追评
     * [orderNo]订单号
     * [evalList]评价列表
     * [reviewEval]是否追评 YES NO
     * */
    fun postEvaluation(
        orderNo: String,
        evalList: List<PostEvaluationBean>?,
        reviewEval: Boolean = false
    ) {
        if (evalList == null) return
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                body["orderNo"] = orderNo
                body["evalList"] = evalList
                body["reviewEval"] = if (reviewEval) "YES" else "NO"
                val randomKey = getRandomKey()
                shopApiService.orderEval(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
            }.onSuccess {
                responseData.postValue(ResponseBean(true))
            }
        }
    }

    val shopConfigBean = MutableLiveData<ShopConfigBean>()

    fun getShopConfig() {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["configKey"] = "mall_second_config"
                body["obj"] = true
                val randomKey = getRandomKey()
                shopApiService.getShopConfig(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
//                ToastUtils.showLongToast(it)
            }.onSuccess {
                shopConfigBean.postValue(it)
            }
        }
    }
}