package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.ShopOrderBean
import com.changanford.common.net.*
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : OrderViewModel
 */
class OrderViewModel: BaseViewModel() {
    //首页
    var shopOrderData = MutableLiveData<ShopOrderBean>()
    /**
     * 获取地址列表
     */
    var addressList: MutableLiveData<ArrayList<AddressBeanItem>?> = MutableLiveData()
    /**
     * 下单
     * [addressId]收货地址id
     * [buyNum]数量
     * [consumerMsg]买家留言
     * [payType]支付方式(积分),可用值:MallPayTypeEnum.FB_PAY(code=FB_PAY, dbCode=0, message=积分支付)
     * [busSourse] 业务来源 0普通商品 1秒杀商品 2砍价商品
     * */
    fun orderCreate(skuId:String,addressId:String,busSourse:String,buyNum:Int,consumerMsg:String?,payType:String="FB_PAY"){
        viewModelScope.launch {
           fetchRequest {
                body.clear()
                body["skuId"]=skuId
                body["busSourse"]=busSourse
                body["buyNum"]=buyNum
                body["consumerMsg"]=consumerMsg?:""
                body["payType"]=payType
                body["addressId"]=addressId
                val randomKey = getRandomKey()
                shopApiService.orderCreate(body.header(randomKey), body.body(randomKey))
            }.onSuccess {

            }
        }
    }
    /**
     * 商品订单列表
     * [orderStatus] 0待付款,1待发货,2待收货,3已完成
     * evalStatus 0待评价
     * */
    fun getShopOrderList(orderStatus:Int?,pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    if(null!=orderStatus&&orderStatus>-1&&orderStatus<3)it["orderStatus"] = orderStatus
                    else if(3==orderStatus)it["evalStatus"] =0
                }
                val randomKey = getRandomKey()
                shopApiService.shopOrderList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                shopOrderData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
            }
        }
    }
    /**
     * 所有订单
     * */
    fun getAllOrderList(pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
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
                val rkey = getRandomKey()
                apiService.getAddressList(body.header(rkey), body.body(rkey))
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
    fun getOrderDetail(orderNo:String) {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["orderNo"]=orderNo
                val rkey = getRandomKey()
                shopApiService.orderDetail(body.header(rkey), body.body(rkey))
            }.onSuccess {

            }
        }
    }
    /**
     * 取消订单
     * [orderNo]订单号
     * */
    fun orderCancel(orderNo:String) {
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["orderNo"]=orderNo
                val rkey = getRandomKey()
                shopApiService.orderCancel(body.header(rkey), body.body(rkey))
            }.onSuccess {

            }
        }
    }
}