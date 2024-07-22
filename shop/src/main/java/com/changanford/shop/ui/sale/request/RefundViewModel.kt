package com.changanford.shop.ui.sale.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.bean.RefundProgressBean
import com.changanford.shop.bean.RefundProgressMultipleBean
import com.changanford.shop.bean.RefundStautsBean

import kotlinx.coroutines.launch


class RefundViewModel : BaseViewModel() {


    var invoiceLiveData: MutableLiveData<String> = MutableLiveData()

    var refundProgressLiveData: MutableLiveData<RefundProgressBean> = MutableLiveData()

    var refundSingleLiveData: MutableLiveData<String> = MutableLiveData()
    val stsBean = MutableLiveData<STSBean>()


    fun getRefund(orderNo: String, refundReason: String) {
        // 退款
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["refundType"] = "ALL_ORDER"
            body["allOrderRefund"] = HashMap<String, String>().also {
                it["orderNo"] = orderNo
                it["refundReason"] = refundReason
            }
            ApiClient.createApi<ShopNetWorkApi>()
                .applyRefund(body.header(rKey), body.body(rKey))
                .onSuccess {
                    "申请已提交".toast()
                    invoiceLiveData.postValue("success")

                }
                .onWithMsgFailure {
                    invoiceLiveData.postValue("fail")
                    it?.toast()
                }
        })
    }

    /**
     *  退款中的数据。
     * */
    fun getRefundProgress(mallMallRefundId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
//            if (!TextUtils.isEmpty(mallMallOrderId)) {
//                body["mallMallOrderId"] = mallMallOrderId
//            }
//            if (!TextUtils.isEmpty(mallMallOrderSkuId)) {
//                body["mallMallOrderSkuId"] = mallMallOrderSkuId
//            }
            body["mallMallRefundId"] = mallMallRefundId
            ApiClient.createApi<ShopNetWorkApi>()
                .refundProgress(body.header(rKey), body.body(rKey))
                .onSuccess {
                    // 组装数据
                    val list: MutableList<RefundStautsBean> = mutableListOf()
                    val onGoing = it?.refundLogMap?.ON_GOING
                    val closed = it?.refundLogMap?.CLOSED
                    val success = it?.refundLogMap?.SUCESS
                    val finish = it?.refundLogMap?.FINISH
                    if (success != null && success.size > 0) {
                        list.addAll(success)
                    }
                    if (finish != null && finish.size > 0) {
                        list.addAll(finish)
                    }
                    if (closed != null && closed.size > 0) {
                        list.addAll(closed)
                    }
                    if (onGoing != null && onGoing.size > 0) {
                        list.addAll(onGoing)
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
    fun cancelRefund(mallMallRefundId: String, block: (() -> Unit)? = null) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallMallRefundId"] = mallMallRefundId
            ApiClient.createApi<ShopNetWorkApi>()
                .cancelRefund(body.header(rKey), body.body(rKey))
                .onSuccess {
                    cancelRefundLiveData.postValue("成功")
                    block?.invoke()
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    fun getOSS() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getOSS(body.header(rKey), body.body(rKey))
                .onSuccess {
                    stsBean.value = it
                }
                .onFailure {

                }
        })
    }

    /**
     *  单个sku 退款
     * */
    fun getSingleRefund(
        orderNo: String,
        refundReason: String,
        mallMallOrderSkuId: String,
        refundMethod: String,
        refundNum: String,
        refundDescText: String = "",
        refundDescImgs: MutableList<String>
    ) {
        // 退款
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["refundType"] = "SINGLE"
            body["singleRefund"] = HashMap<String, Any>().also {
                it["mallMallOrderSkuId"] = mallMallOrderSkuId
                it["orderNo"] = orderNo
                it["refundReason"] = refundReason
                if (!TextUtils.isEmpty(refundDescText)) {
                    it["refundDescText"] = refundDescText
                }
                // 退款方式 --- 仅退款， 退货退款
                it["refundMethod"] = refundMethod
                it["refundNum"] = refundNum // 退款数量
                if (refundDescImgs.size > 0) {
                    it["refundDescImgs"] = refundDescImgs
                }

            }
            ApiClient.createApi<ShopNetWorkApi>()
                .applyRefund(body.header(rKey), body.body(rKey))
                .onSuccess {
                    "申请已提交".toast()
                    refundSingleLiveData.postValue("success")
                }
                .onWithMsgFailure {
                    refundSingleLiveData.postValue("fail")
                    it?.toast()
                }
        })
    }

    var fillInLogisticsLiveData: MutableLiveData<String> = MutableLiveData()

    /**
     *  填写物流信息
     * */
    fun fillInLogistics(
        mallMallRefundId: String?,
        logisticsCompany: String,
        logisticsNo: String,
        logisticsDescImg: MutableList<String>,
        logisticsDescText: String = ""
    ) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            mallMallRefundId?.let {
                body["mallMallRefundId"] = mallMallRefundId
            }
            body["logisticsCompany"] = logisticsCompany
            body["logisticsNo"] = logisticsNo
            if (!TextUtils.isEmpty(logisticsDescText)) {
                body["logisticsDescText"] = logisticsDescText
            }

            if (logisticsDescImg.size > 0) {
                body["logisticsDescImg"] = logisticsDescImg
            }
            ApiClient.createApi<ShopNetWorkApi>()
                .fillInLogistics(body.header(rKey), body.body(rKey))
                .onSuccess {
                    fillInLogisticsLiveData.postValue("success")
                }
                .onWithMsgFailure {
                    fillInLogisticsLiveData.postValue("fail")
                    it?.toast()
                }
        })
    }

    var refundorderItemLiveData: MutableLiveData<OrderItemBean> = MutableLiveData()
    fun getOrderDetail(orderNo: String, showLoading: Boolean = false) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["orderNo"] = orderNo
            val randomKey = getRandomKey()
            ApiClient.createApi<ShopNetWorkApi>()
                .orderDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    refundorderItemLiveData.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    var refundMultipleBean = MutableLiveData<ArrayList<RefundProgressMultipleBean>?>()

    fun getOrderMultiple(orderNo: String, mallOrderSkuId: String?) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["orderNo"] = orderNo
            mallOrderSkuId?.let {
                body["mallOrderSkuId"] = it
            }
            ApiClient.createApi<ShopNetWorkApi>()
                .getProgressList(body.header(rKey), body.body(rKey))
                .onSuccess {data->
                    data?.forEach {
                        // 组装数据
                        val list: MutableList<RefundStautsBean> = mutableListOf()
                        val onGoing = it.refundLogMap.ON_GOING
                        val closed = it.refundLogMap.CLOSED
                        val success = it.refundLogMap.SUCESS
                        val finish = it.refundLogMap.FINISH
                        if (success != null && success.size > 0) {
                            list.addAll(success)
                        }
                        if (finish != null && finish.size > 0) {
                            list.addAll(finish)
                        }
                        if (closed != null && closed.size > 0) {
                            list.addAll(closed)
                        }
                        if (onGoing != null && onGoing.size > 0) {
                            list.addAll(onGoing)
                        }

                        it.refundList = list
                    }
                    refundMultipleBean.value = data
                }
                .onWithMsgFailure {
                    refundMultipleBean.value = null
                    it?.toast()
                }
        })
    }

}