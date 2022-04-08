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


class RefundViewModel : BaseViewModel() {


    var invoiceLiveData: MutableLiveData<String> = MutableLiveData()


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
    fun getRefundProgress(mallMallOrderId:String=""){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallMallOrderId"] = mallMallOrderId
            ApiClient.createApi<ShopNetWorkApi>()
                .refundProgress(body.header(rKey), body.body(rKey))
                .onSuccess {


                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }


}