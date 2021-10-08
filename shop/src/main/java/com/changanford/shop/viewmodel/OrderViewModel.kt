package com.changanford.shop.viewmodel

import androidx.lifecycle.viewModelScope
import com.changanford.common.net.*
import com.changanford.shop.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : OrderViewModel
 */
class OrderViewModel: BaseViewModel() {

    /**
     * 下单
     * [addressId]收货地址id
     *[addressInfo]收货信息（省 市 区 地址 收货人名称 电话 快照,json)
     * [buyNum]数量
     * [consumerMsg]买家留言
     * [discount]是否折扣
     * [payType]支付方式(积分),可用值:MallPayTypeEnum.FB_PAY(code=FB_PAY, dbCode=0, message=积分支付)
     *
     * */
    fun orderCreate(addressId:String,addressInfo:String,buyNum:Int,consumerMsg:String?,discount:Int=0,payType:String="MallPayTypeEnum.FB_PAY"){
        viewModelScope.launch {
           fetchRequest {
                body.clear()
                body["addressId"]=addressId
                body["addressInfo"]=addressInfo
                body["buyNum"]=buyNum
                body["consumerMsg"]=consumerMsg?:""
                body["discount"]=discount
                body["payType"]=addressId
                body["addressId"]=addressId
                body["addressId"]=addressId
                val randomKey = getRandomKey()
                shopApiService.orderCreate(body.header(randomKey), body.body(randomKey))
            }.onSuccess {

            }
        }
    }
}