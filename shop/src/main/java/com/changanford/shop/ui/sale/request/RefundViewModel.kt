package com.changanford.shop.ui.sale.request

import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.bean.RefundProgressBean
import com.changanford.shop.bean.RefundStautsBean


class RefundViewModel : BaseViewModel() {


    var invoiceLiveData: MutableLiveData<String> = MutableLiveData()

    var refundProgressLiveData: MutableLiveData<RefundProgressBean> = MutableLiveData()
    fun getRefund(refundType: String, orderNo: String, refundReason: String) {
        // 退款
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["refundType"] = refundType
            body["allOrderRefund"] = HashMap<String, String>().also {
                it["orderNo"] = orderNo
                it["refundReason"] = refundReason
            }
            ApiClient.createApi<ShopNetWorkApi>()
                .applyRefund(body.header(rKey), body.body(rKey))
                .onSuccess {
                    "申请已提交".toast()
                    invoiceLiveData.postValue("申请已提交")
                    LiveDataBus.get().with(LiveDataBusKey.GET_INVOICE)
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    /**
     *  退款中的数据。
     * */
    fun getRefundProgress(mallMallOrderId: String = "") {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallMallOrderId"] = mallMallOrderId
            ApiClient.createApi<ShopNetWorkApi>()
                .refundProgress(body.header(rKey), body.body(rKey))
                .onSuccess {
                    // 组装数据
                    val list: MutableList<RefundStautsBean> = mutableListOf()
                    val onGoing = it?.refundLogMap?.ON_GOING
                    val closed = it?.refundLogMap?.CLOSED
                    val success = it?.refundLogMap?.SUCESS
                    if (closed != null && closed.size > 0) {
                        list.addAll(closed)
                    }
                    if (onGoing != null && onGoing.size > 0) {
                        list.addAll(onGoing)
                    }
                    if (success != null && success.size > 0) {
                        list.addAll(success)
                    }
                    it?.refundList = list
                    refundProgressLiveData.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }
    /**
     *  撤销退款申请
     * */
    var cancelRefundLiveData: MutableLiveData<String> = MutableLiveData()
    fun cancelRefund(mallMallRefundId:String){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallMallRefundId"] = mallMallRefundId
            ApiClient.createApi<ShopNetWorkApi>()
                .cancelRefund(body.header(rKey), body.body(rKey))
                .onSuccess {
                    cancelRefundLiveData.postValue("成功")
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }


}