package com.changanford.shop.ui.order.request

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.shop.api.ShopNetWorkApi
import java.math.BigDecimal


class GetInvoiceViewModel : BaseViewModel() {


    var deleteShoppingCar: MutableLiveData<String> = MutableLiveData()

    fun getUserInvoiceAdd(addressId:String,invoiceHeader:String,invoiceHeaderName:String,invoiceRmb:String,mallMallOrderId:String,mallMallOrderNo:String,taxpayerIdentifier:String="",memo:String="") {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            body["addressId"] =addressId
            body["invoiceHeader"]=invoiceHeader
            body["invoiceHeaderName"]=invoiceHeaderName
            body["invoiceRmb"]=invoiceRmb
            body["mallMallOrderId"]=mallMallOrderId
            body["mallMallOrderNo"]=mallMallOrderNo
            if(!TextUtils.isEmpty(taxpayerIdentifier)){
                body["taxpayerIdentifier"]=taxpayerIdentifier
            }
            if(!TextUtils.isEmpty(memo)){
                body["memo"]= memo
            }

            ApiClient.createApi<ShopNetWorkApi>()
                .userInvoiceAdd(body.header(rKey), body.body(rKey))
                .onSuccess {
                    if (it != null) {

                    }
                }
                .onWithMsgFailure {
                    it?.toast()
                }
        })
    }




}