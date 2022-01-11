package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.base.ResponseBean
import com.changanford.shop.listener.OnPerformListener
import com.changanford.shop.utils.WConstant
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : OrderViewModel
 */
class OrderViewModel: BaseViewModel() {
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
    /**
     * 下单
     * [addressId]收货地址id
     * [buyNum]数量
     * [consumerMsg]买家留言
     * [payType]支付方式(积分),可用值:FB_PAY
     * [spuPageType]可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT,HAGGLE
     * [mallMallSkuSpuSeckillRangeId]秒杀的skuId
     * [mallMallHaggleUserGoodsId]发起砍价id
     * [vinCode]维保商品 vin码
     * buySource 业务来源 0普通商品 1秒杀商品 2砍价商品 3维保商品
     * */
    fun orderCreate(_skuId:String,addressId:Int?,spuPageType:String?,buyNum:Int,consumerMsg:String?="",
                    mallMallSkuSpuSeckillRangeId:String?=null,mallMallHaggleUserGoodsId:String?=null,vinCode:String?=null,mallMallWbVinSpuId:String?=null,payType:String="FB_PAY"){
        body.clear()
        var buySource=0
        var skuId=_skuId
        when(spuPageType){
            //秒杀
            "SECKILL"->{
                buySource=1
                skuId=mallMallSkuSpuSeckillRangeId?:skuId
            }
            //砍价
            "2"->{
                buySource=2
                mallMallHaggleUserGoodsId?.let {
                    if("0"!=it)body["mallMallHaggleUserGoodsId"]= it
                }
            }
            WConstant.maintenanceType->{//维保商品
                buySource=3
                body["mallMallWbVinSpuId"]= mallMallWbVinSpuId?:""
                body["vin"]= vinCode?:""
            }
        }
        viewModelScope.launch {
          fetchRequest (true){
                body["skuId"]=skuId
                body["busSourse"]=buySource
                body["buyNum"]=buyNum
                body["consumerMsg"]=consumerMsg?:""
                body["payType"]=payType
                body["addressId"]=addressId?:"0"
                val randomKey = getRandomKey()
                shopApiService.orderCreate(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
              orderInfoLiveData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
          }
        }
    }
    private val queryType= arrayOf("ALL","WAIT_PAY","WAIT_SEND","WAIT_RECEIVE","WATI_EVAL",)
    /**
     * 商品订单列表
     * [orderStatus]0全部 1待付款,2待发货,3待收货,4待评价
     * evalStatus 0待评价
     * */
    fun getShopOrderList(orderStatus:Int,pageNo:Int,pageSize:Int=this.pageSize,showLoading: Boolean = false){
        viewModelScope.launch {
            val responseBean=fetchRequest(showLoading) {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    var typeI=orderStatus+1
                    if(typeI<0||typeI>=queryType.size)typeI=0
                    it["queryType"] = queryType[typeI]
//                    if(null!=orderStatus&&orderStatus>-1&&orderStatus<3)it["orderStatus"] = orderStatus
//                    else if(3==orderStatus)it["evalStatus"] =0
                }
                val randomKey = getRandomKey()
                shopApiService.shopOrderList(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                shopOrderData.postValue(null)
                ToastUtils.showLongToast(it,MyApp.mContext)
            }
            responseBean.onSuccess {
                val timestamp=responseBean.timestamp?:System.currentTimeMillis().toString()
                it?.nowTime= timestamp.toLong()
                shopOrderData.postValue(it)
            }
        }
    }
    /**
     * 所有订单
     * */
    fun getAllOrderList(pageNo:Int,pageSize:Int=this.pageSize,showLoading: Boolean = false){
        viewModelScope.launch {
            fetchRequest(showLoading) {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                val randomKey = getRandomKey()
                shopApiService.getAllOrderList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                shopOrderData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
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
                ToastUtils.showLongToast(it,MyApp.mContext)
                addressList.postValue(null)
            }
        }
    }
    /**
     * 订单详情
     * [orderNo]订单号
     * */
    fun getOrderDetail(orderNo:String,showLoading: Boolean = false) {
        viewModelScope.launch {
            fetchRequest(showLoading){
                body.clear()
                body["orderNo"]=orderNo
                val randomKey = getRandomKey()
                shopApiService.orderDetail(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                orderItemLiveData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }
        }
    }
    /**
     * 取消订单
     * [orderNo]订单号
     * */
    fun orderCancel(orderNo:String,listener: OnPerformListener?=null) {
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
    fun fbPay(orderNo:String,fordPayType:String="INTEGRAL") {
        viewModelScope.launch {
            fetchRequest(true){
                body.clear()
                body["orderNo"]=orderNo
                body["fordPayType"]=fordPayType
                val randomKey = getRandomKey()
                shopApiService.fbPay(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                responseData.postValue(ResponseBean(true))
            }.onWithMsgFailure {
                responseData.postValue(ResponseBean(false,msg = it))
            }
        }
    }

    /**
     * 获取我的积分
     * */
    fun getMyIntegral(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getMyIntegral(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
            }.onSuccess {
                myFbLiveData.postValue(it as Int?)
            }
        }
    }
    /**
     * 订单确认收货
     * */
    fun confirmReceipt(orderNo:String,listener: OnPerformListener?){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["orderNo"]=orderNo
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
    fun getOrderKey(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["configKey"]="my_order_type"
                body["obj"]=true
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
    fun updateAddressByOrderNo(orderNo:String,addressId:Int,listener: OnPerformListener?){
        viewModelScope.launch {
            fetchRequest (true){
                body.clear()
                body["orderNo"]=orderNo
                body["addressId"]=addressId
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
    /**
     * 申请退货
     * [orderNo]订单号
     * */
    fun applyRefund(orderNo:String,listener: OnPerformListener?=null) {
        viewModelScope.launch {
            fetchRequest(true){
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.applyRefund(orderNo,body.header(randomKey), body.body(randomKey))
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
    fun getOrderStatus(orderStatus:String,evalStatus:String?):String{
        return when(orderStatus){
            "WAIT_PAY"->"待付款"
            "WAIT_SEND"->"待发货"
            "WAIT_RECEIVE"->"待收货"
            "FINISH"->{
                if(evalStatus!=null&&"WAIT_EVAL"==evalStatus)"待评价"
                else "已完成"
            }
            "CLOSED"->"已关闭"
            "AFERT_SALE_FINISH"->"售后已处理"
            else ->"未知"
        }
    }
}