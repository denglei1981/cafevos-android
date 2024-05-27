package com.changanford.shop.ui.order.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.bean.InvoiceDetails


class GetInvoiceViewModel : BaseViewModel() {


    var invoiceLiveData: MutableLiveData<String> = MutableLiveData()

    var invoiceDetailsLiveData: MutableLiveData<InvoiceDetails> = MutableLiveData()
    fun getUserInvoiceAdd(
        addressId: String,
        invoiceHeader: String,
        invoiceHeaderName: String,
        invoiceRmb: String,
        mallMallOrderId: String,
        mallMallOrderNo: String,
        email: String,
        phone: String,
        taxpayerIdentifier: String = "",
        memo: String = ""
    ) {
        launch(true, block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
//            body["addressId"] =addressId
            body["invoiceHeader"] = invoiceHeader
            body["invoiceHeaderName"] = invoiceHeaderName
            body["invoiceRmb"] = invoiceRmb
            body["email"] = email
            if (phone.isNotEmpty()) {
                body["phone"] = phone
            }
            body["mallMallOrderId"] = mallMallOrderId
            body["mallMallOrderNo"] = mallMallOrderNo
            if (!TextUtils.isEmpty(taxpayerIdentifier)) {
                body["taxpayerIdentifier"] = taxpayerIdentifier
            }
            if (!TextUtils.isEmpty(memo)) {
                body["memo"] = memo
            }

            ApiClient.createApi<ShopNetWorkApi>()
                .userInvoiceAdd(body.header(rKey), body.body(rKey))
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
     *  查看发票详情
     * */
    fun getUserInvoiceDetail(mallMallOrderNo: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["mallMallOrderNo"] = mallMallOrderNo

            ApiClient.createApi<ShopNetWorkApi>()
                .userInvoiceDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    invoiceDetailsLiveData.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    var addressList: MutableLiveData<ArrayList<AddressBeanItem>?> = MutableLiveData()

    /**
     * 获取地址列表
     * */
    fun getAddressList() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .getAddressList(body.header(rKey), body.body(rKey))
                .onSuccess {
                    addressList.postValue(it)
                }
                .onWithMsgFailure {
                    it?.toast()
                }

        })
    }


}